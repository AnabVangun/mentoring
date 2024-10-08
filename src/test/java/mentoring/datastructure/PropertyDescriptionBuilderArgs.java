package mentoring.datastructure;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import test.tools.TestArgs;

abstract class PropertyDescriptionBuilderArgs<T extends PropertyDescriptionBuilder<T>> extends TestArgs{
    protected final String name;
    protected final String headerName;
    protected final PropertyType<?> type;

    public PropertyDescriptionBuilderArgs(String testCase, String name, String headerName, 
            PropertyType<?> type) {
        super(testCase);
        this.name = name;
        this.headerName = headerName;
        this.type = type;
    }
    
    protected abstract T convert();
    
    @SuppressWarnings("unchecked")
    T prepared(){
        return (T) convert().prepare(name, type);
    }
    
    @SuppressWarnings("unchecked")
    protected T readyToBuildWithoutOptionalParameters(){
        return (T) prepared();
    }
    
    @SuppressWarnings("unchecked")
    protected T readyToBuild(){
        return (T) prepared().withHeaderName(headerName);
    }

    @SuppressWarnings("unchecked")
    T built(){
        T builder = readyToBuild();
        builder.build();
        return (T) builder;
    }

    protected Stream<Executable> supplyAssertionsBuilderAsExpected(T actual, 
            boolean withHeaderName){
        return Stream.of(
                () -> Assertions.assertEquals(name, actual.getName()),
                () -> Assertions.assertEquals(withHeaderName ? headerName : name, 
                        actual.getHeaderName()),
                () -> Assertions.assertEquals(type, actual.getType())
        );
    }

    protected final void assertPropertyAsExpected(PropertyDescription<?> actual, 
            boolean withHeaderName){
        Assertions.assertEquals(supplyExpectedProperty(withHeaderName), actual);
    }
    
    /**
     * Override this method to return the PropertyDescription encapsulated in this.
     * @param withHeaderName whether the expected PropertyDescription must have a specific header name
     * @return the expected PropertyDescription
     */
    protected abstract PropertyDescription<?> supplyExpectedProperty(boolean withHeaderName);
}
