package mentoring.io;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.io.ParserTestImplementation.DummyParser;
import mentoring.io.ParserTestImplementation.DummyParserArgs;
import mentoring.io.datareader.YamlReader;
import org.junit.jupiter.api.Assertions;

class ParserTestImplementation implements ParserTest<String, DummyParser, DummyParserArgs>{
    
    @Override
    public Stream<DummyParserArgs> argumentsSupplier() {
        return Stream.of(new DummyParserArgs("foo case", false, "foo"),
                new DummyParserArgs("bar case", false, "bar"));
    }

    @Override
    public Stream<DummyParserArgs> invalidArgumentsSupplier() {
        return Stream.of(new DummyParserArgs("invalid case", true, ""));
    }

    @Override
    public DummyParser prepareParser() {
        return new DummyParser(false, List.of("foo", "bar"));
    }

    static class DummyParser extends Parser<String>{
        private final boolean fail;
        private final Iterator<String> results;
        
        DummyParser(boolean fail, Iterable<String> results){
            super(new YamlReader());
            this.fail = fail;
            this.results = results.iterator();
        }
        
        @Override
        public String parse(Reader reader){
            if (fail){
                throw new IllegalArgumentException("Parser made to fail");
            } else {
                return results.next();
            }
        }

        @Override
        protected String buildObject(Map<String, Object> data) {
            return null;
        }
    }
    
    static record DummyParserArgs(String testCase, boolean fail, String result) 
            implements ParserTest.ParserArgs<String, DummyParser>{

        @Override
        public String convert() {
            try {
                return convertWithException();
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public String convertWithException() throws IOException {
            return new DummyParser(fail, List.of(result)).parse(getDataSource());
        }

        @Override
        public DummyParser getParserUnderTest() {
            return new DummyParser(fail, List.of(result));
        }

        @Override
        public void assertResultAsExpected(String actual) {
            Assertions.assertEquals(result, actual);
        }

        @Override
        public Reader getDataSource() throws IOException {
            return null;
        }
        
    }
}
