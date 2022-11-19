package mentoring.configuration;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.configuration.CriteriaToolbox.Letter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class CriteriaToolboxTest implements TestFramework<Object>{
    
    static final String INVALID_LETTER = "K";
    
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
    Stream<DynamicNode> computeMapSetProximity(){
        int multiplier = CriteriaToolbox.SET_PROXIMITY_MULTIPLIER;
        Set<Integer> set = Set.of(0,1);
        return test(Stream.of(
                new SetMapArgs("no element", multiplier, set, Map.of(), 1),
                new SetMapArgs("one non-common element", multiplier*2, 
                        set, Map.of(2,0),1),
                new SetMapArgs("one common element", (int) (multiplier * 0.8), 
                        set, Map.of(1,0), 0.2),
                new SetMapArgs("two non-common elements", (int) (multiplier * 1.4), 
                        set, Map.of(2,0,3,1),0.4),
                new SetMapArgs("two partially common elements", (int) (multiplier * 1.2), 
                        set, Map.of(0,1,2,0),0.6),
                new SetMapArgs("two common elements", (int) (multiplier * 0.2), 
                        set, Map.of(0,0,1,1),0.8),
                new SetMapArgs("three partially common elements", (int) (multiplier * 1.14285714285), 
                        set, Map.of(0,2,1,1,2,0),1)),
                "computeWeightedAsymetricMapDistance() returns the expected value", 
                args -> Assertions.assertEquals(args.expected, 
                        CriteriaToolbox.computeWeightedAsymetricMapDistance(args.map, args.set, 
                                args.spikeFactor)));
    }
    
    @TestFactory
    Stream<DynamicNode> isValidPrefix_validInput(){
        return test(Arrays.stream(Letter.values()), "Letter.isValidPrefix() on valid input", args ->
                    Assertions.assertTrue(Letter.isValidPrefix(args.prefix)));
    }
    
    @TestFactory
    Stream<DynamicNode> isValidPrefix_invalidInput(){
        return test(Stream.of(INVALID_LETTER), "Letter.isValidPrefix() on invalid input", args ->
                Assertions.assertFalse(Letter.isValidPrefix(args)));
    }
    
    @TestFactory
    Stream<DynamicNode> getOffset_validInput(){
        return test(Arrays.stream(Letter.values()), "Letter.getOffset() on valid input", args ->
                Assertions.assertEquals(args.offset, Letter.getOffset(args.prefix)));
    }
    
    @TestFactory
    Stream<DynamicNode> getOffset_invalidInput(){
        return test(Stream.of(INVALID_LETTER), "Letter.getOffset() on invalid input", args ->
                Assertions.assertThrows(NullPointerException.class, () -> Letter.getOffset(args)));
    }

    @TestFactory
    Stream<DynamicNode> getYear_validInput() {
        return test(YearArgs.FullSupplier(), "getYear() on valid input", args ->
                Assertions.assertEquals(args.expected,
                        CriteriaToolbox.getYear(args.formatted, YearArgs.CURRENT_YEAR)));
    }
    
    @TestFactory
    Stream<DynamicNode> getYear_OneArgForm(){
        return test(YearArgs.CompleteYearArgsSupplier(), "getYear() one-arg form on valid input", 
                args -> Assertions.assertEquals(args.expected,
                        CriteriaToolbox.getYear(args.formatted)));
    }
    
    @TestFactory
    Stream<DynamicNode> getYear_invalidInput(){
        return test(Stream.of(
                new YearArgs("invalid letter", 0, INVALID_LETTER + "6234"),
                new YearArgs("no digits", 0, "BCD"),
                new YearArgs("non-digits char after prefix", 0, "X2O12")), // O vs 0
                "getYear() on invalid input", args ->
                    Assertions.assertThrows(IllegalArgumentException.class, 
                            () -> CriteriaToolbox.getYear(args.formatted)));
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
    
    static class DoubleSetArgs extends TestArgs{
        final int expected;
        final Set<Integer> first;
        final Set<Integer> second;
        
        public DoubleSetArgs(String testCase, int expected, Set<Integer> first, 
                Set<Integer> second) {
            super(testCase);
            this.expected = expected;
            this.first = first;
            this.second = second;
        }
    }
    
    static class SetMapArgs extends TestArgs{
        final int expected;
        final Set<Integer> set;
        final Map<Integer, Integer> map;
        final double spikeFactor;

        public SetMapArgs(String testCase, int expected, Set<Integer> first, 
                Map<Integer, Integer> second, double spikeFactor) {
            super(testCase);
            this.expected = expected;
            this.set = first;
            this.map = second;
            this.spikeFactor = spikeFactor;
        }
    }
    
    static class YearArgs extends TestArgs{
        final int expected;
        final String formatted;
        
        public YearArgs(String testCase, int expected, String formatted){
            super(testCase);
            this.expected = expected;
            this.formatted = formatted;
        }
        
        static final int CURRENT_YEAR = 7468;
        
        static Stream<YearArgs> CompleteYearArgsSupplier(){
            int executiveOffset = CriteriaToolbox.Letter.EXECUTIVE.offset;
            return Stream.of(new YearArgs("four-digit string", 1992, "1992"),
                    new YearArgs("standard letter and five-digit string", 65216, "X65216"),
                    new YearArgs("executive and four-digit string", 1234+executiveOffset, "e1234"),
                    new YearArgs("space after prefix", 1992, "x   1992"));
        }

        static Stream<YearArgs> partialYearsArgsSupplier(){
            int executiveOffset = CriteriaToolbox.Letter.EXECUTIVE.offset;
            return Stream.of(new YearArgs("two-digit string of current century", 7412, "12"),
                    new YearArgs("letter and three-digit string of current millenium", 
                            7398, "x398"),
                    new YearArgs("letter and two-digit string of past century", 
                            7382+executiveOffset, "E82"),
                    new YearArgs("letter and leading zeroes", 7001, "X001"));
        }
        
        static Stream<YearArgs> FullSupplier(){
            return Stream.concat(CompleteYearArgsSupplier(), partialYearsArgsSupplier());
        }
    }
    
    static class DistanceArgs extends TestArgs{
        final Map<String, Integer> indices;
        final String first;
        final String second;
        final int baseValue;
        final int expectedResult;
        
        DistanceArgs(String testCase, Map<String, Integer> indices, String first, String second, 
                int baseValue, int expectedResult) {
            super(testCase);
            this.indices = indices;
            this.first = first;
            this.second = second;
            this.baseValue = baseValue;
            this.expectedResult = expectedResult;
        }
        
        static Stream<DistanceArgs> argsSupplier(){
            return Stream.of(new DistanceArgs("equal input", Map.of("", 1), "", "", 2, 1),
                    new DistanceArgs("zero base value", Map.of("",1,"a",2),"","a",0,0),
                    new DistanceArgs("one-off distance", Map.of("a",1,"b",2),"a","b",2,2),
                    new DistanceArgs("more-than-one-off distance", Map.of("a",0,"b",3),"a","b",3,27));
        }
    }
    
}
