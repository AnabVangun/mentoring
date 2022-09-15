package mentoring.datastructure;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SetPropertyNameTest extends MultiplePropertyNameTest{
    @Override
    public Stream<PropertyArgs<?,?>> argumentsSupplier() {
        return Stream.of(new OneArgMapProperty<>("Simple one-argument property", "name&", 
                        PropertyType.INTEGER, 
                        new String[]{"1"}, Set.of(1)),
                new OneArgMapProperty<>("One-argument property with empty name", "", 
                        PropertyType.BOOLEAN,
                        new String[]{"vrai","faux"}, Set.of(true, false)),
                new TwoArgMapProperty<>("Simple two-argument property", "propriété", "headerName", 
                        PropertyType.STRING,
                        new String[]{"first", "second", "third"}, 
                        Set.of("first", "second", "third")),
                new TwoArgMapProperty<>("Two-argument property with equal names", "name", "name", 
                        PropertyType.INTEGER, 
                        new String[]{"12","-3"}, Set.of(12, -3)),
                new TwoArgMapProperty<>("Two-argument property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN, new String[]{}, Set.of()),
                new TwoArgMapProperty<>("Two-argument property with empty headerName", "name", "", 
                        PropertyType.STRING, new String[]{"first"}, Set.of("first")),
                new TwoArgMapProperty<>("Two-argument property with empty names", "", "", 
                        PropertyType.INTEGER, new String[]{}, Set.of())
            );
    }
    
    static class OneArgMapProperty<K> extends OneArgProperty<K,Integer>{
        final String[] mapInput;
        final Map<? extends K, Integer> expectedResult;

        OneArgMapProperty(String testCase, String name, PropertyType<K> keyType, 
                String[] mapInput, Set<? extends K> expectedResult){
            super(testCase, name, keyType);
            this.mapInput = mapInput;
            this.expectedResult = expectedResult.stream()
                    .collect(Collectors.toMap(Function.identity(), args -> 0));
        }

        @Override
        SetPropertyName<K> convert() {
            return new SetPropertyName<>(getExpectedName(), expectedType);
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

    static class TwoArgMapProperty<K> extends TwoArgProperty<K,Integer>{
        final String[] mapInput;
        final Map<? extends K, ? extends Integer> expectedResult;

        TwoArgMapProperty(String testCase, String name, String headerName, PropertyType<K> keyType,
                String[] mapInput, Set<? extends K> expectedResult){
            super(testCase, name, headerName, keyType);
            this.mapInput = mapInput;
            this.expectedResult = expectedResult.stream()
                    .collect(Collectors.toMap(Function.identity(), args -> 0));
        }

        @Override
        SetPropertyName<K> convert() {
            return new SetPropertyName<>(getExpectedName(), getExpectedHeaderName(), 
                    expectedType);
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
}