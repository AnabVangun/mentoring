package mentoring.datastructure;

/**
 * Class used to build {@link SimplePropertyDescription} objects. A single builder can build several 
 * unrelated objects: the internal state is reinitialised after each creation to make sure that 
 * {@link SimplePropertyDescription} objects remain immutable.
 * This class is not thread-safe.
 */
public class SimplePropertyDescriptionBuilder extends 
        PropertyDescriptionBuilder<SimplePropertyDescriptionBuilder>{
    
    @Override
    protected SimplePropertyDescriptionBuilder getThis(){
        return this;
    }
    
    @Override
    public SimplePropertyDescription<?> build(){
        assertIsInitialised();
        SimplePropertyDescription<?> result = new SimplePropertyDescription<>(getName(), getHeaderName(), 
                getType());
        reset();
        return result;
    }
}
