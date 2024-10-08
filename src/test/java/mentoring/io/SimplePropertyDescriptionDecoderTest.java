package mentoring.io;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SimplePropertyDescription;
import mentoring.io.PropertyDescriptionDecoderTest.PropertyDescriptionDecoderArgs;
import mentoring.io.SimplePropertyDescriptionDecoderTest.SimplePropertyDescriptionDecoderArgs;

class SimplePropertyDescriptionDecoderTest extends PropertyDescriptionDecoderTest<SimplePropertyDescription<?>, 
        SimplePropertyDescriptionDecoder, SimplePropertyDescriptionDecoderArgs>{

    @Override
    Stream<SimplePropertyDescriptionDecoderArgs> specificallyInvalidArgumentsSupplier() {
        return Stream.of(
                new SimplePropertyDescriptionDecoderArgs("unexpected attribute property",
                        List.of(Map.of("name", "name value", "headerName", "headerName value",
                                "type", "integer", "wrong property", "wrong property value")),
                        1));
    }

    @Override
    protected Stream<SimplePropertyDescriptionDecoderArgs> genericallyInvalidArgumentsSupplier() {
        return Stream.of(
                new SimplePropertyDescriptionDecoderArgs("missing attribute property", 
                        List.of(Map.of("name", "name value", "type", "integer")), 0),
                new SimplePropertyDescriptionDecoderArgs(
                        "multiple properties mixing valid and invalid properties",
                        List.of(Map.of("name", "first name", "headerName", "first header", 
                                "type", "integer"),
                                Map.of("name", "second name", "type", "integer")), 
                        0));
    }

    @Override
    public Stream<SimplePropertyDescriptionDecoderArgs> argumentsSupplier() {
        return Stream.of(new SimplePropertyDescriptionDecoderArgs("valid test case",
                        List.of(
                                Map.of("name", "int property", "headerName", "int header", 
                                        "type", "integer"),
                                Map.of("name", "boolean property", "headerName", "boolean header",
                                        "type", "boolean")),
                        List.of(new SimplePropertyDescription<>("int property", "int header", PropertyType.INTEGER),
                                new SimplePropertyDescription<>("boolean property", "boolean header", 
                                        PropertyType.BOOLEAN))));
    }
    
    static class SimplePropertyDescriptionDecoderArgs extends PropertyDescriptionDecoderArgs<SimplePropertyDescription<?>, 
            SimplePropertyDescriptionDecoder>{

        SimplePropertyDescriptionDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                List<SimplePropertyDescription<?>> expectedProperties) {
            super(testCase, multipleInput, expectedProperties);
        }
        
        SimplePropertyDescriptionDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                int specificErrorsExpectedCount) {
            super(testCase, multipleInput, specificErrorsExpectedCount);
        }

        @Override
        SimplePropertyDescriptionDecoder convert() {
            return new SimplePropertyDescriptionDecoder();
        }
        
    }
}
