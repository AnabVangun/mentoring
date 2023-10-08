package mentoring.datastructure;

/**
 * Class used to build {@link MultiplePropertyName} objects. A single builder can build several 
 * unrelated objects: the internal state is reinitialised after each creation to make sure that 
 * {@link MultiplePropertyName} objects remain immutable.
 * This class is not thread-safe.
 */
public class MultiplePropertyNameBuilder extends PropertyNameBuilder<MultiplePropertyNameBuilder> {
    private AggregationType aggregation;
    private boolean aggregationSet = false;
    
    @Override
    protected MultiplePropertyNameBuilder getThis(){
        return this;
    }
    
    @Override
    protected void reset(){
        super.reset();
        aggregationSet = false;
    }
    
    /**
     * Set an aggregation method for the multiple values. This method is mandatory: calls to 
     * {@link #build() } will fail if the method has not been called.
     * @param aggregation the type of aggregation of the property
     * @return this instance for use in a fluent API
     */
    public MultiplePropertyNameBuilder setAggregation(AggregationType aggregation){
        this.aggregation = aggregation;
        aggregationSet = true;
        return this;
    }
    
    @Override
    public MultiplePropertyName<?,?> build(){
        assertIsInitialised();
        assertAggregrationTypeIsSet();
        MultiplePropertyName<?,?> property = switch(aggregation){
            case INDEXED -> new IndexedPropertyName<>(getName(), getHeaderName(), getType());
            case SET -> new SetPropertyName<>(getName(), getHeaderName(), getType());
        };
        reset();
        return property;
    }
    
    private void assertAggregrationTypeIsSet(){
        if (! aggregationSet){
            throw new IllegalStateException(
                    "Tried to call a method on unsufficiently initialised builder");
        }
    }
}
