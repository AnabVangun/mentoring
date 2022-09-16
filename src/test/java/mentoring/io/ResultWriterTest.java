package mentoring.io;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfiguration;
import mentoring.io.ResultWriterTest.ResultWriterArgs;
import mentoring.match.Matches;
import mentoring.match.MatchesTest.MatchesArgs;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ResultWriterTest implements TestFramework<ResultWriterArgs> {

    @Override
    public Stream<ResultWriterArgs> argumentsSupplier() {
        return Stream.of(ResultWriterArgs.ONE, ResultWriterArgs.TWO, ResultWriterArgs.THREE);
    }
    
    @TestFactory
    @SuppressWarnings("unchecked")
    Stream<DynamicNode> constructor_npe(){
        return test(Stream.of((ResultConfiguration) null), 
                "ResultWriter() fails on null input", args ->
                        Assertions.assertThrows(NullPointerException.class, 
                                () -> new ResultWriter<>(args)));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor(){
        return test(Stream.of(ResultWriterArgs.ONE), "ResultWriter() succeeds on valid input", 
                args -> new ResultWriter<>(args.getConfiguration()));
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_npe(){
        return test(Stream.of(new ResultWriterArgs("null matches", "", null), 
                new ResultWriterArgs("null writer", "", 
                        new MatchesArgs(List.of(Pair.of("a","b"))).convert(), null)),
                "writeMatches() throws NPE on null input", args -> {
                    ResultWriter<String, String> writer = new ResultWriter<>(args.getConfiguration());
                    Assertions.assertThrows(NullPointerException.class, () -> 
                            writer.writeMatches(args.input, args.destination)
                    );
                });
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_expectedResult(){
        return test("writeMatches() returns the expected result", args -> {
            ResultWriter<String, String> writer = new ResultWriter<>(args.getConfiguration());
            writer.writeMatches(args.input, args.destination);
            Assertions.assertEquals(args.expectedResult, args.destination.toString());
        });
    }
    
    static class ResultWriterArgs extends TestArgs {
        private final ResultConfiguration<String, String> mock;
        private final String expectedResult;
        private final Matches<String, String> input;
        private final Writer destination;
        
        private ResultWriterArgs(String testCase, String expectedResult, 
                Matches<String, String> input) {
            this(testCase, expectedResult, input, new StringWriter());
        }
        
        @SuppressWarnings("unchecked")
        private ResultWriterArgs(String testCase, String expectedResult,
                Matches<String, String> input, Writer destination){
            super(testCase);
            mock = Mockito.mock(ResultConfiguration.class);
            Mockito.when(mock.getResultHeader()).thenReturn(new String[]{"1","2"});
            Mockito.when(mock.getResultLine(Mockito.any())).thenReturn(new String[]{"3","4"},
                    new String[]{"5","6"}, new String[]{"7","8"});
            this.expectedResult = expectedResult;
            this.input = input;
            this.destination = destination;
        }
        
        static final ResultWriterArgs ONE = new ResultWriterArgs("One match", 
                "\"1\",\"2\"\n\"3\",\"4\"\n",
                new MatchesArgs(List.of(Pair.of("a","b"))).convert());
        static final ResultWriterArgs TWO = new ResultWriterArgs("Two matches", 
                "\"1\",\"2\"\n\"3\",\"4\"\n\"5\",\"6\"\n",
                new MatchesArgs(List.of(Pair.of("a","b"), Pair.of("c","d"))).convert());
        static final ResultWriterArgs THREE = new ResultWriterArgs("Three matches", 
                "\"1\",\"2\"\n\"3\",\"4\"\n\"5\",\"6\"\n\"7\",\"8\"\n",
                new MatchesArgs(List.of(Pair.of("a","b"), Pair.of("c","d"), Pair.of("e","f")))
                        .convert());
        
        ResultConfiguration<String, String> getConfiguration(){
            return mock;
        }
    }
}
