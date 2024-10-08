package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.SimplePropertyDescriptionBuilderTest.SimplePropertyDescriptionBuilderArgs;

class SimplePropertyDescriptionBuilderTest extends 
        PropertyDescriptionBuilderTest<SimplePropertyDescriptionBuilderArgs, SimplePropertyDescriptionBuilder>{
    @Override
    public Stream<SimplePropertyDescriptionBuilderArgs> argumentsSupplier(){
        return Stream.of(new SimplePropertyDescriptionBuilderArgs("standard case", "foo", "bar", 
                PropertyType.SIMPLIFIED_LOWER_STRING));
    }  

    @Override
    protected PropertyDescription<?> provideNewProperty(String name, String headerName, PropertyType<?> type) {
        return new SimplePropertyDescription<>(name, headerName, type);
    }
    
    
    static class SimplePropertyDescriptionBuilderArgs extends PropertyDescriptionBuilderArgs<SimplePropertyDescriptionBuilder>{
        
        SimplePropertyDescriptionBuilderArgs(String testCase, String name, String headerName, 
                PropertyType<?> type) {
            super(testCase, name, headerName, type);
        }
        
        @Override
        protected SimplePropertyDescriptionBuilder convert() {
            return new SimplePropertyDescriptionBuilder();
        }
        
        @Override
        protected PropertyDescription<?> supplyExpectedProperty(boolean withHeaderName){
            return new SimplePropertyDescription<>(name, withHeaderName ? headerName : name, type);
        }
    }
}
