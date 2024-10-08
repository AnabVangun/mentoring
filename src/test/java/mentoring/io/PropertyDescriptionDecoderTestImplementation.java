package mentoring.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mentoring.datastructure.PropertyDescription;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SimplePropertyDescription;
import mentoring.io.PropertyDescriptionDecoderTest.PropertyDescriptionDecoderArgs;
import mentoring.io.PropertyDescriptionDecoderTestImplementation.DummyPropertyDescriptionDecoder;
import mentoring.io.PropertyDescriptionDecoderTestImplementation.DummyPropertyDescriptionDecoderArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;

class PropertyDescriptionDecoderTestImplementation extends PropertyDescriptionDecoderTest<PropertyDescription<?>, 
        DummyPropertyDescriptionDecoder, DummyPropertyDescriptionDecoderArgs> {

    @Override
    Stream<DummyPropertyDescriptionDecoderArgs> specificallyInvalidArgumentsSupplier() {
        return Stream.of(new SpecificallyFailingDummyPropertyDescriptionDecoderArgs("specific errors", 2));
    }

    @Override
    protected Stream<DummyPropertyDescriptionDecoderArgs> genericallyInvalidArgumentsSupplier() {
        return Stream.of(new SpecificallyValidDummyPropertyDescriptionDecoderArgs(
                "missing attribute property", List.of(Map.of("foo","bar")),
                Set.of("barfoo")));
    }

    @Override
    public Stream<DummyPropertyDescriptionDecoderArgs> argumentsSupplier() {
        return Stream.of(new SpecificallyValidDummyPropertyDescriptionDecoderArgs(
                "valid property", List.of(Map.of("foo","bar")), 
                List.of(new SimplePropertyDescription<>("foo", "bar", PropertyType.BOOLEAN)), 
                Set.of("foo")));
    }
    
    @TestFactory
    Stream<DynamicNode> registerUnexpectedAttributeError_validInput() {
        return test("registerUnexpectedAttributeError() does not modify list if the input is valid",
                args -> {
                    PropertyDescriptionDecoder<PropertyDescription<?>> decoder = args.convert();
                    List<String> errors = List.of();
                    Assertions.assertDoesNotThrow(
                            () -> decoder.registerUnexpectedAttributeError(args.singleInput, errors),
                            "Decoder tried to register error by mistake.");
                });
    }
    
    @TestFactory
    Stream<DynamicNode> registerUnexpectedAttributeError_invalidInput() {
        return test(Stream.of(
                new SpecificallyValidDummyPropertyDescriptionDecoderArgs(
                        "unexpected attribute property", 
                        List.of(Map.of("key not found", "value not found", "foo","bar")),
                        Set.of("barfoo"))),
                "registerUnexpectedAttributeError() adds new errors if the input is invalid",
                args -> {
                    PropertyDescriptionDecoder<PropertyDescription<?>> decoder = args.convert();
                    List<String> errors = Mockito.mock(AbstractStringList.class);
                    decoder.registerUnexpectedAttributeError(args.singleInput, errors);
                    Mockito.verify(errors, Mockito.times(2)).add(Mockito.anyString());
                    Mockito.verify(errors, Mockito.never()).clear();
                });
    }
    
    abstract static class AbstractStringList implements List<String>{
    }
    
    static class DummyPropertyDescriptionDecoder extends PropertyDescriptionDecoder<PropertyDescription<?>>{
        private final int specificErrorCount;
        private final Set<String> expectedAttributeNames;
        private final Iterator<PropertyDescription<?>> expectedPropertyDescriptions;

        DummyPropertyDescriptionDecoder(int specificErrorCount, Set<String> expectedAttributeNames, 
                Iterator<PropertyDescription<?>> expectedPropertyDescriptions) {
            this.specificErrorCount = specificErrorCount;
            this.expectedAttributeNames = expectedAttributeNames;
            this.expectedPropertyDescriptions = expectedPropertyDescriptions;
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
        protected PropertyDescription<?> decodeSinglePropertyDescription(
                Map<? extends String, ? extends String> toDecode) {
            return expectedPropertyDescriptions.next();
        }
    }
    
    static abstract class DummyPropertyDescriptionDecoderArgs extends 
            PropertyDescriptionDecoderArgs<PropertyDescription<?>, DummyPropertyDescriptionDecoder>{
        
        public DummyPropertyDescriptionDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                List<PropertyDescription<?>> expectedProperties) {
            super(testCase, multipleInput, expectedProperties);
        }
        
        public DummyPropertyDescriptionDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                int specificErrorsExpectedCount) {
            super(testCase, multipleInput, specificErrorsExpectedCount);
        }
    }
    
    static class SpecificallyFailingDummyPropertyDescriptionDecoderArgs 
            extends DummyPropertyDescriptionDecoderArgs{

        public SpecificallyFailingDummyPropertyDescriptionDecoderArgs(String testCase,
                int specificErrorsExpectedCount) {
            super(testCase, List.of(Map.of()), specificErrorsExpectedCount);
        }

        @Override
        DummyPropertyDescriptionDecoder convert() {
            return new DummyPropertyDescriptionDecoder(specificErrorsExpectedCount, Set.of(), null);
        }
    }
    
    static class SpecificallyValidDummyPropertyDescriptionDecoderArgs
            extends DummyPropertyDescriptionDecoderArgs{
        private final Set<String> expectedAttributeNames;
        private final static List<PropertyDescription<?>> defaultProperties = 
                List.of(new SimplePropertyDescription<>("foo","bar",PropertyType.YEAR));

        public SpecificallyValidDummyPropertyDescriptionDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                List<PropertyDescription<?>> expectedProperties, 
                Set<String> expectedAttributeNames) {
            super(testCase, multipleInput, expectedProperties);
            this.expectedAttributeNames = expectedAttributeNames;
        }
        
        public SpecificallyValidDummyPropertyDescriptionDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput,
                Set<String> expectedAttributeNames) {
            super(testCase, multipleInput, defaultProperties);
            this.expectedAttributeNames = expectedAttributeNames;
        }

        @Override
        DummyPropertyDescriptionDecoder convert() {
            return new DummyPropertyDescriptionDecoder(specificErrorsExpectedCount, expectedAttributeNames,
                    expectedProperties.iterator());
        }
    }
}
