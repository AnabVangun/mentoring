package mentoring.datastructure;

import java.util.Arrays;
import java.util.stream.Stream;
import mentoring.datastructure.Year.Curriculum;
import mentoring.datastructure.YearTest.YearArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class YearTest implements TestFramework<YearArgs> {
    static final String INVALID_CURRICULUM = "K";
    
    @Override
    public Stream<YearArgs> argumentsSupplier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @TestFactory
    Stream<DynamicNode> isValidPrefix_validInput(){
        return test(Arrays.stream(Curriculum.values()), "Curriculum.isValidPrefix() on valid input", args ->
                    Assertions.assertTrue(Curriculum.isValidPrefix(args.prefix)));
    }
    
    @TestFactory
    Stream<DynamicNode> isValidPrefix_invalidInput(){
        return test(Stream.of(INVALID_CURRICULUM), "Curriculum.isValidPrefix() on invalid input", args ->
                Assertions.assertFalse(Curriculum.isValidPrefix(args)));
    }
    
    @TestFactory
    Stream<DynamicNode> getOffset_validInput(){
        return test(Arrays.stream(Curriculum.values()), "Curriculum.getOffset() on valid input", args ->
                Assertions.assertEquals(args.offset, Curriculum.getOffset(args.prefix)));
    }
    
    @TestFactory
    Stream<DynamicNode> getOffset_invalidInput(){
        return test(Stream.of(INVALID_CURRICULUM), "Curriculum.getOffset() on invalid input", args ->
                Assertions.assertThrows(NullPointerException.class, () -> Curriculum.getOffset(args)));
    }

    @TestFactory
    Stream<DynamicNode> getYear_validInput() {
        return test(YearArgs.FullSupplier(), "getYear() on valid input", args ->
                args.assertEqualToExpectedYear(Year.getYear(args.input, YearArgs.CURRENT_YEAR)));
    }
    
    @TestFactory
    Stream<DynamicNode> getYear_OneArgForm(){
        return test(YearArgs.CompleteYearArgsSupplier(), "getYear() one-arg form on valid input", 
                args -> args.assertEqualToExpectedYear(Year.getYear(args.input)));
    }
    
    @TestFactory
    Stream<DynamicNode> getYear_invalidInput(){
        return test(Stream.of(
                new YearArgs("invalid curriculum", null, 0, 0, INVALID_CURRICULUM + "6234"),
                new YearArgs("no digits", null, 0, 0, "BCD"),
                new YearArgs("non-digits char after prefix", null, 0, 0, "X2O12")), // O vs 0
                "getYear() on invalid input", args ->
                    Assertions.assertThrows(IllegalArgumentException.class, 
                            () -> Year.getYear(args.input)));
    }
    
    
    static record YearArgs (String testCase, Curriculum expectedCurriculum, int expectedEntryYear,
        int expectedNormalizedYear, String input) {
        
        static final int CURRENT_YEAR = 7468;
        
        @Override
        public String toString(){
            return this.testCase;
        }
        
        void assertEqualToExpectedYear(Year actual){
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedCurriculum, actual.getCurriculum()),
                    () -> Assertions.assertEquals(expectedEntryYear, actual.getEntryYear()),
                    () -> Assertions.assertEquals(expectedNormalizedYear, actual.getNormalizedYear()));
        }
        
        static Stream<YearArgs> CompleteYearArgsSupplier(){
            int executiveOffset = Curriculum.EXECUTIVE.offset;
            return Stream.of(new YearArgs("four-digit string", Curriculum.INGENIEUR, 1992, 
                            1992, "1992"),
                    new YearArgs("standard letter and five-digit string", Curriculum.INGENIEUR,
                            65216, 65216, "X65216"),
                    new YearArgs("executive and four-digit string", Curriculum.EXECUTIVE, 
                            1234, 1234+executiveOffset, "e1234"),
                    new YearArgs("space after prefix", Curriculum.INGENIEUR, 1992, 1992, "x   1992"));
        }

        static Stream<YearArgs> partialYearsArgsSupplier(){
            int executiveOffset = Curriculum.EXECUTIVE.offset;
            return Stream.of(new YearArgs("two-digit string of current century", Curriculum.INGENIEUR, 
                            7412, 7412, "12"),
                    new YearArgs("letter and three-digit string of current millenium", 
                            Curriculum.INGENIEUR, 7398, 7398, "x398"),
                    new YearArgs("letter and two-digit string of past century", Curriculum.EXECUTIVE,
                            7382, 7382+executiveOffset, "E82"),
                    new YearArgs("letter and leading zeroes", Curriculum.INGENIEUR, 7001, 7001, "X001"));
        }
        
        static Stream<YearArgs> FullSupplier(){
            return Stream.concat(CompleteYearArgsSupplier(), partialYearsArgsSupplier());
        }
    }
}
