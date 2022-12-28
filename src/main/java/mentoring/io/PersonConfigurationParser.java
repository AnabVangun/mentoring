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
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

/**
 * Parser used to build {@link PersonConfiguration} objects from configuration files.
 */
public final class PersonConfigurationParser {
    /**
     * Use a {@link Reader} to build a {@link PersonConfiguration}.
     * @param reader data source representing in textual format the configuration
     * @return a person configuration to be used to parse persons from a file
     * @throws IllegalArgumentException if the content provided by the reader does not have the 
     * appropriate format.
     */
    public PersonConfiguration parse(Reader reader) throws IllegalArgumentException {
        Objects.requireNonNull(reader);
        Load load = new Load(LoadSettings.builder().build());
        Map<String, Object> loaded;
        try {
            loaded = (Map<String, Object>) load.loadFromReader(reader);
        } catch (YamlEngineException e){
            throw new IllegalArgumentException("Could not parse YAML content from reader", e);
        }
        //TODO refactor method
        String configurationName;
        if(loaded.containsKey("configurationName")){
            configurationName = (String) loaded.get("configurationName");
        } else {
            throw new IllegalArgumentException("Property configurationName is missing");
        }
        Set<PropertyName<?>> properties;
        if(loaded.containsKey("properties")){
            properties = new HashSet<>();
            for(List<String> propertyToParse : (List<List<String>>) loaded.get("properties")){
                if (propertyToParse.size() != 3){
                    throw new IllegalArgumentException("Expected list of size 3, received " 
                            + propertyToParse);
                }
                properties.add(new PropertyName<>(propertyToParse.get(0),
                        propertyToParse.get(1), PropertyType.valueOf(propertyToParse.get(2))));
            }
        } else {
            throw new IllegalArgumentException("Property properties is missing");
        }
        Set<MultiplePropertyName<?,?>> multipleProperties;
        if(loaded.containsKey("multipleProperties")){
            multipleProperties = new HashSet<>();
            for (List<String> propertyToParse : 
                    (List<List<String>>) loaded.get("multipleProperties")) {
                if(propertyToParse.size() != 4){
                    throw new IllegalArgumentException("Expected list of size 4, received " 
                            + propertyToParse);
                }
                String name = propertyToParse.get(0);
                String headerName = propertyToParse.get(1);
                PropertyType<?> type = PropertyType.valueOf(propertyToParse.get(2));
                MultiplePropertyName<?,?> newProperty = 
                        switch(propertyToParse.get(3).toLowerCase()){
                            case "set" -> new SetPropertyName(name, headerName, type);
                            case "indexed" -> new IndexedPropertyName(name, headerName, type);
                            default -> throw new IllegalArgumentException("not implemented yet");
                };
                multipleProperties.add(newProperty); 
            }
        } else {
            throw new IllegalArgumentException("Property multipleProperties is missing");
        }
        String separator;
        if(loaded.containsKey("separator")){
            separator = (String) loaded.get("separator");
        } else {
            throw new IllegalArgumentException("Property separator is missing");
        }
        String nameFormat;
        if(loaded.containsKey("nameFormat")){
            nameFormat = (String) loaded.get("nameFormat");
        } else {
            throw new IllegalArgumentException("Property nameFormat is missing");
        }
        List<String> nameProperties;
        if(loaded.containsKey("nameProperties")){
            nameProperties = (List<String>) loaded.get("nameProperties");
        } else {
            throw new IllegalArgumentException("Property nameProperties is missing");
        }
        if (! PersonConfiguration.isValidNameDefinition(nameFormat, nameProperties)){
            throw new IllegalArgumentException(
                    "%s and %s do not constitute a valid name definition"
                            .formatted(nameFormat, nameProperties));
        }
        return new PersonConfigurationImplementation(configurationName, 
                Collections.unmodifiableSet(properties), 
                Collections.unmodifiableSet(multipleProperties), 
                separator, nameFormat, 
                Collections.unmodifiableList(nameProperties));
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
