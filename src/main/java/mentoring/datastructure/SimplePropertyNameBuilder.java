package mentoring.datastructure;

/**
 * Class used to build {@link SimplePropertyName} objects. A single builder can build several 
 * unrelated objects: the internal state is reinitialised after each creation to make sure that 
 * {@link SimplePropertyName} objects remain immutable.
 * This class is not thread-safe.
 */
public class SimplePropertyNameBuilder extends PropertyNameBuilder{
    
    @Override
    public SimplePropertyNameBuilder prepare(String name, PropertyType<?> type){
        super.prepare(name, type);
        return this;
    }
    
    @Override
    public SimplePropertyNameBuilder withHeaderName(String headerName){
        super.withHeaderName(headerName);
        return this;
    }
    
    @Override
    public SimplePropertyName<?> build(){
        checkState();
        SimplePropertyName<?> result = new SimplePropertyName<>(getName(), getHeaderName(), 
                getType());
        reset();
        return result;
    }
}
