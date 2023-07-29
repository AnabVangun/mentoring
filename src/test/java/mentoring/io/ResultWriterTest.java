package mentoring.io;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfiguration;
import mentoring.io.ResultWriterTest.ResultWriterArgs;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesTest.MatchesArgs;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
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
        ResultWriterArgs arg = ResultWriterArgs.ONE;
        return test(Stream.of(arg), "ResultWriter() succeeds on valid input", 
                args -> new ResultWriter<>(args.getConfiguration()));
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> writeMatches_npe(){
        return test(Stream.of(new ResultWriterArgs("null matches", "", null, true), 
                new ResultWriterArgs("null writer", "", 
                        new MatchesArgs<>(List.of(Pair.of("a","b"))).convert(), true, null)),
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
            writer.writeMatches(args.input, args.destination, args.writeHeader);
            Assertions.assertEquals(args.expectedResult, args.destination.toString());
        });
    }
    
    static class ResultWriterArgs extends TestArgs {
        private final ResultConfiguration<String, String> configuration;
        private final String expectedResult;
        private final Matches<String, String> input;
        private final Writer destination;
        private final boolean writeHeader;
        
        private ResultWriterArgs(String testCase, String expectedResult, 
                Matches<String, String> input, boolean writeHeader) {
            this(testCase, expectedResult, input, writeHeader, new StringWriter());
        }
        
        @SuppressWarnings("unchecked")
        private ResultWriterArgs(String testCase, String expectedResult,
                Matches<String, String> input, boolean writeHeader, Writer destination){
            super(testCase);
            Iterator<String[]> results = List.of(new String[]{"3","4"}, new String[]{"5","6"}, 
                    new String[]{"7","8"}).iterator();
            Function<Match<String, String>, String[]> lineFormatter = match -> results.next();
            configuration = ResultConfiguration.createForArrayLine("ResultWriterArgs configuration", 
                    List.of("1","2"), lineFormatter);
            this.expectedResult = expectedResult;
            this.input = input;
            this.writeHeader = writeHeader;
            this.destination = destination;
        }
        
        static final ResultWriterArgs ONE = new ResultWriterArgs("One match", 
                "\"1\",\"2\"\n\"3\",\"4\"\n",
                new MatchesArgs<>(List.of(Pair.of("a","b"))).convert(), true);
        static final ResultWriterArgs TWO = new ResultWriterArgs("Two matches", 
                "\"1\",\"2\"\n\"3\",\"4\"\n\"5\",\"6\"\n",
                new MatchesArgs<>(List.of(Pair.of("a","b"), Pair.of("c","d"))).convert(), true);
        static final ResultWriterArgs THREE = new ResultWriterArgs("Three matches", 
                "\"3\",\"4\"\n\"5\",\"6\"\n\"7\",\"8\"\n",
                new MatchesArgs<>(List.of(Pair.of("a","b"), Pair.of("c","d"), Pair.of("e","f")))
                        .convert(),
                false);
        
        ResultConfiguration<String, String> getConfiguration(){
            return configuration;
        }
    }
}
