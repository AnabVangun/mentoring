package mentoring.io;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.io.datareader.DataReader;

/**
 * Parser used to build {@link PersonConfiguration} objects from configuration files.
 * <p>Instances of this class can be reused but are not thread-safe.
 */
public final class PersonConfigurationParser extends Parser<PersonConfiguration> {
    
    public PersonConfigurationParser(DataReader reader){
        super(reader);
    }
    
    @Override
    protected PersonConfiguration buildObject(Map<String, Object> data) 
            throws IllegalArgumentException {
        String configurationName = extractAttribute(data, "configurationName", String.class);
        Set<PropertyName<?>> properties = extractProperties(data, "properties");
        Set<MultiplePropertyName<?,?>> multipleProperties = 
                extractMultipleProperties(data, "multipleProperties");
        String separator = extractAttribute(data, "separator", String.class);
        String nameFormat = extractAttribute(data, "nameFormat", String.class);
        @SuppressWarnings("unchecked")
        List<String> nameProperties = (List<String>) extractAttribute(data, "nameProperties", 
                Object.class);
        assertValidNameDefinition(nameFormat, nameProperties);
        return new PersonConfiguration(configurationName, 
                Collections.unmodifiableSet(properties), 
                Collections.unmodifiableSet(multipleProperties), 
                separator, nameFormat, 
                Collections.unmodifiableList(nameProperties));
    }
    
    @SuppressWarnings("unchecked")
    private Set<PropertyName<?>> extractProperties(Map<String, Object> data, String propertyKey){
        SimplePropertyNameParser parser = new SimplePropertyNameParser();
        return parser.parsePropertyNames(
                (Iterable<Map<String, String>>) extractAttribute(data, propertyKey));
    }
    
    @SuppressWarnings("unchecked")
    private Set<MultiplePropertyName<?,?>> extractMultipleProperties(Map<String, Object> data, 
            String propertyKey){
        MultiplePropertyNameParser parser = new MultiplePropertyNameParser();
        return parser.parsePropertyNames(
                (Iterable<Map<String, String>>) extractAttribute(data, propertyKey));
    }
    
    private static void assertValidNameDefinition(String nameFormat, List<String> nameProperties){
        if (! PersonConfiguration.isValidNameDefinition(nameFormat, nameProperties)){
            throw new IllegalArgumentException(
                    "%s and %s do not constitute a valid name definition"
                            .formatted(nameFormat, nameProperties));
        }
    }
}
