package mentoring.datastructure;

/**
 * Class used to build {@link PropertyName} objects. A single builder can build several unrelated 
 * objects: the internal state is reinitialised after each creation to make sure that 
 * {@link PropertyName} objects remain immutable.
 * This class is not thread-safe.
 */
public class PropertyNameBuilder {
    
    private String name = null;
    private String headerName = null;
    private PropertyType<?> type = null;
    private boolean initialised = false;
    
    /**
     * Return the name of the future property.
     */
    protected String getName(){
        checkState("getName");
        return name;
    }
    
    /**
     * Return the name of the column corresponding to the future property in CSV files.
     */
    protected String getHeaderName(){
        checkState("getHeaderName");
        return headerName;
    }
    
    /**
     * Return the type of data stored in the property.
     */
    protected PropertyType<?> getType(){
        checkState("getType");
        return type;
    }
    
    protected void checkState(String method){
        if (! initialised){
            throw new IllegalStateException("Tried to call %s() on an unitialised builder"
                    .formatted(method));
        }
    }
    
    /**
     * Prepare the future property.
     * @param name of the future property
     * @param type of data stored in the property
     * @return this instance for use in fluent API
     */
    public PropertyNameBuilder prepare(String name, PropertyType<?> type){
        reset();
        this.name = name;
        this.type = type;
        headerName = name;
        initialised = true;
        return this;
    }
    
    /**
     * Reset the builder state. This method should probably be overridden by subclasses.
     */
    protected void reset(){
        initialised = false;
    }
    
    /**
     * Set a custom header name for the future property. This method is optional: by default, the
     * name provided in {@link #prepare(java.lang.String, mentoring.datastructure.PropertyType) } 
     * will be used as a header name.
     * @param headerName the name of the column corresponding to the future property in CSV files.
     * @return this instance for use in fluent API
     * @throws IllegalStateException when calling this method before 
     * {@link #prepare(java.lang.String, mentoring.datastructure.PropertyType) }
     */
    public PropertyNameBuilder withHeaderName(String headerName) throws IllegalStateException{
        this.headerName = headerName;
        return this;
    }
    
    /**
     * Build the actual property.
     * @return the PropertyName instance prepared by the builder.
     */
    public PropertyName<?> build(){
        checkState("build");
        PropertyName<?> result = new PropertyName<>(name, headerName, type);
        reset();
        return result;
    }
}