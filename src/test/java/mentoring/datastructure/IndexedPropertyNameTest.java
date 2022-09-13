package mentoring.datastructure;

import java.util.Map;
import java.util.stream.Stream;

class IndexedPropertyNameTest extends MultiplePropertyNameTest{
    @Override
    public Stream<PropertyArgs> argumentsSupplier() {
        return Stream.of(new OneArgMapProperty<>("Simple one-argument property", "name&", 
                        PropertyType.INTEGER, 
                        new String[]{"1"}, Map.of(1, 0)),
                new OneArgMapProperty<>("One-argument property with empty name", "", 
                        PropertyType.BOOLEAN,
                        new String[]{"vrai","faux"}, Map.of(true, 0, false, 1)),
                new TwoArgMapProperty<>("Simple two-argument property", "propriété", "headerName", 
                        PropertyType.STRING,
                        new String[]{"first", "second", "third"}, 
                        Map.of("first",0, "second", 1, "third", 2)),
                new TwoArgMapProperty<>("Two-argument property with equal names", "name", "name", 
                        PropertyType.INTEGER, 
                        new String[]{"12","-3"}, Map.of(12, 0, -3, 1)),
                new TwoArgMapProperty<>("Two-argument property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN, new String[]{}, Map.of()),
                new TwoArgMapProperty<>("Two-argument property with empty headerName", "name", "", 
                        PropertyType.STRING, new String[]{"first"}, Map.of("first", 0)),
                new TwoArgMapProperty<>("Two-argument property with empty names", "", "", 
                        PropertyType.INTEGER, new String[]{}, Map.of())
            );
    }
    
    static class OneArgMapProperty<K> extends OneArgProperty{
        final String[] mapInput;
        final Map<? extends K, ? extends Integer> expectedResult;

        OneArgMapProperty(String testCase, String name, PropertyType<K> keyType, 
                String[] mapInput, Map<? extends K, ? extends Integer> expectedResult){
            super(testCase, name, keyType);
            this.mapInput = mapInput;
            this.expectedResult = expectedResult;
        }

        @Override
        IndexedPropertyName<K> convert() {
            return new IndexedPropertyName<>(getExpectedName(), expectedType);
        }
        
        @Override
        PropertyType<Integer> getExpectedValueType(){
            return PropertyType.INTEGER;
        }
        
        @Override
        String[] getMapInput(){
            return mapInput;
        }
        
        @Override
        Map<? extends K, ? extends Integer> getExpectedResult(){
            return expectedResult;
        }
    }

    static class TwoArgMapProperty<K> extends TwoArgProperty{
        final PropertyType valueType;
        final String[] mapInput;
        final Map<? extends K, ? extends Integer> expectedResult;

        TwoArgMapProperty(String testCase, String name, String headerName, PropertyType<K> keyType,
                String[] mapInput, Map<? extends K, ? extends Integer> expectedResult){
            super(testCase, name, headerName, keyType);
            this.valueType = PropertyType.INTEGER;
            this.mapInput = mapInput;
            this.expectedResult = expectedResult;
        }

        @Override
        IndexedPropertyName<K> convert() {
            return new IndexedPropertyName<>(getExpectedName(), getExpectedHeaderName(), 
                    expectedType);
        }
        
        @Override
        PropertyType<Integer> getExpectedValueType(){
            return valueType;
        }
        
        @Override
        String[] getMapInput(){
            return mapInput;
        }
        
        @Override
        Map<? extends K, ? extends Integer> getExpectedResult(){
            return expectedResult;
        }
    }
}