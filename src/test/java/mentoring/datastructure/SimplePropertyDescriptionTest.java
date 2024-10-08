package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.SimplePropertyDescriptionTest.SimplePropertyDescriptionArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class SimplePropertyDescriptionTest extends PropertyDescriptionTest<SimplePropertyDescription<?>, SimplePropertyDescriptionArgs>{

    @Override
    public Stream<SimplePropertyDescriptionArgs> argumentsSupplier() {
        return Stream.of(new SimplePropertyDescriptionArgs("Simple property", "propriété", "headerName", 
                        PropertyType.STRING),
                new SimplePropertyDescriptionArgs("Property with equal names", "name", "name", 
                        PropertyType.INTEGER),
                new SimplePropertyDescriptionArgs("Property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN),
                new SimplePropertyDescriptionArgs("Property with empty headerName", "name", "", 
                        PropertyType.INTEGER),
                new SimplePropertyDescriptionArgs("Property with empty names", "", "", 
                        PropertyType.BOOLEAN)
            );
    }
    
    @Override
    protected SimplePropertyDescriptionArgs getDifferentArgs(){
        return new SimplePropertyDescriptionArgs("Property with different values", "specific_foo", 
                "differentBar", PropertyType.YEAR);
    }
    
    @TestFactory
    protected Stream<DynamicNode> equals_similarMultiplePropertyDescription(){
        return test(Stream.of(new SimplePropertyDescriptionArgs("specific test case", "name", "header name", PropertyType.YEAR)),
                "equals returns false on similar MultiplePropertyDescription", 
                args -> Assertions.assertNotEquals(args.convert(), 
                        new SetPropertyDescription<>("name", "header name", PropertyType.YEAR)));
    }
    
    @TestFactory
    @Override
    Stream<DynamicNode> getStringRepresentation_expectedValue(){
        return test(Stream.of(new SimplePropertyDescriptionArgs("specific test case", "property", "property", 
                PropertyType.INTEGER)), "getStringRepresentation() returns the expected value",
                args -> {
                    PropertyDescription<?> property = args.convert();
                    Person person = new PersonBuilder().withProperty(property.getName(), 3).build();
                    Assertions.assertEquals("3", property.getStringRepresentation(person));
                });
    }
    
    static class SimplePropertyDescriptionArgs 
            extends PropertyDescriptionTest.PropertyDescriptionArgs<Object, 
                    SimplePropertyDescription<? extends Object>>{
        
        SimplePropertyDescriptionArgs(String testCase, String name, String headerName, PropertyType<? extends Object> type){
            super(testCase, name, headerName, type);
        }
        
        @Override
        SimplePropertyDescription<? extends Object> convert() {
            return new SimplePropertyDescription<>(name, headerName, expectedType);
        }
    }
}

