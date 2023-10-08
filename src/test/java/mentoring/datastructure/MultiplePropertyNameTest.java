package mentoring.datastructure;

import java.util.Map;
import java.util.stream.Stream;
import mentoring.datastructure.MultiplePropertyNameTest.MultiplePropertyNameArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

abstract class MultiplePropertyNameTest<T extends MultiplePropertyNameArgs> 
        extends PropertyNameTest<MultiplePropertyName<?,?>, T>{
    
    @TestFactory
    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    Stream<DynamicNode> getValueType(){
        return test("getValueType() returns the expected type", args -> 
                Assertions.assertEquals(args.getExpectedValueType(), 
                        args.convert().getValueType()));
    }
    
    @TestFactory
    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    Stream<DynamicNode> buildMap(){
        return test("buildMap() returns the expected map", args -> 
                Assertions.assertEquals(args.getExpectedResult(),
                        args.convert().buildMap(args.getMapInput())));
    }
    
    abstract static class MultiplePropertyNameArgs extends PropertyNameArgs<Object,
            MultiplePropertyName<? extends Object, ? extends Object>>{
        final PropertyType<?> valueType;
        final String[] mapInput;
        final Map<?, ?> expectedResult;

        MultiplePropertyNameArgs(String testCase, String name, String headerName, 
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
