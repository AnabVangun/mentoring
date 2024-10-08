package mentoring.datastructure;

/**
 * Class used to build {@link SimplePropertyDescription} objects. A single builder can build several unrelated 
 * objects: the internal state is reinitialised after each creation to make sure that 
 * {@link PropertyDescription} objects remain immutable.
 * This class is not thread-safe.
 * @param <T> self-type
 */
public abstract class PropertyDescriptionBuilder<T extends PropertyDescriptionBuilder<T>> {
    private String name = null;
    private String headerName = null;
    private PropertyType<?> type = null;
    private boolean initialised = false;
    
    /**
     * Return the name of the future property.
     */
    protected String getName(){
        assertIsInitialised();
        return name;
    }
    
    /**
     * Return the name of the column corresponding to the future property in CSV files.
     */
    protected String getHeaderName(){
        assertIsInitialised();
        return headerName;
    }
    
    /**
     * Return the type of data stored in the property.
     */
    protected PropertyType<?> getType(){
        assertIsInitialised();
        return type;
    }
    
    protected void assertIsInitialised(){
        if (! initialised){
            throw new IllegalStateException("Tried to call a method on an unitialised builder");
        }
    }
    
    /**
     * Prepare the future property.
     * @param name of the future property
     * @param type of data stored in the property
     * @return this instance for use in fluent API
     */
    public T prepare(String name, PropertyType<?> type){
        reset();
        this.name = name;
        this.type = type;
        headerName = name;
        initialised = true;
        return getThis();
    }
    
    /**
     * Reset the builder state. This method SHOULD be overridden by subclasses adding fields 
     * to reset.
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
    public T withHeaderName(String headerName) throws IllegalStateException{
        this.headerName = headerName;
        return getThis();
    }
    
    /**
     * All subclasses SHOULD implement this method with a simple "return this;".
     * @return the current object
     */
    protected abstract T getThis();
    
    /**
     * Build the actual property.
     * @return the PropertyDescription instance prepared by the builder.
     */
    public abstract PropertyDescription<?> build();
}
