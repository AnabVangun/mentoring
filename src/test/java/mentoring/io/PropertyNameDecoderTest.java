package mentoring.io;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.datastructure.PropertyName;
import mentoring.io.PropertyNameDecoderTest.PropertyNameDecoderArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

abstract class PropertyNameDecoderTest<T extends PropertyName<?>, U extends PropertyNameDecoder<T>, 
        V extends PropertyNameDecoderArgs<T, U>> implements TestFramework<V>{
    
    /**
     * Generate a stream of arguments that would generate specific errors in the decoder. 
     * Only the first property in each {@code PropertyNameDecoderArgs} input is used.
     * @return arguments such that {@link PropertyNameDecoder#registerSpecificErrors(java.util.Map) }
     * returns a non-empty list.
     */
    abstract Stream<V> specificallyInvalidArgumentsSupplier();
    
    /**
     * Generate a stream of arguments that would generate generic errors in the decoder.
     * @return arguments such that {@link PropertyNameDecoder#decodePropertyNames(java.lang.Iterable) }
     * raises an exception but {@link PropertyNameDecoder#registerSpecificErrors(java.util.Map) } 
     * returns an empty list.
     */
    protected abstract Stream<V> genericallyInvalidArgumentsSupplier();
    
    /**
     * Generate a stream of arguments that would generate errors in the decoder.
     * @return arguments such that {@link PropertyNameDecoder#decodePropertyNames(java.lang.Iterable) }
     * raises an exception.
     */
    final Stream<V> invalidArgumentsSupplier(){
        return Stream.concat(genericallyInvalidArgumentsSupplier(), 
                specificallyInvalidArgumentsSupplier());
    }
    
    final Stream<V> allArgsSupplier(){
        return Stream.concat(argumentsSupplier(), invalidArgumentsSupplier());
    }
    
    @TestFactory
    Stream<DynamicNode> decodePropertyNames_validInput(){
        return test("decodePropertyNames() returns the expected properties on a valid input",
                args -> {
                    Set<T> properties = args.convert().decodePropertyNames(args.multipleInput);
                    Set<T> expectedProperties = Set.copyOf(args.expectedProperties);
                    Assertions.assertEquals(expectedProperties, properties);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> decodePropertyNames_invalidInput(){
        return test(invalidArgumentsSupplier(), 
                "decodePropertyNames() throws the expected exception on an invalid input",
                args -> {
                    U decoder = args.convert();
                    Assertions.assertThrows(IllegalArgumentException.class, 
                            () -> decoder.decodePropertyNames(args.multipleInput));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> registerSpecificErrors_modifiableList(){
        return test(allArgsSupplier(), "registerSpecificErrors() returns a modifiable list",
                args -> {
                    U decoder = args.convert();
                    List<String> errors = decoder.registerSpecificErrors(args.singleInput);
                    Assertions.assertTrue(errors.add("new value"), 
                            () -> "Could not add element to list " + errors);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> registerSpecificErrors_asManyResultsAsExpected(){
        return test(allArgsSupplier(),
                "registerSpecificErrors() returns a list containing exactly as many items as expected",
                args -> {
                    List<String> errors = args.convert().registerSpecificErrors(args.singleInput);
                    Assertions.assertEquals(args.specificErrorsExpectedCount, errors.size(),
                            () -> "Wrong number of errors in list " + errors);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> decodeSinglePropertyName_expectedResult(){
        return test("decodeSinglePropertyName() returns the expected property", args -> {
            T actualProperty = args.convert().decodeSinglePropertyName(args.singleInput);
            Assertions.assertEquals(args.expectedSingleProperty, actualProperty);
        });
    }
    
    abstract static class PropertyNameDecoderArgs<E extends PropertyName<?>, 
            F extends PropertyNameDecoder<E>> extends TestArgs{
        
        protected final Map<? extends String, ? extends String> singleInput;
        protected final int specificErrorsExpectedCount;
        protected final List<Map<? extends String, ? extends String>> multipleInput;
        protected final List<E> expectedProperties;
        protected final E expectedSingleProperty;
        
        private PropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput,
                int specificErrorsExpectedCount, List<E> expectedProperties) {
            super(testCase);
            this.multipleInput = multipleInput;
            singleInput = multipleInput.iterator().next();
            this.specificErrorsExpectedCount = specificErrorsExpectedCount;
            this.expectedProperties = expectedProperties;
            expectedSingleProperty = 
                    expectedProperties == null ? null : expectedProperties.iterator().next();
        }
        
        protected PropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput,
                int specificErrorsExpectedCount) {
            this(testCase, multipleInput, specificErrorsExpectedCount, null);
        }
        
        protected PropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput,
                List<E> expectedProperties) {
            this(testCase, multipleInput, 0, expectedProperties);
        }
        
        abstract F convert();
    }
}
