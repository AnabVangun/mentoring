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
                    Assertions.assertAll(
                            () -> Assertions.assertAll(
                                    () -> configuration.getPropertiesNames().forEach(
                                            p -> Assertions.assertTrue(actual.contains(
                                                    p.getHeaderName()), 
                                                    () -> "Property " + p.getName() 
                                                            + " is missing from " + actual)
                                    )),
                            () -> Assertions.assertAll(
                                    () -> configuration.getMultiplePropertiesNames().forEach(
                                            p -> Assertions.assertTrue(actual.contains(
                                                    p.getHeaderName()),
                                                    () -> "Property " + p.getName() 
                                                            + " is missing from " + actual)
                                    )),
                            () -> Assertions.assertAll(
                                    () -> configuration.getNamePropertiesHeaderNames().forEach(
                                            s -> Assertions.assertTrue(actual.contains(s),
                                                    () -> "Property " + s
                                                            + " is missing from " + actual)))
                    );
                });
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

abstract class PersonConfigurationArgs extends TestArgs{
    PersonConfigurationArgs(String testCase){
        super(testCase);
    }
    
    abstract PersonConfiguration convert();
} 
