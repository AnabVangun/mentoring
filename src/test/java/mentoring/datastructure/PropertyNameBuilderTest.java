package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.PropertyNameBuilderTest.PropertyNameBuilderArgs;

class PropertyNameBuilderTest extends 
        AbstractPropertyNameBuilderTest<PropertyNameBuilderArgs, PropertyNameBuilder>{
    //TODO refactor rename SimplePropertyNameBuilderTest
    @Override
    public Stream<PropertyNameBuilderArgs> argumentsSupplier(){
        return Stream.of(new PropertyNameBuilderArgs("standard case", "foo", "bar", 
                PropertyType.SIMPLIFIED_LOWER_STRING));
    }  

    @Override
    protected PropertyName<?> provideNewProperty(String name, String headerName, PropertyType<?> type) {
        return new SimplePropertyName<>(name, headerName, type);
    }
    
    
    static class PropertyNameBuilderArgs extends AbstractPropertyNameBuilderArgs<PropertyNameBuilder>{
        
        PropertyNameBuilderArgs(String testCase, String name, String headerName, 
                PropertyType<?> type) {
            super(testCase, name, headerName, type);
        }
        
        @Override
        protected PropertyNameBuilder convert() {
            return new SimplePropertyNameBuilder();
        }
        
        @Override
        protected PropertyName<?> supplyExpectedProperty(boolean withHeaderName){
            return new SimplePropertyName<>(name, withHeaderName ? headerName : name, type);
        }
    }
}
