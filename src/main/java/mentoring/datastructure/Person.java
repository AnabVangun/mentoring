package mentoring.datastructure;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
/**
 * Immutable class used to represent a single person. Person instances should be initialised through
 * a {@link PersonBuilder} object.
 */
public final class Person {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<String, Object> properties;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<String, Map<?, ?>> multipleProperties;
    private final String fullName;
    
    Person(Map<String, Object> properties, Map<String, Map<?,?>> multipleProperties, String name){
        this.properties = Collections.unmodifiableMap(properties);
        /*
        Caveat: the fact that the values of multipleProperties are immutable is guaranteed 
        by PersonBuilder.
        */
        this.multipleProperties = Collections.unmodifiableMap(multipleProperties);
        this.fullName = name;
    }
    
    public String getFullName(){
        return fullName;
    }
    
    /**
     * Returns the given property with the given type. An exception will be raised if the property
     * does not exist or cannot be cast.
     * @param <T> type to which the property must be cast
     * @param property name of the property to get
     * @param type  type to which the property must be cast
     * @return the value of the property cast to the given type.
     */
    public <T> T getPropertyAs(String property, Class<T> type) {
        if (!properties.containsKey(property)){
            throw new IllegalArgumentException(String.format(
                    "Person %s has no %s property", this, property));
        } else if (!type.isInstance(properties.get(property))){
            throw new ClassCastException(String.format(
                    "Property %s of person %s (%s) is not of required type, %s", property, this,
                    properties.get(property), type));
        }
        return type.cast(properties.get(property));
    }
    
    /**
     * Returns the given property as a set of elements with the given type. 
     * An exception will be raised if the property does not exist or cannot be cast.
     * @param <T> type to which the property elements must be cast
     * @param property name of the property to get
     * @param type  type to which the property must be cast
     * @return a set containing the values of the property cast to the given type.
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> getPropertyAsSetOf(String property, Class<T> type){
        return (Set<T>) getPropertyAsMapOf(property, type, Object.class).keySet();
    }
    
    /**
     * Returns the given property as a map of elements with the given types. 
     * An exception will be raised if the property does not exist or cannot be cast.
     * @param <K> type to which the property keys must be cast
     * @param <V> type to which the property values must be cast
     * @param property name of the property to get
     * @param keyType  type to which the property keys must be cast
     * @param valueType type to which the property values must be cast
     * @return a set containing the values of the property cast to the given type.
     */
    @SuppressWarnings("unchecked")
    public <K,V> Map<K,V> getPropertyAsMapOf(String property, Class<K> keyType, Class<V> valueType){
        if (!multipleProperties.containsKey(property)){
            throw new IllegalArgumentException(String.format(
                    "Person %s has no %s multiple property", this, property));
        }
        try {
            return (Map<K,V>) multipleProperties.get(property);
        } catch (ClassCastException e){
            throw new ClassCastException(String.format(
                    "Property %s of person %s (%s) is not of required types, %s and %s", 
                    property, this, properties.get(property), keyType, valueType));
        }
    }
    
    @Override
    public String toString(){
        return String.format("Person %s", fullName);
    }
    
    @Override
    public boolean equals(Object other){
        if(! (other instanceof Person cast)){
            return false;
        }
        return (fullName.equals(cast.fullName) 
                && properties.equals(cast.properties)
                && multipleProperties.equals(cast.multipleProperties));
    }
    
    @Override
    public int hashCode(){
        return fullName.hashCode() + 31*(properties.hashCode() + 31*multipleProperties.hashCode());
    }
}
