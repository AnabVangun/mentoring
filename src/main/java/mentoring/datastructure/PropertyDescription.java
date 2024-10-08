package mentoring.datastructure;

/**
 * Immutable description of a property.
 * 
 * @param <T> the type of element stored in the property.
 */
public abstract class PropertyDescription<T> {
    private final String headerName;
    private final String name;
    private final PropertyType<T> type;
    
    /**
     * Initialises a new PropertyDescription.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param type expected type of the property
     */
    public PropertyDescription(String name, String headerName, PropertyType<T> type){
        this.headerName = headerName;
        this.name = name;
        this.type = type;
    }
    
    /** Returns the name used for this property in {@link Person} objects. */
    public String getName(){
        return name;
    }
    
    /** Returns the name used for this property in the header of data files. */
    public String getHeaderName(){
        return headerName;
    }
    
    public PropertyType<T> getType(){
        return type;
    }
    
    /**
     * Build a new PropertyDescription with a different header name.
     * All other properties are identical to
     * the initial property.
     * @param headerName the new header name
     * @return a new PropertyDescription with the specified header name
     */
    public abstract PropertyDescription<T> withHeaderName(String headerName);
    
    /**
     * Get a String representation for the value associated with this property.
     * @param person containing the value
     * @return a String representation of the value
     * @throws IllegalArgumentException if the Person does not contain the property
     */
    public abstract String getStringRepresentation(Person person);
    
    /**
     * Check that all attributes of the other PropertyDescription are identical to this one's.
     * @param other PropertyDescription to compare
     * @return true if the two PropertyDescription have the same attributes
     */
    final protected boolean attributeEquals(PropertyDescription other){
        return name.equals(other.name) && headerName.equals(other.headerName) 
                && type.equals(other.type);
    }
    
    /**
     * Compute a hash code based on the attributes of the PropertyDescription.
     * @return a value that can be used as a hash code
     */
    final protected int attributeHashCode(){
        return (getName().hashCode() * 31 + getHeaderName().hashCode()) * 31 + getType().hashCode();
    }
}
