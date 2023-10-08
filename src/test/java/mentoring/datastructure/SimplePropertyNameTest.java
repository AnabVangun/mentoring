package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.SimplePropertyNameTest.SimplePropertyNameArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class SimplePropertyNameTest extends PropertyNameTest<SimplePropertyName<?>, SimplePropertyNameArgs>{

    @Override
    public Stream<SimplePropertyNameArgs> argumentsSupplier() {
        return Stream.of(new SimplePropertyNameArgs("Simple property", "propriété", "headerName", 
                        PropertyType.STRING),
                new SimplePropertyNameArgs("Property with equal names", "name", "name", 
                        PropertyType.INTEGER),
                new SimplePropertyNameArgs("Property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN),
                new SimplePropertyNameArgs("Property with empty headerName", "name", "", 
                        PropertyType.INTEGER),
                new SimplePropertyNameArgs("Property with empty names", "", "", 
                        PropertyType.BOOLEAN)
            );
    }
    
    @Override
    protected SimplePropertyNameArgs getDifferentArgs(){
        return new SimplePropertyNameArgs("Property with different values", "specific_foo", 
                "differentBar", PropertyType.YEAR);
    }
    
    @TestFactory
    protected Stream<DynamicNode> equals_similarMultiplePropertyName(){
        return test(Stream.of(new SimplePropertyNameArgs("specific test case", "name", "header name", PropertyType.YEAR)),
                "equals returns false on similar MultiplePropertyName", 
                args -> Assertions.assertNotEquals(args.convert(), 
                        new SetPropertyName<>("name", "header name", PropertyType.YEAR)));
    }
    
    @TestFactory
    @Override
    Stream<DynamicNode> getStringRepresentation_expectedValue(){
        return test(Stream.of(new SimplePropertyNameArgs("specific test case", "property", "property", 
                PropertyType.INTEGER)), "getStringRepresentation() returns the expected value",
                args -> {
                    PropertyName<?> property = args.convert();
                    Person person = new PersonBuilder().withProperty(property.getName(), 3).build();
                    Assertions.assertEquals("3", property.getStringRepresentation(person));
                });
    }
    
    static class SimplePropertyNameArgs 
            extends PropertyNameTest.PropertyNameArgs<Object, 
                    SimplePropertyName<? extends Object>>{
        
        SimplePropertyNameArgs(String testCase, String name, String headerName, PropertyType<? extends Object> type){
            super(testCase, name, headerName, type);
        }
        
        @Override
        SimplePropertyName<? extends Object> convert() {
            return new SimplePropertyName<>(name, headerName, expectedType);
        }
    }
}

