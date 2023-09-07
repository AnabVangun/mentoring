package mentoring.datastructure;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class IndexedPropertyNameTest extends MultiplePropertyNameTest{
    @Override
    public Stream<PropertyArgs<?,?>> argumentsSupplier() {
        return Stream.of(
                new IndexedPropertyArgs<>("Simple property", "propriété", "headerName", 
                        PropertyType.STRING,
                        new String[]{"first", "second", "third"}, 
                        Map.of("first",0, "second", 1, "third", 2)),
                new IndexedPropertyArgs<>("Property with equal names", "name", "name", 
                        PropertyType.INTEGER, 
                        new String[]{"12","-3"}, Map.of(12, 0, -3, 1)),
                new IndexedPropertyArgs<>("Property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN, new String[]{}, Map.of()),
                new IndexedPropertyArgs<>("Property with empty headerName", "name", "", 
                        PropertyType.STRING, new String[]{"first"}, Map.of("first", 0)),
                new IndexedPropertyArgs<>("Property with empty names", "", "", 
                        PropertyType.INTEGER, new String[]{}, Map.of())
            );
    }
    
    @Override
    @TestFactory
    Stream<DynamicNode> getStringRepresentation_expectedValue(){
        return test(Stream.of(new IndexedPropertyArgs<>("specific test case", "propriété", 
                "headerName", PropertyType.STRING, new String[]{"first", "second", "third"}, 
                        Map.of("first",0, "second", 1, "third", 2))), 
                "getStringRepresentation() returns the expected value",
                args -> {
                    PropertyName<?> property = args.convert();
                    Person person = new PersonBuilder()
                            .withPropertyMap(property.getName(), args.expectedResult).build();
                    Assertions.assertEquals(List.of("first", "second", "third").toString(), 
                            property.getStringRepresentation(person));
                });
    }
    
    @Override
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> getStringRepresentation_NPE(){
        return test(Stream.of(new IndexedPropertyArgs<>("specific test case", "propriété", 
                "headerName", PropertyType.STRING, new String[]{"first", "second", "third"}, 
                        Map.of("first",0, "second", 1, "third", 2))), 
                "getStringRepresentation() returns the expected value",
                args -> {
                    PropertyName<?> property = args.convert();
                    Assertions.assertThrows(NullPointerException.class, 
                            () -> property.getStringRepresentation(null));
                });
    }
    
    static class IndexedPropertyArgs<K> extends MapPropertyArgs<K,Integer>{

        IndexedPropertyArgs(String testCase, String name, String headerName, PropertyType<K> keyType,
                String[] mapInput, Map<K, Integer> expectedResult){
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