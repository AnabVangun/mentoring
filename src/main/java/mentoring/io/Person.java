package mentoring.io;

import com.opencsv.bean.CsvBindAndJoinByName;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

public class Person {
    /**
     * TODO think about the types. To have it easy here, all properties are stored as collections of
     * strings. What about properties that should be a unique integer value? We will need to parse 
     * them each time they need to be used in a criterion.
     * Should we split this into different variables corresponding to the cross-product of 
     * (unique value, collection of values)*(Integer, Boolean, String, Date)?
     * Idea: use PersonConfig to define what a person look like and PersonParser to parse CSV files.
     */
    
    @CsvBindAndJoinByName(column = ".*", elementType = String.class, 
        mapType = HashSetValuedHashMap.class, required = true)
    private MultiValuedMap<String, String> properties;
    private Map<String, Collection<String>> cachedUnmodifiableProperties = new HashMap<>();
    private Map<String, Set<String>> cachedUnmodifiableSetProperties = new HashMap<>();
    
    public Collection<String> getProperty(String property){
        cacheUnmodifiablePropertyIfNeeded(property);
        return cachedUnmodifiableProperties.get(property);
    }
    
    public Set<String> getPropertyAsSet(String property, String separator){
        cacheUnmodifiableSetPropertyIfNeeded(property, separator);
        return cachedUnmodifiableSetProperties.get(property);
    }
    
    private void cacheUnmodifiablePropertyIfNeeded(String property){
        if (! cachedUnmodifiableProperties.containsKey(property)){
            cachedUnmodifiableProperties.put(property, 
                    Collections.unmodifiableCollection(properties.get(property)));
        }
    }
    
    private void cacheUnmodifiableSetPropertyIfNeeded(String property, String separator){
        if (! cachedUnmodifiableSetProperties.containsKey(property)){
            cachedUnmodifiableSetProperties.put(property,
                    Collections.unmodifiableSet(mapToSet(property, separator)));
        }
    }
    
    private Set<String> mapToSet(String property, String separator){
        Set<String> result = new HashSet<>();
        for (String line : properties.get(property)){
            result.addAll(Arrays.asList(line.split(separator)));
        }
        return result;
    }
    
    public String getFullName(){
        //TODO use PersonConfiguration to determine which properties to use.
        return String.format("%s %s (X%s)",this.getProperty("Prénom").iterator().next(),
                this.getProperty("Nom").iterator().next(),
                this.getProperty("Promotion").iterator().next());
    }
}
