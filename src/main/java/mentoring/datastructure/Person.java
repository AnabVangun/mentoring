package mentoring.datastructure;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
/**
 * Immutable class used to represent a single person. Person instances should be initialised through
 * a {@link PersonBuilder} object.
 */
public final class Person {
    private final Map<String, Object> properties;
    private final Map<String, Set<?>> multipleProperties;
    private final String fullName;
    
    Person(Map<String, Object> properties, Map<String, Set<?>> multipleProperties, String name){
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
    
    public int getIntegerProperty(String property){
        return getPropertyAs(property, Integer.class);
    }
    
    public boolean getBooleanProperty(String property){
        return getPropertyAs(property, Boolean.class);
    }
    
    public String getStringProperty(String property){
        return getPropertyAs(property, String.class);
    }
    
    public Set<String> getMultipleStringProperty(String property){
        return getPropertyAsSetOf(property, String.class);
    }
    
    public <T> T getPropertyAs(String property, Class<T> type){
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
    
    @SuppressWarnings("unchecked")
    public <T> Set<T> getPropertyAsSetOf(String property, Class<T> type){
        if (!multipleProperties.containsKey(property)){
            throw new IllegalArgumentException(String.format(
                    "Person %s has no %s multiple property", this, property));
        } else if (!(multipleProperties.get(property) instanceof Set)){
            //FIXME: in if-condition, java 11 does not allow Set<T> after instanceof.
            throw new ClassCastException(String.format(
                    "Property %s of person %s (%s) is not of required type, %s", property, this,
                    properties.get(property), type));
        }
        return (Set<T>) multipleProperties.get(property);
    }
    
    @Override
    public String toString(){
        return String.format("Person %s", fullName);
    }
}
