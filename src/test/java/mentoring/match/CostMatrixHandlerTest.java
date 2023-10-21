package mentoring.match;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

final class CostMatrixHandlerTest implements TestFramework<CostMatrixHandlerTest.CostMatrixHandlerArgs>{

    @Override
    public Stream<CostMatrixHandlerArgs> argumentsSupplier() {
        return Stream.of(
                new CostMatrixHandlerArgs("handler without necessary criterion", 
                        new int[][]{{7,5,3},{11,8,5},{15,11,7},{19,14,9},{23,17,11}}, 
                        List.of(1,2,3,4,5), List.of(3,2,1),
                        List.of((mentee, mentor) -> mentee*mentor, 
                                (mentee, mentor) -> mentee + mentor)),
                new NecessaryCostMatrixHandlerArgs("handler with necessary criterion", 
                        new int[][]{{11,9,7,5,3}, {17,14,11,8,5}, {23,19,15,11,7}},
                        new boolean[][]{{true, true, true, true, false},
                            {false, false, false, false, false},
                            {true, true, false, true, true}},
                        List.of(1,2,3), List.of(5,4,3,2,1),
                        List.of((mentee, mentor) -> mentee*mentor,
                                (mentee, mentor) -> mentee+mentor),
                        List.of((mentee, mentor) -> mentee != 2,
                                (mentee, mentor) -> !mentee.equals(mentor))
                ));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor(){
        return test("constructor yields the correct cost matrix", args -> {
            CostMatrixHandler<Integer,Integer> matrixHandler = args.convert();
            assertMatricesAsExpected(args.expectedCostMatrix, args.expectedAllowedMatchMatrix, 
                    matrixHandler);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> isMatchAllowed(){
        boolean[][] expectedResult = new boolean[][]{{true,false,false},{false,true,false}};
        Stream<CostMatrixHandlerArgs> testCase = Stream.of(
                new NecessaryCostMatrixHandlerArgs("specific test case", null, null,
                        List.of(0,1), List.of(0,1,2),
                        List.of((mentee, mentor) -> 0),
                        List.of((mentee, mentor) -> expectedResult[mentee][mentor])
                ));
        return test(testCase, "isMatchAllowed() yields the correct result", args -> {
            CostMatrixHandler<Integer, Integer> matrixHandler = args.convert();
            boolean[][] actualResult = new boolean[args.mentees.size()][args.mentors.size()];
            for (int i = 0; i < actualResult.length; i++){
                for (int j = 0; j < actualResult.length; j++){
                    actualResult[i][j] = matrixHandler.isMatchAllowed(i, j);
                }
            }
            Assertions.assertArrayEquals(expectedResult, actualResult);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> builderFailsOnInvalidCriteria(){
        Stream<CostMatrixHandlerArgs> testCase = Stream.of(
                new CostMatrixHandlerArgs("handler without necessary criterion", null, 
                        List.of(1,2,3), List.of(3,2,1),
                        List.of((mentee, mentor) -> mentee == 2 ? -1 : 1)),
                new NecessaryCostMatrixHandlerArgs("handler with necessary criterion", null, null,
                        List.of(1,2,3), List.of(3,2,1),
                        List.of((mentee, mentor) -> -1),
                        List.of((mentee, mentor) -> mentee == 2)
                ));
        return test(testCase, "builder fails on integer overflow", args -> {
            Assertions.assertThrows(IllegalStateException.class, 
                    () -> args.convert());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> builderFailsOnOverflow(){
        Stream<CostMatrixHandlerArgs> testCase = Stream.of(
                new CostMatrixHandlerArgs("handler without necessary criterion", null, 
                        List.of(1,2,3), List.of(3,2,1),
                        List.of((mentee, mentor) -> 3,
                                (mentee, mentor) -> mentee == 2 ? Integer.MAX_VALUE : 1)),
                new NecessaryCostMatrixHandlerArgs("handler with necessary criterion", null, null,
                        List.of(1,2,3), List.of(3,2,1),
                        List.of((mentee, mentor) -> Integer.MAX_VALUE,
                                (mentee, mentor) -> Integer.MAX_VALUE),
                        List.of((mentee, mentor) -> mentee == 2)
                ));
        return test(testCase, "builder fails on integer overflow", args -> {
            Assertions.assertThrows(IllegalStateException.class, 
                    () -> args.convert());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> solveCostMatrixSolvesMatrix(){
        DummySolver solver = new DummySolver();
        return test("solveCostMatrix() solves cost matrix", args -> {
            Assertions.assertEquals(solver.expectedResult, 
                    args.convert().solveCostMatrix(solver));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> solveCostMatrixDoesNotModifyMatrix(){
        DummySolver solver = new DummySolver();
        return test("solveCostMatrix() does not modify matrix", args -> {
            CostMatrixHandler<Integer,Integer> matrixHandler = args.convert();
            matrixHandler.solveCostMatrix(solver);
            assertMatricesAsExpected(args.expectedCostMatrix, args.expectedAllowedMatchMatrix,
                    matrixHandler);
        });
    }
    
    static void assertMatricesAsExpected(int[][] expectedCostMatrix, 
            boolean[][] expectedAllowedMatrix, CostMatrixHandler<?,?> matrixHandler){
        int[][] actualCostMatrix = extractCostMatrix(matrixHandler,
                expectedCostMatrix.length, expectedCostMatrix[0].length);
        boolean[][] actualAllowedMatchMatrix = extractAllowedMatchMatrix(matrixHandler,
                expectedAllowedMatrix.length, expectedCostMatrix[0].length);
       Assertions.assertAll(
               () -> Assertions.assertArrayEquals(expectedCostMatrix, actualCostMatrix),
               () -> Assertions.assertArrayEquals(expectedAllowedMatrix, actualAllowedMatchMatrix));
    }
    
    static int[][] extractCostMatrix(CostMatrixHandler<?,?> matrixHandler, int nRows, int nCols){
        int[][] result = new int[nRows][nCols];
        for(int i = 0; i < nRows; i++){
            for(int j = 0; j < nCols; j++){
                result[i][j] = matrixHandler.getMatchScore(i, j);
            }
        }
        return result;
    }
    
    static boolean[][] extractAllowedMatchMatrix(CostMatrixHandler<?,?> matrixHandler, int nRows, 
            int nCols){
        boolean[][] result = new boolean[nRows][nCols];
        for(int i = 0; i < nRows; i++){
            for(int j = 0; j < nCols; j++){
                result[i][j] = matrixHandler.isMatchAllowed(i, j);
            }
        }
        return result;
    }
    
    static class CostMatrixHandlerArgs extends TestArgs{
        final List<Integer> mentees;
        final List<Integer> mentors;
        final Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria;
        final int[][] expectedCostMatrix;
        final boolean[][] expectedAllowedMatchMatrix;
        
        CostMatrixHandlerArgs(String testCase, int[][] expectedCostMatrix, 
                boolean[][] expectedAllowedMatchMatrix,
                List<Integer> mentees, List<Integer> mentors,
                Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria){
            super(testCase);
            this.mentees = mentees;
            this.mentors = mentors;
            this.progressiveCriteria = progressiveCriteria;
            this.expectedCostMatrix = expectedCostMatrix;
            if(expectedAllowedMatchMatrix == null){
                if(expectedCostMatrix == null){
                    this.expectedAllowedMatchMatrix = null;
                } else {
                    boolean[][] allowedMatchMatrix = 
                            new boolean[expectedCostMatrix.length][];
                    for (int i = 0; i < allowedMatchMatrix.length; i ++){
                        boolean[] allTrue = new boolean[expectedCostMatrix[i].length];
                        Arrays.fill(allTrue, true);
                        allowedMatchMatrix[i] = allTrue;
                    }
                    this.expectedAllowedMatchMatrix = allowedMatchMatrix;
                }
            } else {
                this.expectedAllowedMatchMatrix = expectedAllowedMatchMatrix;
            }
        }
        
        CostMatrixHandlerArgs(String testCase, int[][] expectedCostMatrix, 
                List<Integer> mentees, List<Integer> mentors,
                Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria){
            this(testCase, expectedCostMatrix, null, mentees, mentors, progressiveCriteria);
        }
        
        CostMatrixHandler<Integer, Integer> convert(){
            return new CostMatrixHandler<>(mentees, mentors, progressiveCriteria);
        }
    }
    
    static class NecessaryCostMatrixHandlerArgs extends CostMatrixHandlerArgs{
        private final List<NecessaryCriterion<Integer, Integer>> necessaryCriteria;
        
        NecessaryCostMatrixHandlerArgs(String testCase, int[][] expectedCostMatrix,
                boolean[][] expectedAllowedMatchMatrix,
                List<Integer> mentees, List<Integer> mentors,
                Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria,
                List<NecessaryCriterion<Integer, Integer>> necessaryCriteria){
            super(testCase, expectedCostMatrix, expectedAllowedMatchMatrix, 
                    mentees, mentors, progressiveCriteria);
            this.necessaryCriteria = necessaryCriteria;
        }
        
        @Override
        CostMatrixHandler<Integer, Integer> convert(){
            CostMatrixHandler<Integer, Integer> result = super.convert();
            result.withNecessaryCriteria(necessaryCriteria);
            return result;
        }
    }
}