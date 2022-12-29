package mentoring.io;

import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

/**
 * Parser used to build {@link PersonConfiguration} objects from configuration files.
 * <p>Instances of this class can be reused but are not thread-safe.
 */
public final class PersonConfigurationParser {
    private final Load yamlReader = new Load(LoadSettings.builder().build());
    private Map<String, Object> yamlData;
    /**
     * Use a {@link Reader} to build a {@link PersonConfiguration}.
     * @param reader data source representing in textual format the configuration
     * @return a person configuration to be used to parse persons from a file
     * @throws IllegalArgumentException if the content provided by the reader does not have the 
     * appropriate format.
     */
    public PersonConfiguration parse(Reader reader) throws IllegalArgumentException {
        Objects.requireNonNull(reader);
        yamlData = readYaml(reader);
        String configurationName = (String) extractAttribute("configurationName");
        Set<PropertyName<?>> properties = extractProperties("properties");
        Set<MultiplePropertyName<?,?>> multipleProperties = 
                extractMultipleProperties("multipleProperties");
        String separator = (String) extractAttribute("separator");
        String nameFormat = (String) extractAttribute("nameFormat");
        List<String> nameProperties = (List<String>) extractAttribute("nameProperties");
        assertValidNameDefinition(nameFormat, nameProperties);
        return new PersonConfigurationImplementation(configurationName, 
                Collections.unmodifiableSet(properties), 
                Collections.unmodifiableSet(multipleProperties), 
                separator, nameFormat, 
                Collections.unmodifiableList(nameProperties));
    }
    
    private Map<String, Object> readYaml(Reader reader){
        try {
            return (Map<String, Object>) yamlReader.loadFromReader(reader);
        } catch (YamlEngineException e){
            throw new IllegalArgumentException("Could not parse YAML content from reader", e);
        }
    }
    
    private Object extractAttribute(String propertyKey){
        if(yamlData.containsKey(propertyKey)){
            return yamlData.get(propertyKey);
        } else {
            throw new IllegalArgumentException("Property %s is missing.".formatted(propertyKey));
        }
    }
    
    private Set<PropertyName<?>> extractProperties(String propertyKey){
        SimplePropertyNameParser parser = new SimplePropertyNameParser();
        return parser.parsePropertyNames(
                (Iterable<Map<String, String>>) extractAttribute(propertyKey));
    }
    
    private Set<MultiplePropertyName<?,?>> extractMultipleProperties(String propertyKey){
        MultiplePropertyNameParser parser = new MultiplePropertyNameParser();
        return parser.parsePropertyNames(
                (Iterable<Map<String, String>>) extractAttribute(propertyKey));
    }
    
    private static void assertValidNameDefinition(String nameFormat, List<String> nameProperties){
        if (! PersonConfiguration.isValidNameDefinition(nameFormat, nameProperties)){
            throw new IllegalArgumentException(
                    "%s and %s do not constitute a valid name definition"
                            .formatted(nameFormat, nameProperties));
        }
    }
    
    /*
        TODO make PersonConfiguration an abstract class; define behaviour for all getters, make all
    properties final and have constructor do all the assignment.
    */
    private static class PersonConfigurationImplementation implements PersonConfiguration {
        private final String configurationName;
        private final Set<PropertyName<?>> properties;
        private final Set<MultiplePropertyName<?,?>> multipleProperties;
        private final String separator;
        private final String nameFormat;
        private final List<String> nameProperties;
        private final Collection<String> allPropertiesHeaderNames;
        
        PersonConfigurationImplementation(String configurationName,
                Set<PropertyName<?>> properties, 
                Set<MultiplePropertyName<?,?>> multipleProperties, 
                String separator, String nameFormat,
                List<String> nameProperties){
            this.configurationName = configurationName;
            Set<String> tmpAllProperties = new HashSet<>();
            this.properties = properties;
            this.properties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
            this.multipleProperties = multipleProperties;
            this.multipleProperties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
            this.separator = separator;
            this.nameFormat = nameFormat;
            this.nameProperties = nameProperties;
            tmpAllProperties.addAll(nameProperties);
            allPropertiesHeaderNames = Collections.unmodifiableCollection(tmpAllProperties);
    }
        @Override
        public String toString(){
            return configurationName;
        }
        
        @Override
        public Set<PropertyName<?>> getPropertiesNames() {
            return properties;
        }

        @Override
        public Set<MultiplePropertyName<?,?>> getMultiplePropertiesNames() {
            return multipleProperties;
        }

        @Override
        public String getSeparator() {
            return separator;
        }

        @Override
        public String getNameFormat() {
            return nameFormat;
        }

        @Override
        public List<String> getNamePropertiesHeaderNames() {
            return nameProperties;
        }

        @Override
        public Collection<String> getAllPropertiesHeaderNames() {
            return allPropertiesHeaderNames;
        }
        
    }
}
