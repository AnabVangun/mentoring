package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.PropertyNameBuilderTest.SimplePropertyNameBuilderArgs;

class PropertyNameBuilderTest extends 
        AbstractPropertyNameBuilderTest<SimplePropertyNameBuilderArgs, SimplePropertyNameBuilder>{
    //TODO refactor rename SimplePropertyNameBuilderTest
    @Override
    public Stream<SimplePropertyNameBuilderArgs> argumentsSupplier(){
        return Stream.of(new SimplePropertyNameBuilderArgs("standard case", "foo", "bar", 
                PropertyType.SIMPLIFIED_LOWER_STRING));
    }  

    @Override
    protected PropertyName<?> provideNewProperty(String name, String headerName, PropertyType<?> type) {
        return new SimplePropertyName<>(name, headerName, type);
    }
    
    
    static class SimplePropertyNameBuilderArgs extends AbstractPropertyNameBuilderArgs<SimplePropertyNameBuilder>{
        
        SimplePropertyNameBuilderArgs(String testCase, String name, String headerName, 
                PropertyType<?> type) {
            super(testCase, name, headerName, type);
        }
        
        @Override
        protected SimplePropertyNameBuilder convert() {
            return new SimplePropertyNameBuilder();
        }
        
        @Override
        protected PropertyName<?> supplyExpectedProperty(boolean withHeaderName){
            return new SimplePropertyName<>(name, withHeaderName ? headerName : name, type);
        }
    }
}
