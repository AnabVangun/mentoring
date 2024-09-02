package mentoring.configuration;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class CriteriaToolboxTest implements TestFramework<Object>{
    @Override
    public Stream<Object> argumentsSupplier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @TestFactory
    Stream<DynamicNode> logicalNotAOrB() {
        return test(Stream.of(new DoubleBooleanArgs(true, true, true),
                new DoubleBooleanArgs(true, false, true), 
                new DoubleBooleanArgs(false, true, false),
                new DoubleBooleanArgs(true, false, false)), 
                "logicalNotAOrB()", args ->
                Assertions.assertEquals(args.expected, 
                        CriteriaToolbox.logicalNotAOrB(args.first, args.second)));
    }

    @TestFactory
    Stream<DynamicNode> computeSetProximity() {
        int multiplier = CriteriaToolbox.SET_PROXIMITY_MULTIPLIER;
        Set<Integer> first = Set.of(0,1);
        return test(Stream.of(
                new DoubleSetArgs("no element", multiplier*2, first, Set.of()),
                new DoubleSetArgs("one non-common element", multiplier*3, first, Set.of(2)),
                new DoubleSetArgs("one common element", multiplier/2, first, Set.of(1)),
                new DoubleSetArgs("two non-common elements", multiplier*4, first, Set.of(2,3)),
                new DoubleSetArgs("two partially common elements", multiplier, first, Set.of(0,2)),
                new DoubleSetArgs("two common elements", 0, first, first),
                new DoubleSetArgs("three partially common elements", multiplier*1/3, first, Set.of(0,1,2))),
                "computeSetProximity(): value and symetry", args -> {
                    Assertions.assertAll(
                            () -> Assertions.assertEquals(args.expected, 
                                    CriteriaToolbox.computeSetDistance(args.first, args.second)),
                            () -> Assertions.assertEquals(
                                    CriteriaToolbox.computeSetDistance(args.first, args.second), 
                                    CriteriaToolbox.computeSetDistance(args.second, args.first)));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> computePreferenceMapSimilarityScore_correctOutput(){
        return test(Stream.of(
                new TwoMapsArgs("same first property", 102, Map.of(2, 1), Map.of(2,2,5,6),
                        100, 1, 12),
                new TwoMapsArgs("multiple common property", 28, Map.of(2,1,5,2,6,4),
                        Map.of(6,5,5,8), 10, 1, 3),
                new TwoMapsArgs("no common property", 38, Map.of(-5,1,0,2),
                        Map.of(12,3),10,2, 5),
                new TwoMapsArgs("empty from", -38, Map.of(), Map.of(2,1), 25, 6, -2),
                new TwoMapsArgs("empty to", 375, Map.of(1,2), Map.of(), 25, 2, 150),
                new TwoMapsArgs("both maps empty", 93, Map.of(), Map.of(), 25, 6, 3)),
                "computePreferenceMapSimilarityScore() returns the expected result", args ->
                        Assertions.assertEquals(args.expected,
                                CriteriaToolbox.computePreferenceMapSimilarityScore(args.from, 
                                        args.to, args.fromFactor, args.toFactor, 
                                        args.defaultValue)));
    }
    
    @TestFactory
    Stream<DynamicNode> exponentialDistance_correctOuput(){
        return test(DistanceArgs.argsSupplier(),
                "exponentialDistance() returns expected result", args -> 
                    Assertions.assertEquals(args.expectedResult, 
                            CriteriaToolbox.exponentialDistance(args.indices, args.first, 
                                    args.second, args.baseValue)));
    }
    @TestFactory
    Stream<DynamicNode> exponentialDistance_symmetry(){
        return test(DistanceArgs.argsSupplier(),
                "exponentialDistance() is symmetric", args -> 
                    Assertions.assertEquals(CriteriaToolbox.exponentialDistance(args.indices, args.first, 
                                    args.second, args.baseValue),
                            CriteriaToolbox.exponentialDistance(args.indices, args.second, 
                                    args.first, args.baseValue)));
    }
    
    @TestFactory
    Stream<DynamicNode> exponentialDistance_invalidInput(){
        return test(Stream.of(new DistanceArgs("invalid input", Map.of(), "", "b", 0, 0)),
                "exponentialDistance() throws an exception on invalid input", args ->
                    Assertions.assertThrows(RuntimeException.class, () -> 
                            CriteriaToolbox.exponentialDistance(args.indices, args.first, 
                                    args.second, args.baseValue)));
    }
    
    static class DoubleBooleanArgs extends TestArgs {
        final boolean expected;
        final boolean first;
        final boolean second;
        
        public DoubleBooleanArgs(boolean expected, boolean first, boolean second) {
            super("Combining " + first + " and " + second + " to yield " + expected);
            this.expected = expected;
            this.first = first;
            this.second = second;
        }
    }
    
    static record DoubleSetArgs(String testCase, int expected, Set<Integer> first, 
        Set<Integer> second){
        
        @Override
        public String toString(){
            return testCase;
        }
    }
    
    static record TwoMapsArgs(String testCase, int expected, Map<Integer, Integer> from, 
        Map<Integer, Integer> to, int fromFactor, int toFactor, int defaultValue) {
        
        @Override
        public String toString(){
            return testCase;
        }
    }
    
    static record DistanceArgs(String testCase, Map<String, Integer> indices, String first, 
        String second, int baseValue, int expectedResult) {
        
        @Override
        public String toString(){
            return testCase;
        }
        
        static Stream<DistanceArgs> argsSupplier(){
            return Stream.of(new DistanceArgs("equal input", Map.of("", 1), "", "", 2, 1),
                    new DistanceArgs("zero base value", Map.of("",1,"a",2),"","a",0,0),
                    new DistanceArgs("one-off distance", Map.of("a",1,"b",2),"a","b",2,2),
                    new DistanceArgs("more-than-one-off distance", Map.of("a",0,"b",3),"a","b",3,27));
        }
    }
    
}
