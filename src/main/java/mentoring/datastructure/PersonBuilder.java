package mentoring.datastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class used to build {@link Person} objects. A single builder can build several unrelated objects:
 * the internal state is reinitialised after each creation to make sure that {@link Person} objects
 * remain immutable.
 * This class is not thread-safe.
 */
public final class PersonBuilder {
    public static final String DEFAULT_NAME = "DEFAULT_NAME";
    private Map<String, Object> properties;
    private Map<String, Map<?,?>> multipleProperties;
    private String name;
    
    public PersonBuilder(){
        clear();
    }
    
    private void clear(){
        properties = new HashMap<>();
        multipleProperties = new HashMap<>();
        name = DEFAULT_NAME;
    }
    
    public PersonBuilder withProperty(String property, Object value){
        properties.put(Objects.requireNonNull(property), Objects.requireNonNull(value));
        return this;
    }
    
    public <K,V> PersonBuilder withPropertyMap(String property, Map<K,V> entries){
        multipleProperties.put(Objects.requireNonNull(property),
                Map.copyOf(entries));
        return this;
    }
    
    public PersonBuilder withFullName(String name){
        this.name = Objects.requireNonNull(name);
        return this;
    }
    
    public Person build(){
        Person result = new Person(properties, multipleProperties, name);
        clear();
        return result;
    }
}
