package mentoring.datastructure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * Immutable description of a multiple property where the value associated with a key is its index
 * in the parsed property.
 * @param <K> type of the key in the key-value pairs stored in the property.
 */
public class IndexedPropertyName<K> extends MultiplePropertyName<K,Integer> {
    
    /**
     * Initialises a new IndexedPropertyName.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param keyType expected type of the property key
     */
    public IndexedPropertyName(String name, String headerName, PropertyType<K> keyType) {
        super(name, headerName, keyType, PropertyType.INTEGER, getParser(keyType));
    }
    
    /**
     * Initialises a new MultiplePropertyName.
     * @param name of the property in {@link Person} objects
     * @param keyType expected type of the property key
     */
    public IndexedPropertyName(String name, PropertyType<K> keyType) {
        super(name, name, keyType, PropertyType.INTEGER, getParser(keyType));
    }
    
    private static <K> Function<String[], Map<? extends K, ? extends Integer>> getParser(
            PropertyType<K> keyType){
        return input -> {
            Map<K, Integer> result = new HashMap<>();
            for(int i = 0; i < input.length; i++){
                K parsedKey = keyType.parse(input[i]);
                result.putIfAbsent(parsedKey, i);
            }
            return result;
        };
    };
}
