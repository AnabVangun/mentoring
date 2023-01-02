package mentoring.datastructure;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to build {@link MultiplePropertyName} objects. A single builder can build several 
 * unrelated objects: the internal state is reinitialised after each creation to make sure that 
 * {@link MultiplePropertyName} objects remain immutable.
 * This class is not thread-safe.
 */
public class MultiplePropertyNameBuilder extends PropertyNameBuilder {
    private AggregationType aggregation;
    private boolean aggregationSet = false;
    
    private static enum AggregationType {
        INDEXED("indexed"),
        SET("set");
        
        private final String stringValue;
        private AggregationType(String s){
            this.stringValue = s;
        }
        
        private static final Map<String, AggregationType> lookup = new HashMap<>();
        static {
            for(AggregationType value : AggregationType.values()){
                lookup.put(value.stringValue, value);
            }
        }
        
        static AggregationType lookup(String value){
            String simplified = value.toLowerCase();
            if(! lookup.containsKey(simplified)){
                throw new IllegalArgumentException("Aggregation type %s is invalid, valid values"
                        + " are %s".formatted(value, lookup.keySet()));
            }
            return lookup.get(simplified);
        }
    }
    
    @Override
    public MultiplePropertyNameBuilder prepare(String name, PropertyType<?> type){
        super.prepare(name, type);
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
     * @throws IllegalArgumentException if {@code aggregation} has an unknown value.
     */
    public MultiplePropertyNameBuilder setAggregation(String aggregation) 
            throws IllegalArgumentException{
        this.aggregation = AggregationType.lookup(aggregation);
        aggregationSet = true;
        return this;
    }
    
    @Override
    public MultiplePropertyName<?,?> build(){
        checkState("build");
        checkAggregation("build");
        MultiplePropertyName<?,?> property = switch(aggregation){
            case INDEXED -> new IndexedPropertyName<>(getName(), getHeaderName(), getType());
            case SET -> new SetPropertyName<>(getName(), getHeaderName(), getType());
        };
        reset();
        return property;
    }
    
    private void checkAggregation(String method){
        if (! aggregationSet){
            throw new IllegalStateException("Tried to call %s() on an unitialised builder"
                    .formatted(method));
        }
    }
}
