package mentoring.io;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
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
    
    /**
     * Generate a stream of arguments that would generate specific errors in the parser.
     * @return arguments such that {@link Parser#registerSpecificErrors(java.util.Map) }
     * returns a non-empty list.
     */
    Stream<V> specificallyInvalidArgumentsSupplier();
    
    /**
     * Generate a stream of arguments that would generate generic errors in the parser.
     * @return arguments such that {@link Parser#buildObject(java.util.Map) }
     * raises an exception but {@link Parser#registerSpecificErrors(java.util.Map) } 
     * returns an empty list.
     */
    Stream<V> genericallyInvalidArgumentsSupplier();
    
    /**
     * Generate a stream of arguments that would generate errors in the decoder.
     * @return arguments such that {@link PropertyDescriptionDecoder#decodePropertyDescriptions(java.lang.Iterable) }
     * raises an exception.
     */
    default Stream<V> invalidArgumentsSupplier(){
        return Stream.concat(genericallyInvalidArgumentsSupplier(), 
                specificallyInvalidArgumentsSupplier());
    }
    
    default Stream<V> allArgsSupplier(){
        return Stream.concat(argumentsSupplier(), invalidArgumentsSupplier());
    }
    
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
    
    
    @TestFactory
    default Stream<DynamicNode> registerSpecificErrors_modifiableList(){
        return test(allArgsSupplier(), "registerSpecificErrors() returns a modifiable list",
                args -> {
                    U parser = prepareParser();
                    List<String> errors = parser.registerSpecificErrors(args.getData());
                    Assertions.assertTrue(errors.add("new value"), 
                            () -> "Could not add element to list " + errors);
                });
    }
    
    
    @TestFactory
    default Stream<DynamicNode> registerSpecificErrors_asManyResultsAsExpected(){
        return test(allArgsSupplier(),
                "registerSpecificErrors() returns a list containing exactly as many items as expected",
                args -> {
                    List<String> errors = prepareParser().registerSpecificErrors(args.getData());
                    Assertions.assertEquals(args.getExpectedSpecificErrorsCount(), errors.size(),
                            () -> "Wrong number of errors in list " + errors);
                });
    }
    
    static interface ParserArgs<T, U extends Parser<T>> {
        
        default T convert(){
            try {
                return convertWithException();
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }
        
        T convertWithException() throws IOException;
        
        U getParserUnderTest();
        
        void assertResultAsExpected(T actual);
        
        Reader getDataSource() throws IOException;
        
        Map<String, Object> getData();
        
        int getExpectedSpecificErrorsCount();
    }
}
