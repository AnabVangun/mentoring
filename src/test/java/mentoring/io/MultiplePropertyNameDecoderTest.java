package mentoring.io;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;
import mentoring.io.MultiplePropertyNameDecoderTest.MultiplePropertyNameDecoderArgs;
import mentoring.io.PropertyNameDecoderTest.PropertyNameDecoderArgs;

class MultiplePropertyNameDecoderTest extends PropertyNameDecoderTest<MultiplePropertyName<?,?>, 
        MultiplePropertyNameDecoder, MultiplePropertyNameDecoderArgs>{

    @Override
    Stream<MultiplePropertyNameDecoderArgs> specificallyInvalidArgumentsSupplier() {
        return Stream.of(new MultiplePropertyNameDecoderArgs("unexpected attribute property", 
                List.of(Map.of("name", "name value", "headerName", "headerName value",
                                "type", "integer", "aggregation", "aggregation value", 
                                "wrong property", "wrong property value", 
                                "second wrong property", "wrong value")),
                        2));
    }

    @Override
    protected Stream<MultiplePropertyNameDecoderArgs> genericallyInvalidArgumentsSupplier() {
        return Stream.of(new MultiplePropertyNameDecoderArgs("missing attribute property", 
                        List.of(Map.of("name", "name value")), 0));
    }

    @Override
    public Stream<MultiplePropertyNameDecoderArgs> argumentsSupplier() {
        return Stream.of(
                new MultiplePropertyNameDecoderArgs("valid test case",
                        List.of(
                                Map.of("name", "int property", "headerName", "int header", 
                                        "type", "integer", "aggregation", "set"),
                                Map.of("name", "boolean property", "headerName", "boolean header",
                                        "type", "boolean", "aggregation", "indexed")),
                        List.of(new SetPropertyName<>("int property", "int header", PropertyType.INTEGER),
                                new IndexedPropertyName<>("boolean property", "boolean header", 
                                        PropertyType.BOOLEAN))));
    }
    
    static class MultiplePropertyNameDecoderArgs extends 
            PropertyNameDecoderArgs<MultiplePropertyName<?,?>, MultiplePropertyNameDecoder>{

        MultiplePropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                List<MultiplePropertyName<?, ?>> expectedProperties) {
            super(testCase, multipleInput, expectedProperties);
        }
        
        MultiplePropertyNameDecoderArgs(String testCase, 
                List<Map<? extends String, ? extends String>> multipleInput, 
                int specificErrorsExpectedCount) {
            super(testCase, multipleInput, specificErrorsExpectedCount);
        }

        @Override
        MultiplePropertyNameDecoder convert() {
            return new MultiplePropertyNameDecoder();
        }
    }
}
