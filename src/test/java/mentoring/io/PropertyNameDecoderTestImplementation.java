package mentoring.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SimplePropertyName;
import mentoring.io.PropertyNameDecoderTest.PropertyNameDecoderArgs;
import mentoring.io.PropertyNameDecoderTestImplementation.DummyPropertyNameDecoder;
import mentoring.io.PropertyNameDecoderTestImplementation.DummyPropertyNameDecoderArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;

class PropertyNameDecoderTestImplementation extends PropertyNameDecoderTest<PropertyName<?>, 
        DummyPropertyNameDecoder, DummyPropertyNameDecoderArgs> {

    @Override
    Stream<DummyPropertyNameDecoderArgs> specificallyInvalidArgumentsSupplier() {
        return Stream.of(new SpecificallyFailingDummyPropertyNameDecoderArgs("specific errors", 2));
    }

    @Override
    protected Stream<DummyPropertyNameDecoderArgs> genericallyInvalidArgumentsSupplier() {
        return Stream.of(new SpecificallyValidDummyPropertyNameDecoderArgs(
                "missing attribute property", List.of(Map.of("foo","bar")),
                Set.of("barfoo")));
    }

    @Override
    public Stream<DummyPropertyNameDecoderArgs> argumentsSupplier() {
        return Stream.of(new SpecificallyValidDummyPropertyNameDecoderArgs(
                "valid property", List.of(Map.of("foo","bar")), 
                List.of(new SimplePropertyName<>("foo", "bar", PropertyType.BOOLEAN)), 
                Set.of("foo")));
    }
    
    @TestFactory
    Stream<DynamicNode> registerUnexpectedAttributeError_validInput() {
        return test("registerUnexpectedAttributeError() does not modify list if the input is valid",
                args -> {
                    PropertyNameDecoder<PropertyName<?>> decoder = args.convert();
                    List<String> errors = List.of();
                    Assertions.assertDoesNotThrow(
                            () -> decoder.registerUnexpectedAttributeError(args.singleInput, errors),
                            "Decoder tried to register error by mistake.");
                });
    }
    
    @TestFactory
    Stream<DynamicNode> registerUnexpectedAttributeError_invalidInput() {
        return test(Stream.of(
                new SpecificallyValidDummyPropertyNameDecoderArgs(
                        "unexpected attribute property", 
                        List.of(Map.of("key not found", "value not found", "foo","bar")),
                        Set.of("barfoo"))),
                "registerUnexpectedAttributeError() adds new errors if the input is invalid",
                args -> {
                    PropertyNameDecoder<PropertyName<?>> decoder = args.convert();
                    List<String> errors = Mockito.mock(AbstractStringList.class);
                    decoder.registerUnexpectedAttributeError(args.singleInput, errors);
                    Mockito.verify(errors, Mockito.times(2)).add(Mockito.anyString());
                    Mockito.verify(errors, Mockito.never()).clear();
                });
    }
    
    abstract static class AbstractStringList implements List<String>{
    }
    
    static class DummyPropertyNameDecoder extends PropertyNameDecoder<PropertyName<?>>{
        private final int specificErrorCount;
        private final Set<String> expectedAttributeNames;
        private final Iterator<PropertyName<?>> expectedPropertyNames;

        DummyPropertyNameDecoder(int specificErrorCount, Set<String> expectedAttributeNames, 
                Iterator<PropertyName<?>> expectedPropertyNames) {
            this.specificErrorCount = specificErrorCount;
            this.expectedAttributeNames = expectedAttributeNames;
            this.expectedPropertyNames = expectedPropertyNames;
        }
        
        @Override
        protected List<String> registerSpecificErrors(
                Map<? extends String, ? extends String> toValidate) {
            return IntStream.range(0, specificErrorCount).mapToObj(Integer::toString)
                    .collect(Collectors.toCollection(() -> new ArrayList<>()));
        }

        @Override
        protected Set<String> getExpectedAttributeNames() {
            return expectedAttributeNames;
        }

        @Override
        protected PropertyName<?> decodeSinglePropertyName(
                Map<? extends String, ? extends String> toDecode) {
            return expectedPropertyNames.next();
        }
    }
    
    static abstract class DummyPropertyNameDecoderArgs extends 
            PropertyNameDecoderArgs<PropertyName<?>, DummyPropertyNameDecoder>{
        
        public DummyPropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                List<PropertyName<?>> expectedProperties) {
            super(testCase, multipleInput, expectedProperties);
        }
        
        public DummyPropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                int specificErrorsExpectedCount) {
            super(testCase, multipleInput, specificErrorsExpectedCount);
        }
    }
    
    static class SpecificallyFailingDummyPropertyNameDecoderArgs 
            extends DummyPropertyNameDecoderArgs{

        public SpecificallyFailingDummyPropertyNameDecoderArgs(String testCase,
                int specificErrorsExpectedCount) {
            super(testCase, List.of(Map.of()), specificErrorsExpectedCount);
        }

        @Override
        DummyPropertyNameDecoder convert() {
            return new DummyPropertyNameDecoder(specificErrorsExpectedCount, Set.of(), null);
        }
    }
    
    static class SpecificallyValidDummyPropertyNameDecoderArgs
            extends DummyPropertyNameDecoderArgs{
        private final Set<String> expectedAttributeNames;
        private final static List<PropertyName<?>> defaultProperties = 
                List.of(new SimplePropertyName<>("foo","bar",PropertyType.YEAR));

        public SpecificallyValidDummyPropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                List<PropertyName<?>> expectedProperties, 
                Set<String> expectedAttributeNames) {
            super(testCase, multipleInput, expectedProperties);
            this.expectedAttributeNames = expectedAttributeNames;
        }
        
        public SpecificallyValidDummyPropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput,
                Set<String> expectedAttributeNames) {
            super(testCase, multipleInput, defaultProperties);
            this.expectedAttributeNames = expectedAttributeNames;
        }

        @Override
        DummyPropertyNameDecoder convert() {
            return new DummyPropertyNameDecoder(specificErrorsExpectedCount, expectedAttributeNames,
                    expectedProperties.iterator());
        }
    }
}
