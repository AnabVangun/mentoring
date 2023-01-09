package mentoring.io;

import java.io.IOException;
import java.io.Reader;
import java.util.stream.Stream;
import mentoring.io.ParserTest.ParserArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

/**
 * Extension of the test framework providing the standard tests for a Parser class.
 * @param <T> class to parse
 * @param <U> parser subclass under test
 * @param <V> helper class encapsulating the arguments and methods necessary for the test
 */
interface ParserTest<T, U extends Parser<T>, V extends ParserArgs<T,U>> extends TestFramework<V>{
    
    Stream<V> invalidArgumentsSupplier();
    
    @TestFactory
    default Stream<DynamicNode> parse_validInput(){
        return test("parse() returns the expected object on valid input", args -> 
                args.assertResultAsExpected(args.convert()));
    };
    
    @TestFactory
    default Stream<DynamicNode> parse_reuseReader(){
        Assertions.assertTrue(argumentsSupplier().count() > 1, () -> 
                "argumentsSupplier() returns only %s result, at least 2 are needed for this test"
                        .formatted(argumentsSupplier().count()));
        U parser = prepareParser();
        return test(Stream.of("specific test case"), 
                "parse() returns the expected result when the reader is reused",
                args -> Assertions.assertAll(argumentsSupplier().map(testCase -> () -> {
                    try {
                        testCase.assertResultAsExpected(parser.parse(testCase.getDataSource()));
                    } catch(IOException e){
                        Assertions.fail("An exception was raised", e);
                    }
                })));
    }
    
    U prepareParser();
    
    @TestFactory
    default Stream<DynamicNode> parse_invalidInput(){
        return test(invalidArgumentsSupplier(), 
                "parse() returns the expected exception on invalid input", args -> 
                        Assertions.assertThrows(IllegalArgumentException.class, 
                                () -> args.convertWithException()));
    }
    
    static interface ParserArgs<T, U extends Parser<T>> {
        
        T convert();
        
        T convertWithException() throws IOException;
        
        U getParserUnderTest();
        
        void assertResultAsExpected(T actual);
        
        Reader getDataSource() throws IOException;
    }
}
