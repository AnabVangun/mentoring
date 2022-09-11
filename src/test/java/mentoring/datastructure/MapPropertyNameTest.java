package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.PropertyNameTest.OneArgProperty;
import mentoring.datastructure.PropertyNameTest.PropertyArgs;
import mentoring.datastructure.PropertyNameTest.TwoArgProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

final class MapPropertyNameTest extends PropertyNameTest{
    @Override
    public Stream<PropertyArgs> argumentsSupplier() {
        return Stream.of(new OneArgMapProperty("Simple one-argument property", "name&", 
                        PropertyType.INTEGER, PropertyType.BOOLEAN),
                new OneArgMapProperty("One-argument property with empty name", "", 
                        PropertyType.BOOLEAN, PropertyType.INTEGER),
                new TwoArgMapProperty("Simple two-argument property", "propriété", "headerName", 
                        PropertyType.STRING, PropertyType.STRING),
                new TwoArgMapProperty("Two-argument property with equal names", "name", "name", 
                        PropertyType.INTEGER, PropertyType.STRING),
                new TwoArgMapProperty("Two-argument property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN, PropertyType.BOOLEAN),
                new TwoArgMapProperty("Two-argument property with empty headerName", "name", "", 
                        PropertyType.STRING, PropertyType.INTEGER),
                new TwoArgMapProperty("Two-argument property with empty names", "", "", 
                        PropertyType.INTEGER, PropertyType.STRING)
            );
    }
    
    @TestFactory
    Stream<DynamicNode> getValueType(){
        return test("getValueType() returns the expected type", args -> 
                Assertions.assertEquals(args.getExpectedValueType(), 
                        ((MapPropertyName) args.convert()).getValueType()));
    }
    
    static class OneArgMapProperty extends OneArgProperty{
        final PropertyType valueType;

        OneArgMapProperty(String testCase, String name, PropertyType keyType, PropertyType valueType){
            super(testCase, name, keyType);
            this.valueType = valueType;
        }

        @Override
        MapPropertyName convert() {
            return new MapPropertyName(getExpectedName(), expectedType, valueType);
        }
        
        @Override
        PropertyType getExpectedValueType(){
            return valueType;
        }
    }

    static class TwoArgMapProperty extends TwoArgProperty{
        final PropertyType valueType;

        TwoArgMapProperty(String testCase, String name, String headerName, PropertyType keyType,
                PropertyType valueType){
            super(testCase, name, headerName, keyType);
            this.valueType = valueType;
        }

        @Override
        MapPropertyName convert() {
            return new MapPropertyName(getExpectedName(), getExpectedHeaderName(), expectedType, 
                    valueType);
        }
        
        @Override
        PropertyType getExpectedValueType(){
            return valueType;
        }
    }
}
