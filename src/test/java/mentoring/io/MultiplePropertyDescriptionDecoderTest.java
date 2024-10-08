package mentoring.io;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.datastructure.IndexedPropertyDescription;
import mentoring.datastructure.MultiplePropertyDescription;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyDescription;
import mentoring.io.MultiplePropertyDescriptionDecoderTest.MultiplePropertyDescriptionDecoderArgs;
import mentoring.io.PropertyDescriptionDecoderTest.PropertyDescriptionDecoderArgs;

class MultiplePropertyDescriptionDecoderTest extends PropertyDescriptionDecoderTest<MultiplePropertyDescription<?,?>, 
        MultiplePropertyDescriptionDecoder, MultiplePropertyDescriptionDecoderArgs>{

    @Override
    Stream<MultiplePropertyDescriptionDecoderArgs> specificallyInvalidArgumentsSupplier() {
        return Stream.of(new MultiplePropertyDescriptionDecoderArgs("unexpected attribute property", 
                List.of(Map.of("name", "name value", "headerName", "headerName value",
                                "type", "integer", "aggregation", "aggregation value", 
                                "wrong property", "wrong property value", 
                                "second wrong property", "wrong value")),
                        2));
    }

    @Override
    protected Stream<MultiplePropertyDescriptionDecoderArgs> genericallyInvalidArgumentsSupplier() {
        return Stream.of(new MultiplePropertyDescriptionDecoderArgs("missing attribute property", 
                        List.of(Map.of("name", "name value")), 0));
    }

    @Override
    public Stream<MultiplePropertyDescriptionDecoderArgs> argumentsSupplier() {
        return Stream.of(new MultiplePropertyDescriptionDecoderArgs("valid test case",
                        List.of(
                                Map.of("name", "int property", "headerName", "int header", 
                                        "type", "integer", "aggregation", "set"),
                                Map.of("name", "boolean property", "headerName", "boolean header",
                                        "type", "boolean", "aggregation", "indexed")),
                        List.of(new SetPropertyDescription<>("int property", "int header", PropertyType.INTEGER),
                                new IndexedPropertyDescription<>("boolean property", "boolean header", 
                                        PropertyType.BOOLEAN))));
    }
    
    static class MultiplePropertyDescriptionDecoderArgs extends 
            PropertyDescriptionDecoderArgs<MultiplePropertyDescription<?,?>, MultiplePropertyDescriptionDecoder>{

        MultiplePropertyDescriptionDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                List<MultiplePropertyDescription<?, ?>> expectedProperties) {
            super(testCase, multipleInput, expectedProperties);
        }
        
        MultiplePropertyDescriptionDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                int specificErrorsExpectedCount) {
            super(testCase, multipleInput, specificErrorsExpectedCount);
        }

        @Override
        MultiplePropertyDescriptionDecoder convert() {
            return new MultiplePropertyDescriptionDecoder();
        }
    }
}
