package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.SimplePropertyNameTest.PropertyArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class SimplePropertyNameTest extends AbstractPropertyNameTest<SimplePropertyName<?>, PropertyArgs>{

    @Override
    public Stream<PropertyArgs> argumentsSupplier() {
        return Stream.of(
                new PropertyArgs("Simple property", "propriété", "headerName", 
                        PropertyType.STRING),
                new PropertyArgs("Property with equal names", "name", "name", 
                        PropertyType.INTEGER),
                new PropertyArgs("Property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN),
                new PropertyArgs("Property with empty headerName", "name", "", 
                        PropertyType.INTEGER),
                new PropertyArgs("Property with empty names", "", "", 
                        PropertyType.BOOLEAN)
            );
    }
    
    @Override
    protected PropertyArgs getDifferentArgs(){
        return new PropertyArgs("Property with different values", "specific_foo", 
                "differentBar", PropertyType.YEAR);
    }
    
    @TestFactory
    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    Stream<DynamicNode> equals_equalValue(){
        return test("equals() returns true on equal values", 
                args -> Assertions.assertEquals(args.convert(), args.convert()));
    }
    
    @TestFactory
    Stream<DynamicNode> equals_differentValue(){
        return test("equals returns false on different values",
                args -> Assertions.assertNotEquals(args.convert(), getDifferentArgs().convert()));
    }
    
    @TestFactory
    protected Stream<DynamicNode> equals_similarMultiplePropertyName(){
        return test(Stream.of(
                new PropertyArgs("specific test case", "name", "header name", PropertyType.YEAR)),
                "equals returns false on similar MultiplePropertyName", 
                args -> Assertions.assertNotEquals(args.convert(), 
                        new SetPropertyName<>("name", "header name", PropertyType.YEAR)));
    }
    
    @TestFactory
    Stream<DynamicNode> hashCode_equalValue(){
        return test("hashCode() returns the same value for two equal values",
                args -> Assertions.assertEquals(args.convert().hashCode(), 
                        args.convert().hashCode()));
    }
    
    @TestFactory
    Stream<DynamicNode> hashCode_consistent(){
        return test("hashCode() returns the same value when called multiple times",
                args -> {
                    PropertyName<?> property = args.convert();
                    int first = property.hashCode();
                    Assertions.assertEquals(first, property.hashCode());
                });
    }
    
    @TestFactory
    @Override
    Stream<DynamicNode> getStringRepresentation_expectedValue(){
        return test(Stream.of(new PropertyArgs("specific test case", "property", "property", 
                PropertyType.INTEGER)), "getStringRepresentation() returns the expected value",
                args -> {
                    PropertyName<?> property = args.convert();
                    Person person = new PersonBuilder().withProperty(property.getName(), 3).build();
                    Assertions.assertEquals("3", property.getStringRepresentation(person));
                });
    }
    
    static class PropertyArgs 
            extends AbstractPropertyNameTest.AbstractPropertyNameArgs<Object, 
                    SimplePropertyName<? extends Object>>{
        //TODO refactor rename SimplePropertyArgs
        
        PropertyArgs(String testCase, String name, String headerName, PropertyType<? extends Object> type){
            super(testCase, name, headerName, type);
        }
        
        @Override
        SimplePropertyName<? extends Object> convert() {
            return new SimplePropertyName<>(name, headerName, expectedType);
        }
    }
}

