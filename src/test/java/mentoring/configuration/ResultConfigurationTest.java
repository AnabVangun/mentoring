package mentoring.configuration;

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
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @TestFactory
    Stream<DynamicNode> getResultMap(){
        return test(Stream.of(new ResultConfigurationTestArgs("unique test case", 
                List.of("first", "second"))),
                "getResultMap() returns the expected value", args -> {
                    Mockito.when(args.mockFormatter.apply(Mockito.any()))
                            .thenReturn(new String[]{"first value", "second value"});
                    ResultConfiguration<String, String> configuration = args.convert();
                    Assertions.assertEquals(Map.of("first", "first value", "second", "second value"),
                            configuration.getResultMap(
                                    new MatchTest.MatchArgs("foo", "bar", "foobar", 0)
                                            .convertAs(String.class, String.class)));
        });
    }
    
    static class ResultConfigurationTestArgs extends TestArgs{
        final List<String> expectedHeader;
        @SuppressWarnings("unchecked")
        final Function<Match<String, String>, String[]> mockFormatter = Mockito.mock(Function.class);

        public ResultConfigurationTestArgs(String testCase, List<String> expectedHeader) {
            super(testCase);
            this.expectedHeader = expectedHeader;
        }
        
        ResultConfiguration<String, String> convert(){
            return ResultConfiguration.create(toString(), expectedHeader, mockFormatter);
        }
    } 
}
