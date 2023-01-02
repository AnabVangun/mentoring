package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.PropertyNameBuilderTest.PropertyNameBuilderArgs;

class PropertyNameBuilderTest extends 
        AbstractPropertyNameBuilderTest<PropertyNameBuilderArgs, PropertyNameBuilder>{
    
    @Override
    public Stream<PropertyNameBuilderArgs> argumentsSupplier(){
        return Stream.of(new PropertyNameBuilderArgs("standard case", "foo", "bar", 
                PropertyType.SIMPLIFIED_LOWER_STRING));
    }  
    
    
    static class PropertyNameBuilderArgs extends AbstractPropertyNameBuilderArgs<PropertyNameBuilder>{
        
        PropertyNameBuilderArgs(String testCase, String name, String headerName, 
                PropertyType<?> type) {
            super(testCase, name, headerName, type);
        }
        
        @Override
        protected PropertyNameBuilder convert() {
            return new PropertyNameBuilder();
        }
    }
}
