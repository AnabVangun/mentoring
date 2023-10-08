package mentoring.datastructure;

/**
 * Class used to build {@link SimplePropertyName} objects. A single builder can build several 
 * unrelated objects: the internal state is reinitialised after each creation to make sure that 
 * {@link SimplePropertyName} objects remain immutable.
 * This class is not thread-safe.
 */
public class SimplePropertyNameBuilder extends PropertyNameBuilder<SimplePropertyNameBuilder>{
    
    @Override
    protected SimplePropertyNameBuilder getThis(){
        return this;
    }
    
    @Override
    public SimplePropertyName<?> build(){
        assertIsInitialised();
        SimplePropertyName<?> result = new SimplePropertyName<>(getName(), getHeaderName(), 
                getType());
        reset();
        return result;
    }
}
