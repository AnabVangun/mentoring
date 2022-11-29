package mentoring.datastructure;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class CacheTest implements TestFramework<Object> {
    @Override
    public Stream<Object> argumentsSupplier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @TestFactory
    Stream<DynamicNode> buildCache_validInput(){
        return test(Stream.of(
                new CacheArgs<>("specific valid input", Integer.class, Boolean.class, 1),
                new CacheArgs<Integer, Boolean>("null input", null, null, 2)), "buildCache() succeeds", args -> 
                args.convert());
    }
    
    @TestFactory
    Stream<DynamicNode> buildCache_invalidInput(){
        return test(Stream.of(
                new CacheArgs<>("zero capacity", null, Float.class, 0),
                new CacheArgs<>("negative capacity", Double.class, null, -3)), 
                "buildCache() fails", args -> 
                        Assertions.assertThrows(IllegalArgumentException.class, 
                                () -> args.convert()));
    }

    @TestFactory
    Stream<DynamicNode> computeIfAbsent_valueNotInCache(){
        return test(Stream.of(new CacheArgs<>("specific test case", Integer.class, String.class, 2)),
                "computeIfAbsent() returns the expected result if value is not in cache", 
                args -> {
                    var cache = args.convert();
                    String expectedOutput = "foo";
                    String output = cache.computeIfAbsent(3, arg -> expectedOutput);
                    Assertions.assertEquals(expectedOutput, output);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> computeIfAbsent_valueInCache(){
        return test(Stream.of(new CacheArgs<>("specific test case", Boolean.class, Integer.class, 8)),
                "computeIfAbsent() returns the value in cache whenever possible",
                args -> {
                    var cache = args.convert();
                    Integer expectedOutput = 8754122;
                    Integer wrongOutput = 12345;
                    cache.computeIfAbsent(Boolean.TRUE, arg -> expectedOutput);
                    //computeIfAbsent fetch previous result rather than computing.
                    Integer output = cache.computeIfAbsent(Boolean.TRUE, arg -> wrongOutput);
                    Assertions.assertEquals(expectedOutput, output);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> computeIfAbsent_valueRemovedFromCache_Removed(){
        final int CAPACITY = 3;
        return test(Stream.of(new CacheArgs<>("capacity of one", Integer.class, String.class, 1),
                new CacheArgs<>("capacity of " + CAPACITY, Integer.class, String.class, CAPACITY)),
                "computeIfAbsent() removes old values from cache",
                args -> {
                    var cache = args.convert();
                    String expectedOutput = "bar";
                    String wrongOutput = "foo";
                    //put enough values to delete first value from cache
                    for (int i = 0; i < CAPACITY+1; i++){
                        cache.computeIfAbsent(i, arg -> wrongOutput);
                    }
                    String output = cache.computeIfAbsent(0, arg -> expectedOutput);
                    /*
                    If the returned value is expectedOutput, it means that the value was recomputed 
                    and thus that the key was flushed from the cache.
                    */
                    Assertions.assertEquals(expectedOutput, output);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> computeIfAbsent_valueRemovedFromCache_PutBackInCache(){
        final int CAPACITY = 3;
        return test(Stream.of(new CacheArgs<>("capacity of one", Integer.class, String.class, 1),
                new CacheArgs<>("capacity of " + CAPACITY, Integer.class, String.class, CAPACITY)),
                "computeIfAbsent() puts back in cache old removed values if asked again",
                args -> {
                    var cache = args.convert();
                    String expectedOutput = "foo";
                    String wrongOutput = "bar";
                    for (int i = 0; i < CAPACITY + 1; i++){
                        cache.computeIfAbsent(i, arg -> wrongOutput);
                    }
                    cache.computeIfAbsent(0, arg -> expectedOutput);
                    String output = cache.computeIfAbsent(0, arg -> wrongOutput);
                    Assertions.assertEquals(expectedOutput, output);
                });
    }
    
    static class CacheArgs<K,V> extends TestArgs {
        final Class<K> keyType;
        final Class<V> valueType;
        final int capacity;
        
        CacheArgs(String testCase, Class<K> keyType, Class<V> valueType, int capacity){
            super(testCase);
            this.keyType = keyType;
            this.valueType = valueType;
            this.capacity = capacity;
        }
        
        Cache<K,V> convert(){
            return Cache.buildCache(keyType, valueType, capacity);
        }
    }
}
