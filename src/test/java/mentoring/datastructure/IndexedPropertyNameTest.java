package mentoring.datastructure;

import java.util.Map;
import java.util.stream.Stream;

class IndexedPropertyNameTest extends MultiplePropertyNameTest{
    @Override
    public Stream<PropertyArgs<?,?>> argumentsSupplier() {
        return Stream.of(
                new IndexedPropertyArgs<>("Simple two-argument property", "propriété", "headerName", 
                        PropertyType.STRING,
                        new String[]{"first", "second", "third"}, 
                        Map.of("first",0, "second", 1, "third", 2)),
                new IndexedPropertyArgs<>("Two-argument property with equal names", "name", "name", 
                        PropertyType.INTEGER, 
                        new String[]{"12","-3"}, Map.of(12, 0, -3, 1)),
                new IndexedPropertyArgs<>("Two-argument property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN, new String[]{}, Map.of()),
                new IndexedPropertyArgs<>("Two-argument property with empty headerName", "name", "", 
                        PropertyType.STRING, new String[]{"first"}, Map.of("first", 0)),
                new IndexedPropertyArgs<>("Two-argument property with empty names", "", "", 
                        PropertyType.INTEGER, new String[]{}, Map.of())
            );
    }
    
    static class IndexedPropertyArgs<K> extends MapPropertyArgs<K,Integer>{

        IndexedPropertyArgs(String testCase, String name, String headerName, PropertyType<K> keyType,
                String[] mapInput, Map<? extends K, ? extends Integer> expectedResult){
            super(testCase, name, headerName, keyType, PropertyType.INTEGER, null, mapInput, 
                    expectedResult);
        }

        @Override
        IndexedPropertyName<K> convert() {
            return new IndexedPropertyName<>(getExpectedName(), getExpectedHeaderName(), 
                    expectedType);
        }
    }
}