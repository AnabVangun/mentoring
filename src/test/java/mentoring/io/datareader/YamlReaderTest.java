package mentoring.io.datareader;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.io.datareader.YamlReaderTest.YamlReaderArgs;

class YamlReaderTest implements DataReaderTest<YamlReader, YamlReaderArgs>{
    
    @Override
    public Stream<YamlReaderArgs> argumentsSupplier(){
        return Stream.of(new YamlReaderArgs("sampleFile.yaml", 
                Map.of("first", "value",
                        "list", List.of("string", Map.of("map", "second value"), 
                                Map.of("object", Map.of("firstProperty", "third value", 
                                        "second property", "fourth_value"))))),
                new YamlReaderArgs("sampleFile2.yaml", Map.of("second", "value")));
    }
    
    @Override
    public Stream<YamlReaderArgs> invalidArgumentsSupplier(){
        return Stream.of(new YamlReaderArgs("poorlyIndentedTestConfiguration.yaml", null));
    }
    
    @Override
    public YamlReader prepareReader(){
        return new YamlReader();
    }
    
    static class YamlReaderArgs extends DataReaderArgs<YamlReader>{

        public YamlReaderArgs(String filePath, Map<String, Object> expectedResult) {
            super(filePath, expectedResult);
        }
        
        @Override
        protected YamlReader getReaderUnderTest(){
            return new YamlReader();
        }
        
    }
}
