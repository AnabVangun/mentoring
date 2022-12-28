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
        return Stream.of(
                new PropertyArgs<>("Simple property", "propriété", "headerName", 
                        PropertyType.STRING),
                new PropertyArgs<>("Property with equal names", "name", "name", 
                        PropertyType.INTEGER),
                new PropertyArgs<>("Property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN),
                new PropertyArgs<>("Property with empty headerName", "name", "", 
                        PropertyType.STRING),
                new PropertyArgs<>("Property with empty names", "", "", 
                        PropertyType.INTEGER)
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

