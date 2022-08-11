package mentoring.match;

import assignmentproblem.hungariansolver.HungarianSolver;
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
                        new int[][]{{11,9,7,5,MatchesBuilder.PROHIBITIVE_VALUE},
                            {MatchesBuilder.PROHIBITIVE_VALUE, MatchesBuilder.PROHIBITIVE_VALUE, 
                                MatchesBuilder.PROHIBITIVE_VALUE, MatchesBuilder.PROHIBITIVE_VALUE, 
                                MatchesBuilder.PROHIBITIVE_VALUE},
                            {23,19,MatchesBuilder.PROHIBITIVE_VALUE,11,7}}, 
                        List.of(1,2,3), List.of(5,4,3,2,1),
                        List.of((mentee, mentor) -> mentee*mentor,
                                (mentee, mentor) -> mentee+mentor),
                        List.of((mentee, mentor) -> mentee != 2,
                                (mentee, mentor) -> !mentee.equals(mentor))
                ));
    }
    
    @TestFactory
    Stream<DynamicNode> isMatchNotProhibitiveFailsBeforeBuildingMatrix(){
        return test("isMatchNotProhibitive() fails before buildCostMatrix()", args -> {
            CostMatrixHandler<Integer, Integer> matrixHandler = args.convert();
            Assertions.assertThrows(NullPointerException.class, 
                    () -> matrixHandler.isMatchScoreNotProhibitive(0, 0));
        });
    }
    @TestFactory
    Stream<DynamicNode> getMatchScoreFailsBeforeBuildingMatrix(){
        return test("getMatchScore() fails before buildCostMatrix()", args -> {
            CostMatrixHandler<Integer, Integer> matrixHandler = args.convert();
            Assertions.assertThrows(NullPointerException.class, 
                    () -> matrixHandler.getMatchScore(0, 0));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> buildCostMatrix(){
        return test("buildCostMatrix() yields the correct cost matrix", args -> {
            CostMatrixHandler<Integer,Integer> matrixHandler = args.convert().buildCostMatrix();
            assertCostMatrixEquals(args.expectedCostMatrix, matrixHandler);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> isMatchScoreNotProhibitive(){
        boolean[][] expectedResult = new boolean[][]{{true,false,false},{false,true,false}};
        Stream<CostMatrixHandlerArgs> testCase = Stream.of(
                new NecessaryCostMatrixHandlerArgs("specific test case", null, 
                        List.of(0,1), List.of(0,1,2),
                        List.of((mentee, mentor) -> 0),
                        List.of((mentee, mentor) -> expectedResult[mentee][mentor])
                ));
        return test(testCase, "isMatchScoreNotProhibitive() yields the correct result", args -> {
            CostMatrixHandler<Integer, Integer> matrixHandler = args.convert().buildCostMatrix();
            boolean[][] actualResult = new boolean[args.mentees.size()][args.mentors.size()];
            for (int i = 0; i < actualResult.length; i++){
                for (int j = 0; j < actualResult.length; j++){
                    actualResult[i][j] = matrixHandler.isMatchScoreNotProhibitive(i, j);
                }
            }
            Assertions.assertArrayEquals(expectedResult, actualResult);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> failToBuildCostMatrixOnInvalidCriteria(){
        Stream<CostMatrixHandlerArgs> testCase = Stream.of(
                new CostMatrixHandlerArgs("handler without necessary criterion", null, 
                        List.of(1,2,3), List.of(3,2,1),
                        List.of((mentee, mentor) -> mentee == 2 ? -1 : 1)),
                new NecessaryCostMatrixHandlerArgs("handler with necessary criterion", null, 
                        List.of(1,2,3), List.of(3,2,1),
                        List.of((mentee, mentor) -> -1),
                        List.of((mentee, mentor) -> mentee == 2)
                ));
        return test(testCase, "buildCostMatrix() fails on integer overflow", args -> {
            CostMatrixHandler<Integer, Integer> matrixHandler = args.convert();
            Assertions.assertThrows(IllegalStateException.class, 
                    () -> matrixHandler.buildCostMatrix());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> failToBuildCostMatrixOnOverflow(){
        Stream<CostMatrixHandlerArgs> testCase = Stream.of(
                new CostMatrixHandlerArgs("handler without necessary criterion", null, 
                        List.of(1,2,3), List.of(3,2,1),
                        List.of((mentee, mentor) -> 3,
                                (mentee, mentor) -> mentee == 2 ? Integer.MAX_VALUE : 1)),
                new NecessaryCostMatrixHandlerArgs("handler with necessary criterion", null, 
                        List.of(1,2,3), List.of(3,2,1),
                        List.of((mentee, mentor) -> Integer.MAX_VALUE,
                                (mentee, mentor) -> Integer.MAX_VALUE),
                        List.of((mentee, mentor) -> mentee == 2)
                ));
        return test(testCase, "buildCostMatrix() fails on integer overflow", args -> {
            CostMatrixHandler<Integer, Integer> matrixHandler = args.convert();
            Assertions.assertThrows(IllegalStateException.class, 
                    () -> matrixHandler.buildCostMatrix());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> solveCostMatrixSolvesMatrix(){
        DummySolver solver = new DummySolver();
        return test("solveCostMatrix() solves cost matrix", args -> {
            Assertions.assertEquals(solver.expectedResult, 
                    args.convert().buildCostMatrix().solveCostMatrix(solver));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> solveCostMatrixDoesNotModifyMatrix(){
        DummySolver solver = new DummySolver();
        return test("solveCostMatrix() does not modify matrix", args -> {
            CostMatrixHandler<Integer,Integer> matrixHandler = args.convert().buildCostMatrix();
            matrixHandler.solveCostMatrix(solver);
            assertCostMatrixEquals(args.expectedCostMatrix, matrixHandler);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> failToSolveUninitialisedCostMatrix(){
        return test("solveCostMatrix() fails if called before buildCostMatrix()", args -> {
            CostMatrixHandler<Integer,Integer> matrixHandler = args.convert();
            //TODO here, we should rightly expect a RuntimeException but we suffer from an error in assignmentproblem
            Assertions.assertThrows(RuntimeException.class, 
                    () -> matrixHandler.solveCostMatrix(new HungarianSolver(-1)));
        });
    }
    
    static void assertCostMatrixEquals(int[][] expected, CostMatrixHandler<?,?> matrixHandler){
        int[][] actualCostMatrix = extractCostMatrix(matrixHandler,
                expected.length, expected[0].length);
       Assertions.assertArrayEquals(expected, actualCostMatrix);
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
    
    static class CostMatrixHandlerArgs extends TestArgs{
        final List<Integer> mentees;
        final List<Integer> mentors;
        final Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria;
        final int[][] expectedCostMatrix;
        
        CostMatrixHandlerArgs(String testCase, int[][] expectedCostMatrix,
                List<Integer> mentees, List<Integer> mentors,
                Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria){
            super(testCase);
            this.mentees = mentees;
            this.mentors = mentors;
            this.progressiveCriteria = progressiveCriteria;
            this.expectedCostMatrix = expectedCostMatrix;
        }
        
        CostMatrixHandler<Integer, Integer> convert(){
            return new CostMatrixHandler<>(mentees, mentors, progressiveCriteria);
        }
    }
    
    static class NecessaryCostMatrixHandlerArgs extends CostMatrixHandlerArgs{
        private final List<NecessaryCriterion<Integer, Integer>> necessaryCriteria;
        
        NecessaryCostMatrixHandlerArgs(String testCase, int[][] expectedCostMatrix,
                List<Integer> mentees, List<Integer> mentors,
                Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria,
                List<NecessaryCriterion<Integer, Integer>> necessaryCriteria){
            super(testCase, expectedCostMatrix, mentees, mentors, progressiveCriteria);
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