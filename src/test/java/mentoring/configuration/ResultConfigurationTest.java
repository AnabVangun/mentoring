package mentoring.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfigurationTest.ResultConfigurationTestArgs;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ResultConfigurationTest implements TestFramework<ResultConfigurationTestArgs>{
    
    @Override
    public Stream<ResultConfigurationTestArgs> argumentsSupplier() {
        @SuppressWarnings("unchecked")
        Function<Match<String,String>, String[]> arrayFormatter = Mockito.mock(Function.class);
        Mockito.when(arrayFormatter.apply(Mockito.any()))
                .thenReturn(new String[]{"first value", "second value"});
        @SuppressWarnings("unchecked")
        Function<Match<String, String>, Map<String, String>> mapFormatter = 
                Mockito.mock(Function.class);
        Mockito.when(mapFormatter.apply(Mockito.any()))
                .thenReturn(Map.of("first", "first value", "second", "second value"));
        List<String> headers = List.of("first", "second");
        return Stream.of(
                new ArrayResultConfigurationTestArgs("array-specialised configuration", 
                        headers, arrayFormatter),
                new MapResultConfigurationTestArgs("map-specialised configuration", 
                        headers, mapFormatter));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("unique test case"), "createXX() throws an NPE on null input", args ->{
            Class<NullPointerException> NPE = NullPointerException.class;
            Assertions.assertAll(
                    () -> Assertions.assertThrows(NPE, () -> ResultConfiguration
                            .createForArrayLine(null, List.of(), input -> new String[0])),
                    () -> Assertions.assertThrows(NPE, () -> ResultConfiguration
                            .createForArrayLine("foo", null, input -> new String[0])),
                    () -> Assertions.assertThrows(NPE, () -> ResultConfiguration
                            .createForArrayLine("foo", List.of(), null)),
                    () -> Assertions.assertThrows(NPE, () -> ResultConfiguration
                            .createForMapLine(null, List.of(), input -> Map.of())),
                    () -> Assertions.assertThrows(NPE, () -> ResultConfiguration
                            .createForMapLine("foo", null, input -> Map.of())),
                    () -> Assertions.assertThrows(NPE, () -> ResultConfiguration
                            .createForMapLine("foo", List.of(), null))
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getResultHeader_expectedValues(){
        return test("getResultHeader() returns the expected value", args -> {
            Assertions.assertEquals(args.expectedHeader, args.convert().getResultHeader());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getResultHeader_protectedFromModifications(){
        return test("getResultHeader() returns a value protected from modifications", 
                args -> Assertions.assertThrows(UnsupportedOperationException.class,
                        () -> args.convert().getResultHeader().set(0, "new foo")));
    }
    
    @TestFactory
    Stream<DynamicNode> getResultLine(){
        return test("getResultLine() returns the expected value", args -> {
            String[] expectedResult = new String[]{"first value", "second value"};
            Assertions.assertArrayEquals(expectedResult, 
                    args.convert().getResultLine(generateMatch()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getResultMap(){
        return test("getResultMap() returns the expected value", args -> {
            Map<String, String> expectedResult = Map.of("first", "first value", 
                    "second", "second value");
            Assertions.assertEquals(expectedResult, args.convert().getResultMap(generateMatch()));
        });
    }
    
    private static Match<String, String> generateMatch(){
        return new MatchTest.MatchArgs("foo", "bar", "foobar", 0)
                .convertAs(String.class, String.class);
    }
    
    static abstract class ResultConfigurationTestArgs extends TestArgs {
        final List<String> expectedHeader;
        
        protected ResultConfigurationTestArgs(String testCase, List<String> expectedHeader){
            super(testCase);
            this.expectedHeader = expectedHeader;
        }
        
        abstract ResultConfiguration<String, String> convert();
    }
    
    static class ArrayResultConfigurationTestArgs extends ResultConfigurationTestArgs{
        @SuppressWarnings("unchecked")
        final Function<Match<String, String>, String[]> formatter;

        public ArrayResultConfigurationTestArgs(String testCase, List<String> expectedHeader,
                Function<Match<String, String>, String[]> formatter) {
            super(testCase, expectedHeader);
            this.formatter = formatter;
        }
        
        @Override
        ResultConfiguration<String, String> convert(){
            return ResultConfiguration.createForArrayLine(toString(), expectedHeader, formatter);
        }
    }
    
    static class MapResultConfigurationTestArgs extends ResultConfigurationTestArgs{
        @SuppressWarnings("unchecked")
        final Function<Match<String, String>, Map<String,String>> formatter;

        public MapResultConfigurationTestArgs(String testCase, List<String> expectedHeader,
                Function<Match<String, String>, Map<String,String>> formatter) {
            super(testCase, expectedHeader);
            this.formatter = formatter;
        }
        
        @Override
        ResultConfiguration<String, String> convert(){
            return ResultConfiguration.createForMapLine(toString(), expectedHeader, formatter);
        }
    } 
}
