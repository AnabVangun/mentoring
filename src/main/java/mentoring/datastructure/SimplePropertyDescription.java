package mentoring.datastructure;

/**
 * Simple implementation of PropertyDescription.
 * 
 * @param <T> the type of element stored in the property.
 */
public class SimplePropertyDescription<T> extends PropertyDescription<T>{
    /**
     * Initialises a new PropertyDescription.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param type expected type of the property
     */
    public SimplePropertyDescription(String name, String headerName, PropertyType<T> type){
        super(name, headerName, type);
    }
    
    @Override
    public SimplePropertyDescription<T> withHeaderName(String headerName){
        return new SimplePropertyDescription<>(getName(), headerName, getType());
    }
    
    
    @Override
    public boolean equals(Object o){
        return o instanceof SimplePropertyDescription cast && attributeEquals(cast);
    }
    
    @Override
    public int hashCode(){
        return attributeHashCode();
    }
    
    /**
     * Get a String representation for the value associated with this property.
     * @param person containing the value
     * @return a String representation of the value
     * @throws IllegalArgumentException if the Person does not contain the property
     */
    @Override
    public String getStringRepresentation(Person person){
        return person.getPropertyAs(getName(), getType().getType()).toString();
    }
}
