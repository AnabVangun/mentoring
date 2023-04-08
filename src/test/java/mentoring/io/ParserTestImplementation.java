package mentoring.io;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.io.ParserTestImplementation.DummyParser;
import mentoring.io.ParserTestImplementation.DummyParserArgs;
import mentoring.io.datareader.YamlReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class ParserTestImplementation implements ParserTest<String, DummyParser, DummyParserArgs>{
    
    @Override
    public Stream<DummyParserArgs> argumentsSupplier() {
        return Stream.of(new DummyParserArgs("foo case", false, "foo"),
                new DummyParserArgs("bar case", false, "bar"));
    }

    @Override
    public Stream<DummyParserArgs> specificallyInvalidArgumentsSupplier() {
        return Stream.of();
    }
    
    @Override
    public Stream<DummyParserArgs> genericallyInvalidArgumentsSupplier(){
        return Stream.of(new DummyParserArgs("invalid case", true, ""));
    }

    @Override
    public DummyParser prepareParser() {
        return new DummyParser(false, List.of("foo", "bar"));
    }
    
    @TestFactory
    Stream<DynamicNode> extractAttribute_fullForm_expectedValue(){
        return test(Stream.of(Map.of("key", 2)), 
                "extractAttribute() (full form) returns the expected value", 
                args -> Assertions.assertEquals(Integer.valueOf(2), 
                        Parser.extractAttribute(args, "key", Integer.class)));
    }
    
    @TestFactory
    Stream<DynamicNode> extractAttribute_fullForm_missingProperty(){
        return test(Stream.of(Map.of("key", 3)), 
                "extractAttribute() (full form) throws an exception when the property is missing", 
                args -> Assertions.assertThrows(IllegalArgumentException.class,
                        () -> Parser.extractAttribute(args, "missing", Integer.class)));
    }
    
    @TestFactory
    Stream<DynamicNode> extractAttribute_partialForm_expectedValue(){
        return test(Stream.of(Map.of("key", "foo")), 
                "extractAttribute() (partial form) returns the expected value",
                args -> Assertions.assertEquals("foo", Parser.extractAttribute(args, "key")));
    }
    
    @TestFactory
    Stream<DynamicNode> extractAttribute_partialForm_missingProperty(){
        return test(Stream.of(Map.of("key", "bar")), 
                "extractAttribute() (partial form) throws an exception when the property is missing",
                args -> Assertions.assertThrows(IllegalArgumentException.class,
                        () -> Parser.extractAttribute(args, "missing")));
    }
    
    @TestFactory
    Stream<DynamicNode> extractAttributeList_expectedValue(){
        return test(Stream.of(Map.of("key", List.of("first", "second"))),
                "extractAttributeList() returns the expected value",
                args -> Assertions.assertEquals(List.of("first", "second"), 
                        Parser.extractAttribute(args, "key")));
    }
    
    @TestFactory
    Stream<DynamicNode> extractAttributeList_missingProperty(){
        return test(Stream.of(Map.of("key", 2)),
                "extractAttributeList() throws an exception when the property is missing",
                args -> Assertions.assertThrows(IllegalArgumentException.class,
                        () -> Parser.extractAttributeList(args, "missing", Integer.class)));
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
            throw new UnsupportedOperationException("not needed");
        }
        
        @Override
        protected List<String> registerSpecificErrors(Map<String, Object> data){
            return new ArrayList<>();
        }

        @Override
        protected Set<String> getExpectedKeys(){
            throw new UnsupportedOperationException("not needed");
        }
    }
    
    static record DummyParserArgs(String testCase, boolean fail, String result) 
            implements ParserTest.ParserArgs<String, DummyParser>{

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
        
        @Override
        public Map<String, Object> getData() {
            return Map.of();
        }
        
        @Override
        public int getExpectedSpecificErrorsCount(){
            return 0;
        }
    }
}
