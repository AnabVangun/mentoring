package mentoring.datastructure;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class Person {
    private final Map<String, Integer> integerProperties;
    private final Map<String, Boolean> booleanProperties;
    private final Map<String, String> stringProperties;
    private final Map<String, Set<String>> multipleStringProperties;
    private final String fullName;
    
    Person(Map<String, Integer> integerProperties, 
            Map<String, Boolean> booleanProperties,
            Map<String, String> stringProperties, 
            Map<String, Set<String>> multipleStringProperties,
            String name){
        this.integerProperties = Collections.unmodifiableMap(integerProperties);
        this.booleanProperties = Collections.unmodifiableMap(booleanProperties);
        this.stringProperties = Collections.unmodifiableMap(stringProperties);
        this.multipleStringProperties = Collections.unmodifiableMap(multipleStringProperties);
        this.fullName = name;
    }
    
    public String getFullName(){
        return fullName;
    }
    
    public int getIntegerProperty(String property){
        if (!integerProperties.containsKey(property)){
            throw new IllegalArgumentException(String.format(
                    "Person %s has no %s integer property", this, property));
        }
        return integerProperties.get(property);
    }
    
    public boolean getBooleanProperty(String property){
        if (!booleanProperties.containsKey(property)){
            throw new IllegalArgumentException(String.format(
                    "Person %s has no %s boolean property", this, property));
        }
        return booleanProperties.get(property);
    }
    
    public String getStringProperty(String property){
        if (!stringProperties.containsKey(property)){
            throw new IllegalArgumentException(String.format(
                    "Person %s has no %s string property", this, property));
        }
        return stringProperties.get(property);
    }
    
    public Set<String> getMultipleStringProperty(String property){
        if (!multipleStringProperties.containsKey(property)){
            throw new IllegalArgumentException(String.format(
                    "Person %s has no %s multiple string property", this, property));
        }
        return multipleStringProperties.get(property);
    }
    
}
