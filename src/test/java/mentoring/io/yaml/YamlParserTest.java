package mentoring.io.yaml;

import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.io.yaml.YamlParserTest.YamlParserArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

class YamlParserTest implements TestFramework<YamlParserArgs>{
    
    @Override
    public Stream<YamlParserArgs> argumentsSupplier(){
        return Stream.of(new YamlParserArgs("sampleFile.yaml", 
                Map.of("first", "value",
                        "list", List.of("string", Map.of("map", "second value"), 
                                Map.of("object", Map.of("firstProperty", "third value", 
                                        "second property", "fourth_value"))))));
    }
    
    Stream<YamlParserArgs> invalidArgumentsSupplier(){
        return Stream.of(new YamlParserArgs("poorlyIndentedTestConfiguration.yaml", null));
    }
    
    @TestFactory
    Stream<DynamicNode> parse_validInput(){
        return test("parse() returns the expected object on valid input", args ->
            Assertions.assertEquals(args.expectedResult, args.convert()));
    }
    
    @TestFactory
    Stream<DynamicNode> parse_invalidInput(){
        return test(invalidArgumentsSupplier(), 
                "parse() returns the expected exception on invalid input", args ->
                        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> args.convertWithException()));
    }
    
    static record YamlParserArgs(String filePath, Map<String, Object> expectedResult){
        @Override
        public String toString(){
            return filePath;
        }
        
        Map<String, Object> convertWithException() throws IOException{
            return new YamlParser().parse(new FileReader(getClass().getResource(filePath).getFile(),
                    Charset.forName("utf-8")));
        }
        
        Map<String, Object> convert(){
            try {
                return convertWithException();
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }
    }
}
