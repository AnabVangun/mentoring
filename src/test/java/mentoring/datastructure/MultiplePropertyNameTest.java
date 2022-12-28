package mentoring.datastructure;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import mentoring.datastructure.PropertyNameTest.PropertyArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class MultiplePropertyNameTest extends PropertyNameTest{
    @Override
    public Stream<PropertyArgs<?,?>> argumentsSupplier() {
        return Stream.of(
                new MapPropertyArgs<>("Simple property", "propriété", "headerName", 
                        PropertyType.STRING, PropertyType.STRING,
                        entries -> Map.of(entries[0], entries[entries.length-1]),
                        new String[]{"first", "second", "third"}, Map.of("first","third")),
                new MapPropertyArgs<>("Property with equal names", "name", "name", 
                        PropertyType.INTEGER, PropertyType.STRING,
                        entries -> Map.of(0, entries[0], 2, entries[1]),
                        new String[]{"first","second"}, Map.of(0, "first", 2, "second")),
                new MapPropertyArgs<>("Property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN, PropertyType.BOOLEAN,
                        entries -> Map.of(false, true), new String[]{}, Map.of(false, true)),
                new MapPropertyArgs<>("Property with empty headerName", "name", "", 
                        PropertyType.STRING, PropertyType.INTEGER,
                        entries -> Map.of(), new String[]{"first"}, Map.of()),
                new MapPropertyArgs<>("Property with empty names", "", "", 
                        PropertyType.INTEGER, PropertyType.STRING,
                        entries -> Map.of(-12, "third"), new String[]{}, Map.of(-12, "third"))
            );
    }
    
    @TestFactory
    Stream<DynamicNode> getValueType(){
        return test("getValueType() returns the expected type", args -> 
                Assertions.assertEquals(args.getExpectedValueType(), 
                        ((MultiplePropertyName) args.convert()).getValueType()));
    }
    
    @TestFactory
    Stream<DynamicNode> buildMap(){
        return test("buildMap() returns the expected map", args -> 
                Assertions.assertEquals(args.getExpectedResult(),
                        ((MultiplePropertyName) args.convert()).buildMap(args.getMapInput())));
    }
    
    static class MapPropertyArgs<K,V> extends PropertyArgs<K, V>{
        final PropertyType<V> valueType;
        final Function<String[], Map<K,V>> parser;
        final String[] mapInput;
        final Map<K,V> expectedResult;

        MapPropertyArgs(String testCase, String name, String headerName, PropertyType<K> keyType,
                PropertyType<V> valueType, Function<String[], Map<K,V>> parser,
                String[] mapInput, Map<K,V> expectedResult){
            super(testCase, name, headerName, keyType);
            this.valueType = valueType;
            this.parser = parser;
            this.mapInput = mapInput;
            this.expectedResult = expectedResult;
        }

        @Override
        MultiplePropertyName<K,V> convert() {
            return new MultiplePropertyName<>(getExpectedName(), getExpectedHeaderName(), 
                    expectedType, valueType, parser);
        }
        
        @Override
        PropertyType<V> getExpectedValueType(){
            return valueType;
        }
        
        @Override
        String[] getMapInput(){
            return mapInput;
        }
        
        @Override
        Map<K, V> getExpectedResult(){
            return expectedResult;
        }
    }
}
