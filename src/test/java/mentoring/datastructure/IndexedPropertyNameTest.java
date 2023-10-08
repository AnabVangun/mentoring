package mentoring.datastructure;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.datastructure.IndexedPropertyNameTest.IndexedPropertyArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class IndexedPropertyNameTest extends MultiplePropertyNameTest<IndexedPropertyArgs>{
    @Override
    public Stream<IndexedPropertyArgs> argumentsSupplier() {
        return Stream.of(
                new IndexedPropertyArgs("Simple property", "propriété", "headerName", 
                        PropertyType.STRING,
                        new String[]{"first", "second", "third"}, 
                        Map.of("first",0, "second", 1, "third", 2)),
                new IndexedPropertyArgs("Property with equal names", "name", "name", 
                        PropertyType.INTEGER, 
                        new String[]{"12","-3"}, Map.of(12, 0, -3, 1)),
                new IndexedPropertyArgs("Property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN, new String[]{}, Map.of()),
                new IndexedPropertyArgs("Property with empty headerName", "name", "", 
                        PropertyType.STRING, new String[]{"first"}, Map.of("first", 0)),
                new IndexedPropertyArgs("Property with empty names", "", "", 
                        PropertyType.INTEGER, new String[]{}, Map.of())
            );
    }
    
    @Override
    @TestFactory
    Stream<DynamicNode> getStringRepresentation_expectedValue(){
        return test(Stream.of(new IndexedPropertyArgs("specific test case", "propriété", 
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
    protected IndexedPropertyArgs getDifferentArgs() {
        return new IndexedPropertyArgs("different args", "different property", "different header", 
                PropertyType.INTEGER, new String[]{"different input"}, Map.of("different input", 1));
    }
    
    static class IndexedPropertyArgs extends MultiplePropertyNameArgs{
        IndexedPropertyArgs(String testCase, String name, String headerName, PropertyType<?> keyType,
                String[] mapInput, Map<?, Integer> expectedResult){
            super(testCase, name, headerName, keyType, PropertyType.INTEGER, mapInput, 
                    expectedResult);
        }

        @Override
        protected IndexedPropertyName<?> convert() {
            return new IndexedPropertyName<>(getExpectedName(), getExpectedHeaderName(), 
                    expectedType);
        }
    }
}