package mentoring.configuration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

interface PersonConfigurationTest<T extends PersonConfigurationArgs> extends TestFramework<T> {
    
    @TestFactory
    default Stream<DynamicNode> getAllPropertiesHeaderNames_sufficient(){
        return test("getAllPropertiesHeaderNames() contains the name of all required properties",
                args -> {
                    PersonConfiguration configuration = args.convert();
                    Collection<String> actual = 
                            configuration.getAllPropertiesHeaderNames();
                    Set<String> missing = new HashSet<>();
                    configuration.getPropertiesNames().forEach(p -> 
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
                    configuration.getPropertiesNames().forEach(p -> 
                            modifiable.remove(p.getHeaderName()));
                    configuration.getMultiplePropertiesNames().forEach(p -> 
                            modifiable.remove(p.getHeaderName()));
                    modifiable.removeAll(configuration.getNamePropertiesHeaderNames());
                    Assertions.assertTrue(modifiable.isEmpty(), "Collection " + modifiable 
                            + " should have been empty");
                });
    }
}

interface PersonConfigurationArgs {
    abstract PersonConfiguration convert();
} 
