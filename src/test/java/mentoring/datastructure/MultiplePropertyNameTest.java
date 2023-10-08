package mentoring.datastructure;

import java.util.Map;
import java.util.stream.Stream;
import mentoring.datastructure.MultiplePropertyNameTest.MapPropertyArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

abstract class MultiplePropertyNameTest<T extends MapPropertyArgs> 
        extends AbstractPropertyNameTest<MultiplePropertyName<?,?>, T>{
    
    @TestFactory
    Stream<DynamicNode> getValueType(){
        return test("getValueType() returns the expected type", args -> 
                Assertions.assertEquals(args.getExpectedValueType(), 
                        args.convert().getValueType()));
    }
    
    @TestFactory
    Stream<DynamicNode> buildMap(){
        return test("buildMap() returns the expected map", args -> 
                Assertions.assertEquals(args.getExpectedResult(),
                        args.convert().buildMap(args.getMapInput())));
    }
    
    abstract static class MapPropertyArgs extends AbstractPropertyNameArgs<Object,
            MultiplePropertyName<? extends Object, ? extends Object>>{
        //TODO refactor rename MultiplePropertyNameArgs
        final PropertyType<?> valueType;
        final String[] mapInput;
        final Map<?, ?> expectedResult;

        MapPropertyArgs(String testCase, String name, String headerName, 
                PropertyType<?> keyType,
                PropertyType<?> valueType,
                String[] mapInput, Map<?, ?> expectedResult){
            super(testCase, name, headerName, keyType);
            this.valueType = valueType;
            this.mapInput = mapInput;
            this.expectedResult = expectedResult;
        }

        @Override
        protected abstract MultiplePropertyName<?, ?> convert();
        
        PropertyType<?> getExpectedValueType(){
            return valueType;
        }
        
        String[] getMapInput(){
            return mapInput;
        }
        
        Map<?, ?> getExpectedResult(){
            return expectedResult;
        }
    }
}
