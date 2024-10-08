package mentoring.datastructure;

import java.util.Map;
import java.util.stream.Stream;
import mentoring.datastructure.MultiplePropertyDescriptionTest.MultiplePropertyDescriptionArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

abstract class MultiplePropertyDescriptionTest<T extends MultiplePropertyDescriptionArgs> 
        extends PropertyDescriptionTest<MultiplePropertyDescription<?,?>, T>{
    
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
    
    abstract static class MultiplePropertyDescriptionArgs extends PropertyDescriptionArgs<Object,
            MultiplePropertyDescription<? extends Object, ? extends Object>>{
        final PropertyType<?> valueType;
        final String[] mapInput;
        final Map<?, ?> expectedResult;

        MultiplePropertyDescriptionArgs(String testCase, String name, String headerName, 
                PropertyType<?> keyType,
                PropertyType<?> valueType,
                String[] mapInput, Map<?, ?> expectedResult){
            super(testCase, name, headerName, keyType);
            this.valueType = valueType;
            this.mapInput = mapInput;
            this.expectedResult = expectedResult;
        }

        @Override
        protected abstract MultiplePropertyDescription<?, ?> convert();
        
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
