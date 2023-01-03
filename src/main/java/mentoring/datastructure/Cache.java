package mentoring.datastructure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;

/**
 * Standard contract for a cache.
 * @param <K> the type of keys used to access values
 * @param <V> the type of values stored
 */
public interface Cache<K,V> {
    /**
     * Returns function(key). If key is already in the cache, return the value without computing it.
     * When this method returns, key will be in the cache and another key may have been dropped.
     * @param key input on which function must be computed.
     * @param function used to compute the value.
     * @return function(key).
     */
    V computeIfAbsent(K key, Function<? super K,? extends V> function);
    
    /**
     * Generates a new empty cache.
     * @param <K> the type of keys used to access values
     * @param <V> the type of values stored
     * @param keyType the type of keys used to access values
     * @param valueType the type of values stored
     * @param capacity the maximum number of simultaneous keys in the cache
     * @return a new empty cache with the given parameters
     * @throws IllegalArgumentException if the capacity is too low
     */
    static <K,V> Cache<K,V> buildCache(Class<K> keyType, Class<V> valueType, 
            int capacity) throws IllegalArgumentException {
        return buildCache(keyType, valueType, capacity, false);
    }
    
    /**
     * Generates a new empty cache.
     * @param <K> the type of keys used to access values
     * @param <V> the type of values stored
     * @param keyType the type of keys used to access values
     * @param valueType the type of values stored
     * @param capacity the maximum number of simultaneous keys in the cache
     * @param gcCompatible if true, the values can be garbage-collected and the mappings removed
     * @return a new empty cache with the given parameters
     * @throws IllegalArgumentException if the capacity is too low
     */
    static <K,V> Cache<K,V> buildCache(Class<K> keyType, Class<V> valueType, int capacity, 
            boolean gcCompatible) throws IllegalArgumentException{
        if (capacity < 1){
            throw new IllegalArgumentException(
                    "Illegal capacity, expected at least 1 and received " + capacity);
        }
        if (gcCompatible){
            return new GcCompatibleCache<>(keyType, valueType, capacity);
        } else {
            return new LruCache<>(keyType, valueType, capacity);
        }
    }
}

/**
 * Cache implementation based on the Least Recently Used strategy.
 */
final class LruCache<K,V> implements Cache<K,V> {
    final private Map<K,V> map;
    final private int capacity;
    
    LruCache(Class<? extends K> keyType, Class<? extends V> valueType, int capacity){
        map = new LimitedCapacityLinkedHashMap<>(capacity*2, 0.75f, true);
        this.capacity = capacity;
    }
    
    private class LimitedCapacityLinkedHashMap<K,V> extends LinkedHashMap<K,V> {
        LimitedCapacityLinkedHashMap(int capacity, float loadFactor, boolean accessOrder){
            super(capacity, loadFactor, accessOrder);
        }
        
        @Override
        protected boolean removeEldestEntry(Map.Entry<K,V> eldest){
            return size() > capacity;
        }
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K,? extends V> function) {
        return map.computeIfAbsent(key, function);
    }
}

/**
 * Cache implementation that lets mapping be garbage-collected when values are not referenced 
 * outside of the cache anymore.
 */
final class GcCompatibleCache<K, V> implements Cache<K,V> {
    final private Map<K,V> map;
    
    GcCompatibleCache(Class<? extends K> keyType, Class<? extends V> valueType, int capacity){
        map = new ReferenceMap<>(AbstractReferenceMap.ReferenceStrength.HARD, 
                AbstractReferenceMap.ReferenceStrength.WEAK, capacity, 0.75f);
    }
    
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> function) {
        return map.computeIfAbsent(key, function);
    }
}