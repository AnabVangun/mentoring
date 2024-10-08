package mentoring.datastructure;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Immutable description of a multiple property where the value associated with a key is irrelevant.
 * @param <K> type of the key in the key-value pairs stored in the property.
 */
public class SetPropertyDescription<K> extends MultiplePropertyDescription<K,Integer> {
    
    /**
     * Initialises a new SetPropertyDescription.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param keyType expected type of the property key
     */
    public SetPropertyDescription(String name, String headerName, PropertyType<K> keyType) {
        super(name, headerName, keyType, PropertyType.INTEGER);
    }
    
    @Override
    public String getStringRepresentation(Person person){
        return person.getPropertyAsSetOf(getName(), getType().getType()).toString();
    }

    @Override
    public SetPropertyDescription<K> withHeaderName(String headerName) {
        return new SetPropertyDescription<>(getName(), headerName, getType());
    }

    @Override
    public Map<K, Integer> buildMap(String[] keys) {
        return Arrays.stream(keys).collect(Collectors.toMap(getType()::parse, args -> 0));
    }
    
    @Override
    public boolean equals(Object o){
        return o instanceof SetPropertyDescription cast && attributeEquals(cast);
    }
    
    @Override
    public int hashCode(){
        return attributeHashCode()*31 + 2;
    }
}
