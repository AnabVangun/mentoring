package mentoring.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

public interface ExtendedPersonConfigurationTest<T extends ExtendedPersonConfigurationArgs>
        extends SimplePersonConfigurationTest<T> {
    
    String builderName();
    
    @TestFactory
    default Stream<DynamicNode> assertPersonConfigurationAsExpected(){
        return test(builderName() + " returns the expected PersonConfiguration", args -> {
            PersonConfiguration actual = args.convert();
            Assertions.assertAll("Some part of PersonConfiguration is not as expected", 
                    () -> Assertions.assertEquals(args.getExpectedName(), actual.toString()),
                    () -> assertPropertiesAsExpected(args.getExpectedProperties(),
                            actual.getPropertiesNames()),
                    () -> assertMultiplePropertiesAsExpected(args.getExpectedMultipleProperties(),
                            actual.getMultiplePropertiesNames()),
                    () -> Assertions.assertEquals(args.getExpectedSeparator(),
                            actual.getSeparator()),
                    () -> Assertions.assertEquals(args.getExpectedNameFormat(),
                            actual.getNameFormat()),
                    () -> Assertions.assertEquals(args.getExpectedNameProperties(), 
                            actual.getNamePropertiesHeaderNames()),
                    () -> Assertions.assertTrue(PersonConfiguration.isValidNameDefinition(
                            actual.getNameFormat(), actual.getNamePropertiesHeaderNames())));
        });
    }
    
    default void assertPropertiesAsExpected(Set<PropertyName<?>> expected, 
            Set<PropertyName<?>> actual){
        String nameSeparator = "###@###";
        Map<String, PropertyType<?>> expectedAsMap = new HashMap<>();
        expected.forEach(item -> expectedAsMap.put(
                item.getName() + nameSeparator + item.getHeaderName(), item.getType()));
        Map<String, PropertyType<?>> actualAsMap = new HashMap<>();
        actual.forEach(item -> actualAsMap.put(
                item.getName() + nameSeparator + item.getHeaderName(), item.getType()));
        Assertions.assertEquals(expectedAsMap, actualAsMap);
    }
    
    default void assertMultiplePropertiesAsExpected(Set<MultiplePropertyName<?,?>> expected,
            Set<MultiplePropertyName<?,?>> actual){
        String nameSeparator = "###@###";
        Map<String, Pair<PropertyType<?>, PropertyType<?>>> expectedAsMap = new HashMap<>();
        expected.forEach(item -> expectedAsMap.put(
                item.getClass() + nameSeparator + item.getName() + nameSeparator + item.getHeaderName(), 
                Pair.of(item.getType(), item.getValueType())));
         Map<String, Pair<PropertyType<?>, PropertyType<?>>> actualAsMap = new HashMap<>();
        actual.forEach(item -> actualAsMap.put(
                item.getClass() + nameSeparator + item.getName() + nameSeparator + item.getHeaderName(), 
                Pair.of(item.getType(), item.getValueType())));
        Assertions.assertEquals(expectedAsMap, actualAsMap);
    }
}
