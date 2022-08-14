package mentoring.datastructure;

import java.util.HashMap;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

final class PropertyNameTest implements TestFramework<PropertyNameTest.PropertyArgs>{

    @Override
    public Stream<PropertyArgs> argumentsSupplier() {
        return Stream.of(new OneArgProperty("Simple one-argument property", "name&", Integer.class),
                new OneArgProperty("One-argument property with empty name", "", Boolean.class),
                new TwoArgProperty("Simple two-argument property", "propriété", "headerName", 
                        Float.class),
                new TwoArgProperty("Two-argument property with equal names", "name", "name", 
                        String.class),
                new TwoArgProperty("Two-argument property with empty name", "", "header_name", 
                        Set.class),
                new TwoArgProperty("Two-argument property with empty headerName", "name", "", 
                        Long.class),
                new TwoArgProperty("Two-argument property with empty names", "", "", HashMap.class)
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
    
    static abstract class PropertyArgs<T> extends TestArgs{
        final Class<T> expectedType;
        
        abstract PropertyName<T> convert();
        abstract String getExpectedName();
        abstract String getExpectedHeaderName();

        PropertyArgs(String testCase, Class<T> type){
            super(testCase);
            expectedType = type;
        }
    }

    static class OneArgProperty<T> extends PropertyArgs<T>{
        private final String name;

        OneArgProperty(String testCase, String name, Class<T> type){
            super(testCase, type);
            this.name = name;
        }

        @Override
        PropertyName<T> convert() {
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

    static class TwoArgProperty<T> extends PropertyArgs<T>{
        private final String name;
        private final String headerName;

        TwoArgProperty(String testCase, String name, String headerName, Class<T> type){
            super(testCase, type);
            this.name = name;
            this.headerName = headerName;
        }

        @Override
        PropertyName<T> convert() {
            return new PropertyName(name, headerName, expectedType);
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

