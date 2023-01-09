package mentoring.io.datareader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.io.datareader.DataReaderTest.DataReaderArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

interface DataReaderTest<T extends DataReader, U extends DataReaderArgs<T>> 
        extends TestFramework<U>{
    
    Stream<U> invalidArgumentsSupplier();
    
    @TestFactory
    default Stream<DynamicNode> parse_validInput(){
        return test("parse() returns the expected object on valid input", args ->
            Assertions.assertEquals(args.expectedResult, args.convert()));
    }
    
    @TestFactory
    default Stream<DynamicNode> parse_reuseReader(){
        Assertions.assertTrue(argumentsSupplier().count() > 1, 
                () -> "argumentsSupplier() returns only %s result, at least 2 are needed for "
                        + "this test.".formatted(argumentsSupplier().count()));
        T reader = prepareReader();
        return test(Stream.of("specific test case"), 
                "parse() returns the expected result when the reader is reused", 
                args -> Assertions.assertAll(argumentsSupplier().map(testCase -> () -> {
                    try {
                        Assertions.assertEquals(testCase.expectedResult, 
                                reader.read(testCase.getDataSource()));
                    } catch (IOException e){
                        Assertions.fail("An exception was raised", e);
                    }
        })));
    }
    
    T prepareReader();
    
    @TestFactory
    default Stream<DynamicNode> parse_invalidInput(){
        return test(invalidArgumentsSupplier(), 
                "parse() returns the expected exception on invalid input", args ->
                        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> args.convertWithException()));
    }
    
    abstract static class DataReaderArgs<T extends DataReader>{
        protected final String filePath;
        protected final Map<String, Object> expectedResult;
        
        protected DataReaderArgs(String filePath, Map<String, Object> expectedResult){
            this.filePath = filePath;
            this.expectedResult = expectedResult;
        }
        
        @Override
        public String toString(){
            return filePath;
        }
        
        Map<String, Object> convert(){
            try {
                return convertWithException();
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }
        
        Map<String, Object> convertWithException() throws IOException{
            return getReaderUnderTest().read(getDataSource());
        }
        
        protected abstract T getReaderUnderTest();
        
        Reader getDataSource() throws IOException{
            return new FileReader(getClass().getResource(filePath).getFile(), 
                    Charset.forName("utf-8"));
        }
    }
}
