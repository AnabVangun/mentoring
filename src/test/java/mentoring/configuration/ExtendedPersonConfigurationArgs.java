package mentoring.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;

public interface ExtendedPersonConfigurationArgs extends SimplePersonConfigurationArgs {
    
    String getExpectedName();
    
    Set<PropertyName<?>> getExpectedProperties();
    
    Set<MultiplePropertyName<?,?>> getExpectedMultipleProperties();
    
    String getExpectedSeparator();
    
    String getExpectedNameFormat();
    
    List<String> getExpectedNameProperties();
    
    static void assertResultAsExpected(ExtendedPersonConfigurationArgs expected, 
            PersonConfiguration actual){
        Assertions.assertAll("Some part of PersonConfiguration is not as expected", 
                    () -> Assertions.assertEquals(expected.getExpectedName(), actual.toString()),
                    () -> assertPropertiesAsExpected(expected.getExpectedProperties(),
                            actual.getPropertiesNames()),
                    () -> assertMultiplePropertiesAsExpected(expected.getExpectedMultipleProperties(),
                            actual.getMultiplePropertiesNames()),
                    () -> Assertions.assertEquals(expected.getExpectedSeparator(),
                            actual.getSeparator()),
                    () -> Assertions.assertEquals(expected.getExpectedNameFormat(),
                            actual.getNameFormat()),
                    () -> Assertions.assertEquals(expected.getExpectedNameProperties(), 
                            actual.getNamePropertiesHeaderNames()),
                    () -> Assertions.assertTrue(PersonConfiguration.isValidNameDefinition(
                            actual.getNameFormat(), actual.getNamePropertiesHeaderNames())));
    }
    
    static void assertPropertiesAsExpected(Set<PropertyName<?>> expected, 
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
    
    static void assertMultiplePropertiesAsExpected(Set<MultiplePropertyName<?,?>> expected,
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
