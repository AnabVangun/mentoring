package mentoring.datastructure;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class PropertyNameTest implements TestFramework<PropertyNameTest.PropertyArgs<?,?>>{

    @Override
    public Stream<PropertyArgs<?,?>> argumentsSupplier() {
        return Stream.of(new OneArgProperty<>("Simple one-argument property", "name&", PropertyType.INTEGER),
                new OneArgProperty<>("One-argument property with empty name", "", PropertyType.BOOLEAN),
                new TwoArgProperty<>("Simple two-argument property", "propriété", "headerName", 
                        PropertyType.STRING),
                new TwoArgProperty<>("Two-argument property with equal names", "name", "name", 
                        PropertyType.INTEGER),
                new TwoArgProperty<>("Two-argument property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN),
                new TwoArgProperty<>("Two-argument property with empty headerName", "name", "", 
                        PropertyType.STRING),
                new TwoArgProperty<>("Two-argument property with empty names", "", "", PropertyType.INTEGER)
            );
    }
    
    @TestFactory
    Stream<DynamicNode> getName(){
        return test("getName()", args -> assertEquals(args.getExpectedName(),
                args.convert().getName()));
    }
    
    @TestFactory
    Stream<DynamicNode> getHeaderName(){
        return test("getHeaderName()", args -> assertEquals(args.getExpectedHeaderName(),
                args.convert().getHeaderName()));
    }
    
    @TestFactory
    Stream<DynamicNode> getType(){
        return test("getType()", args -> assertEquals(args.expectedType,
                args.convert().getType()));
    }
    
    static abstract class PropertyArgs<K,V> extends TestArgs{
        final PropertyType<K> expectedType;
        
        abstract PropertyName<K> convert();
        abstract String getExpectedName();
        abstract String getExpectedHeaderName();

        PropertyArgs(String testCase, PropertyType<K> type){
            super(testCase);
            expectedType = type;
        }
        
        PropertyType<V> getExpectedValueType(){
            throw new UnsupportedOperationException("Method used only in MapPropertyArg objects");
        }
        
        String[] getMapInput(){
            throw new UnsupportedOperationException("Method used only in MapPropertyArg objects");
        }
        
        Map<? extends K, ? extends V> getExpectedResult(){
            throw new UnsupportedOperationException("Method used only in MapPropertyArg objects");
        }
    }

    static class OneArgProperty<K, V> extends PropertyArgs<K, V>{
        private final String name;

        OneArgProperty(String testCase, String name, PropertyType<K> type){
            super(testCase, type);
            this.name = name;
        }

        @Override
        PropertyName<K> convert() {
            return new PropertyName<>(name, expectedType);
        }

        @Override
        String getExpectedName() {
            return this.name;
        }

        @Override
        String getExpectedHeaderName() {
            return this.name;
        }
    }

    static class TwoArgProperty<K,V> extends PropertyArgs<K,V>{
        private final String name;
        private final String headerName;

        TwoArgProperty(String testCase, String name, String headerName, PropertyType<K> type){
            super(testCase, type);
            this.name = name;
            this.headerName = headerName;
        }

        @Override
        PropertyName<K> convert() {
            return new PropertyName<>(name, headerName, expectedType);
        }

        @Override
        String getExpectedName() {
            return this.name;
        }

        @Override
        String getExpectedHeaderName() {
            return this.headerName;
        }
    }
}

