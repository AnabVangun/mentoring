package mentoring.io;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SimplePropertyName;
import mentoring.io.PropertyNameDecoderTest.PropertyNameDecoderArgs;
import mentoring.io.SimplePropertyNameDecoderTest.SimplePropertyNameDecoderArgs;

class SimplePropertyNameDecoderTest extends PropertyNameDecoderTest<SimplePropertyName<?>, 
        SimplePropertyNameDecoder, SimplePropertyNameDecoderArgs>{

    @Override
    Stream<SimplePropertyNameDecoderArgs> specificallyInvalidArgumentsSupplier() {
        return Stream.of(
                new SimplePropertyNameDecoderArgs("unexpected attribute property",
                        List.of(Map.of("name", "name value", "headerName", "headerName value",
                                "type", "integer", "wrong property", "wrong property value")),
                        1));
    }

    @Override
    protected Stream<SimplePropertyNameDecoderArgs> genericallyInvalidArgumentsSupplier() {
        return Stream.of(
                new SimplePropertyNameDecoderArgs("missing attribute property", 
                        List.of(Map.of("name", "name value", "type", "integer")), 0),
                new SimplePropertyNameDecoderArgs(
                        "multiple properties mixing valid and invalid properties",
                        List.of(Map.of("name", "first name", "headerName", "first header", 
                                "type", "integer"),
                                Map.of("name", "second name", "type", "integer")), 
                        0));
    }

    @Override
    public Stream<SimplePropertyNameDecoderArgs> argumentsSupplier() {
        return Stream.of(
                new SimplePropertyNameDecoderArgs("valid test case",
                        List.of(
                                Map.of("name", "int property", "headerName", "int header", 
                                        "type", "integer"),
                                Map.of("name", "boolean property", "headerName", "boolean header",
                                        "type", "boolean")),
                        List.of(new SimplePropertyName<>("int property", "int header", PropertyType.INTEGER),
                                new SimplePropertyName<>("boolean property", "boolean header", 
                                        PropertyType.BOOLEAN))));
    }
    
    static class SimplePropertyNameDecoderArgs extends PropertyNameDecoderArgs<SimplePropertyName<?>, 
            SimplePropertyNameDecoder>{

        SimplePropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                List<SimplePropertyName<?>> expectedProperties) {
            super(testCase, multipleInput, expectedProperties);
        }
        
        SimplePropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                int specificErrorsExpectedCount) {
            super(testCase, multipleInput, specificErrorsExpectedCount);
        }

        @Override
        SimplePropertyNameDecoder convert() {
            return new SimplePropertyNameDecoder();
        }
        
    }
}
