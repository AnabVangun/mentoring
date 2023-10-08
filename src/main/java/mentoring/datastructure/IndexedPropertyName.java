package mentoring.datastructure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * Immutable description of a multiple property where the value associated with a key is its index
 * in the parsed property. If the key is present several times, only its lowest index is kept.
 * @param <K> type of the key in the key-value pairs stored in the property.
 */
public class IndexedPropertyName<K> extends MultiplePropertyName<K,Integer> {
    
    /**
     * Initialise a new IndexedPropertyName.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param keyType expected type of the property key
     */
    public IndexedPropertyName(String name, String headerName, PropertyType<K> keyType) {
        super(name, headerName, keyType, PropertyType.INTEGER);
    }
    
    private static <K> Function<String[], Map<K, Integer>> computeParser(PropertyType<K> keyType){
        return input -> {
                    Map<K, Integer> map = new HashMap<>();
                    for(int i = 0; i < input.length; i++){
                        K parsedKey = keyType.parse(input[i]);
                        map.putIfAbsent(parsedKey, i);
                    }
                    return map;  
        };
    }
    
    @Override
    public String getStringRepresentation(Person person){
        Map<K, Integer> map =  person.getPropertyAsMapOf(getName(), getType().getType(), 
                getValueType().getType());
        Object[] list = new Object[map.size()];
        for(Map.Entry<K, Integer> entry : map.entrySet()){
            list[entry.getValue()] = entry.getKey();
        }
        return Arrays.toString(list);
    }

    @Override
    public PropertyName<K> withHeaderName(String headerName) {
        return new IndexedPropertyName<>(getName(), headerName, getType());
    }

    @Override
    public Map<K, Integer> buildMap(String[] keys) {
        Map<K, Integer> result = new HashMap<>();
        for(int i = 0; i < keys.length; i++){
            K parsedKey = getType().parse(keys[i]);
            result.putIfAbsent(parsedKey, i);
        }
        return result;
    }
    
    @Override
    public boolean equals(Object o){
        return o instanceof IndexedPropertyName cast && attributeEquals(cast);
    }
    
    @Override
    public int hashCode(){
        return attributeHashCode()*31 + 1;
    }
}
