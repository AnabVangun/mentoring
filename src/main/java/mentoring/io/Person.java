package mentoring.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Person {
    
    private final Map<String, Integer> integerProperties = new HashMap<>();
    private final Map<String, Boolean> booleanProperties = new HashMap<>();
    private final Map<String, String> stringProperties = new HashMap<>();
    private final Map<String, Set<String>> multipleStringProperties = new HashMap<>();
    private final Map<String, Set<String>> cachedUnmodifiableSetProperties = new HashMap<>();
    String fullName;
    
    void setMultipleProperty(String property, Set<String> values){
        if (cachedUnmodifiableSetProperties.containsKey(property)){
            cachedUnmodifiableSetProperties.remove(property);
        }
        multipleStringProperties.put(property, values);
    }
    
    public Set<String> getMultipleStringProperty(String property){
        cacheUnmodifiableSetPropertyIfNeeded(property);
        return cachedUnmodifiableSetProperties.get(property);
    }
    
    private void cacheUnmodifiableSetPropertyIfNeeded(String property){
        if (! cachedUnmodifiableSetProperties.containsKey(property)){
            cachedUnmodifiableSetProperties.put(property,
                    Collections.unmodifiableSet(multipleStringProperties.get(property)));
        }
    }
    
    public String getFullName(){
        return fullName;
    }
    
    void setIntegerProperty(String property, Integer value){
        integerProperties.put(property, value);
    }
    
    public int getIntegerProperty(String property){
        return integerProperties.get(property);
    }
    
    void setBooleanProperty(String property, Boolean value){
        booleanProperties.put(property, value);
    }
    
    public boolean getBooleanProperty(String property){
        return booleanProperties.get(property);
    }
    
    public String getStringProperty(String property){
        return stringProperties.get(property);
    }
    
    void setStringProperty(String property, String value){
        stringProperties.put(property, value);
    }
}
