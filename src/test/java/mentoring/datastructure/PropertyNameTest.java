package mentoring.datastructure;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class PropertyNameTest implements TestFramework<PropertyNameTest.PropertyArgs<?,?>>{

    @Override
    public Stream<PropertyArgs<?,?>> argumentsSupplier() {
        return Stream.of(
                new PropertyArgs<>("Simple property", "propriété", "headerName", 
                        PropertyType.STRING),
                new PropertyArgs<>("Property with equal names", "name", "name", 
                        PropertyType.INTEGER),
                new PropertyArgs<>("Property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN),
                new PropertyArgs<>("Property with empty headerName", "name", "", 
                        PropertyType.INTEGER),
                new PropertyArgs<>("Property with empty names", "", "", 
                        PropertyType.BOOLEAN)
            );
    }
    
    protected PropertyArgs<?,?> getDifferentArgs(){
        return new PropertyArgs<>("Property with different values", "specific_foo", 
                "differentBar", PropertyType.YEAR);
    }
    
    @TestFactory
    Stream<DynamicNode> getName(){
        return test("getName()", args -> Assertions.assertEquals(args.getExpectedName(),
                args.convert().getName()));
    }
    
    @TestFactory
    Stream<DynamicNode> getHeaderName(){
        return test("getHeaderName()", args -> Assertions.assertEquals(args.getExpectedHeaderName(),
                args.convert().getHeaderName()));
    }
    
    @TestFactory
    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    Stream<DynamicNode> getType(){
        return test("getType()", args -> Assertions.assertEquals(args.expectedType,
                args.convert().getType()));
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
                new PropertyArgs<>("specific test case", "name", "header name", PropertyType.YEAR)),
                "equals returns false on similar MultiplePropertyName", 
                args -> Assertions.assertNotEquals(args.convert(), 
                        new MultiplePropertyName<>("name", "header name", PropertyType.YEAR, 
                                PropertyType.BOOLEAN, null)));
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
    Stream<DynamicNode> getStringRepresentation_expectedValue(){
        return test(Stream.of(new PropertyArgs<>("specific test case", "property", "property", 
                PropertyType.INTEGER)), "getStringRepresentation() returns the expected value",
                args -> {
                    PropertyName<?> property = args.convert();
                    Person person = new PersonBuilder().withProperty(property.getName(), 3).build();
                    Assertions.assertEquals("3", property.getStringRepresentation(person));
                });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> getStringRepresentation_NPE(){
        return test(Stream.of(new PropertyArgs<>("specific test case", "property", "property", 
                PropertyType.INTEGER)), "getStringRepresentation() returns the expected value",
                args -> {
                    PropertyName<?> property = args.convert();
                    Assertions.assertThrows(NullPointerException.class, 
                            () -> property.getStringRepresentation(null));
                });
    }
    
    static class PropertyArgs<K,V> extends TestArgs{
        final PropertyType<K> expectedType;
        private final String name;
        private final String headerName;
        
        PropertyArgs(String testCase, String name, String headerName, PropertyType<K> type){
            super(testCase);
            expectedType = type;
            this.name = name;
            this.headerName = headerName;
        }
        
        PropertyName<K> convert() {
            return new PropertyName<>(name, headerName, expectedType);
        }

        String getExpectedName() {
            return this.name;
        }

        String getExpectedHeaderName() {
            return this.headerName;
        }
        
        PropertyType<V> getExpectedValueType(){
            throw new UnsupportedOperationException("Method used only in MapPropertyArg objects");
        }
        
        String[] getMapInput(){
            throw new UnsupportedOperationException("Method used only in MapPropertyArg objects");
        }
        
        Map<K,V> getExpectedResult(){
            throw new UnsupportedOperationException("Method used only in MapPropertyArg objects");
        }
    }
}

