package mentoring.datastructure;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Immutable description of a multiple property where the value associated with a key is irrelevant.
 * @param <K> type of the key in the key-value pairs stored in the property.
 */
public class SetPropertyName<K> extends MultiplePropertyName<K,Integer> {
    
    /**
     * Initialises a new SetPropertyName.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param keyType expected type of the property key
     */
    public SetPropertyName(String name, String headerName, PropertyType<K> keyType) {
        super(name, headerName, keyType, PropertyType.INTEGER, getParser(keyType));
    }
    
    private static final Cache<PropertyType, Object> cache = Cache.buildCache(PropertyType.class, 
            Object.class, 5, true);
    
    @SuppressWarnings("unchecked")
    private static <K> Function<String[], Map<K, Integer>> getParser(PropertyType<K> keyType){
        return (Function<String[], Map<K, Integer>>) cache.computeIfAbsent(keyType, 
                SetPropertyName::computeParser);
    }
    
    private static <K> Function<String[], Map<K, Integer>> computeParser(PropertyType<K> keyType){
        return input -> Arrays.stream(input).collect(Collectors.toMap(keyType::parse, 
                args -> 0));
    }
    
    @Override
    public String getStringRepresentation(Person person){
        return person.getPropertyAsSetOf(getName(), getType().getType()).toString();
    }
}
