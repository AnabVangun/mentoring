package mentoring.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

/**
 * Tests for all the {@link PersonConfiguration} implementations.
 * @param <T> the actual SimplePersonConfigurationArgs implementation.
 * @see {@link ExtendedPersonConfigurationTest} for a more extensive test suite.
 */
public interface SimplePersonConfigurationTest<T extends SimplePersonConfigurationArgs> 
        extends TestFramework<T> {
    
    @TestFactory
    default Stream<DynamicNode> getAllPropertiesHeaderNames_sufficient(){
        return test("getAllPropertiesHeaderNames() contains the name of all required properties",
                args -> {
                    PersonConfiguration configuration = args.convert();
                    Collection<String> actual = 
                            configuration.getAllPropertiesHeaderNames();
                    Set<String> missing = new HashSet<>();
                    configuration.getSimplePropertiesNames().forEach(p -> 
                            addStringIfMissing(p.getHeaderName(), actual, p.getName(), missing));
                    configuration.getMultiplePropertiesNames().forEach(p -> 
                            addStringIfMissing(p.getHeaderName(), actual, p.getName(), missing));
                    configuration.getNamePropertiesHeaderNames().forEach(s -> 
                            addStringIfMissing(s, actual, s, missing));
                    Assertions.assertTrue(missing.isEmpty(), "Properties " + missing 
                            + " are missing from " + actual);
                });
    }
    
    private static void addStringIfMissing(String stringToCheck, Collection<String> collectionToCheck,
            String stringToAdd, Collection<String> collector){
        if(!collectionToCheck.contains(stringToCheck)){
            collector.add(stringToAdd);
        }
    }
    
    @TestFactory
    default Stream<DynamicNode> getAllPropertiesHeaderNames_necessary(){
        return test("getAllPropertiesHeaderNames() contains only the name of required properties",
                args -> {
                    PersonConfiguration configuration = args.convert();
                    Collection<String> actual = 
                            configuration.getAllPropertiesHeaderNames();
                    Set<String> modifiable = new HashSet<>(actual);
                    configuration.getSimplePropertiesNames().forEach(p -> 
                            modifiable.remove(p.getHeaderName()));
                    configuration.getMultiplePropertiesNames().forEach(p -> 
                            modifiable.remove(p.getHeaderName()));
                    modifiable.removeAll(configuration.getNamePropertiesHeaderNames());
                    Assertions.assertTrue(modifiable.isEmpty(), "Collection " + modifiable 
                            + " should have been empty");
                });
    }
    
    @TestFactory
    default Stream<DynamicNode> isValidNameDefinition_validInput(){
        return test(Stream.of(Pair.of("no placeholder", new ArrayList<String>()),
                Pair.of("one %s placeholder", List.of("first")),
                Pair.of("two %s placeholders %S", List.of("one", "two"))),
                "isValidNameDefinition() returns true on valid input", args -> 
                        Assertions.assertTrue(PersonConfiguration
                                .isValidNameDefinition(args.getLeft(), args.getRight())));
    }
    
    @TestFactory
    default Stream<DynamicNode> isValidNameDefinition_invalidInput(){
        return test(Stream.of(Pair.of("one placeholder %s", new ArrayList<String>()),
                Pair.of("two placeholders %s%s", List.of("only one")),
                Pair.of("no placeholder", List.of("too many"))),
                "isValidNameDefinition() returns false on invalid input", args ->
                        Assertions.assertFalse(PersonConfiguration
                                .isValidNameDefinition(args.getLeft(),args.getRight())));
    }
}