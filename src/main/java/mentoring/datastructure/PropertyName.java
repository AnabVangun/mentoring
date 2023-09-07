package mentoring.datastructure;

/**
 * Immutable description of a property.
 * 
 * @param <T> the type of element stored in the property.
 */
public class PropertyName<T> {
    private final String headerName;
    private final String name;
    private final PropertyType<T> type;
    
    /**
     * Initialises a new PropertyName.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param type expected type of the property
     */
    public PropertyName(String name, String headerName, PropertyType<T> type){
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
    
    public PropertyName<T> withHeaderName(String headerName){
        return new PropertyName<>(name, headerName, type);
    }
    
    @Override
    public boolean equals(Object o){
        return o.getClass().equals(PropertyName.class) && attributeEquals((PropertyName) o);
    }
    
    protected boolean attributeEquals(PropertyName other){
        return name.equals(other.name) && headerName.equals(other.headerName) 
                && type.equals(other.type);
    }
    
    @Override
    public int hashCode(){
        return (name.hashCode() * 31 + headerName.hashCode()) * 31 + type.hashCode();
    }
    
    /**
     * Get a String representation for the value associated with this property.
     * @param person containing the value
     * @return a String representation of the value
     * @throws IllegalArgumentException if the Person does not contain the property
     */
    public String getStringRepresentation(Person person){
        return person.getPropertyAs(name, type.getType()).toString();
    }
}
