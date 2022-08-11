package mentoring.datastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

final class PropertyNameTest implements TestFramework<PropertyNameTest.PropertyArgs>{

    @Override
    public Stream<PropertyArgs> argumentsSupplier() {
        return Stream.of(new OneArgProperty("Simple one-argument property", "name&"),
                new OneArgProperty("One-argument property with empty name", ""),
                new TwoArgProperty("Simple two-argument property", "propriété", "headerName"),
                new TwoArgProperty("Two-argument property with equal names", "name", "name"),
                new TwoArgProperty("Two-argument property with empty name", "", "header_name"),
                new TwoArgProperty("Two-argument property with empty headerName", "name", ""),
                new TwoArgProperty("Two-argument property with empty names", "", "")
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
    
    static abstract class PropertyArgs extends TestArgs{
        abstract PropertyName convert();
        abstract String getExpectedName();
        abstract String getExpectedHeaderName();

        PropertyArgs(String testCase){
            super(testCase);
        }
    }

    static class OneArgProperty extends PropertyArgs{
        private final String name;

        OneArgProperty(String testCase, String name){
            super(testCase);
            this.name = name;
        }

        @Override
        PropertyName convert() {
            return new PropertyName(name);
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

    static class TwoArgProperty extends PropertyArgs{
        private final String name;
        private final String headerName;

        TwoArgProperty(String testCase, String name, String headerName){
            super(testCase);
            this.name = name;
            this.headerName = headerName;
        }

        @Override
        PropertyName convert() {
            return new PropertyName(name, headerName);
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

