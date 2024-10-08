package mentoring.datastructure;

/**
 * Class used to build {@link MultiplePropertyDescription} objects. A single builder can build several 
 * unrelated objects: the internal state is reinitialised after each creation to make sure that 
 * {@link MultiplePropertyDescription} objects remain immutable.
 * This class is not thread-safe.
 */
public class MultiplePropertyDescriptionBuilder extends 
        PropertyDescriptionBuilder<MultiplePropertyDescriptionBuilder> {
    private AggregationType aggregation;
    private boolean aggregationSet = false;
    
    @Override
    protected MultiplePropertyDescriptionBuilder getThis(){
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
    public MultiplePropertyDescriptionBuilder setAggregation(AggregationType aggregation){
        this.aggregation = aggregation;
        aggregationSet = true;
        return this;
    }
    
    @Override
    public MultiplePropertyDescription<?,?> build(){
        assertIsInitialised();
        assertAggregrationTypeIsSet();
        MultiplePropertyDescription<?,?> property = switch(aggregation){
            case INDEXED -> new IndexedPropertyDescription<>(getName(), getHeaderName(), getType());
            case SET -> new SetPropertyDescription<>(getName(), getHeaderName(), getType());
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
