package mentoring.io;

import java.util.ArrayList;
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
    
    private final static String CONFIGURATION_NAME_KEY = "configurationName";
    private final static String PROPERTIES_KEY = "properties";
    private final static String MULTIPLE_PROPERTIES_KEY = "multipleProperties";
    private final static String SEPARATOR_KEY = "separator";
    private final static String NAME_FORMAT_KEY = "nameFormat";
    private final static String NAME_PROPERTIES_KEY = "nameProperties";
    private final static Set<String> EXPECTED_KEYS = Set.of(CONFIGURATION_NAME_KEY, PROPERTIES_KEY,
            MULTIPLE_PROPERTIES_KEY, SEPARATOR_KEY, NAME_FORMAT_KEY, NAME_PROPERTIES_KEY);
    
    @Override
    protected PersonConfiguration buildObject(Map<String, Object> data) 
            throws IllegalArgumentException {
        String configurationName = extractAttribute(data, CONFIGURATION_NAME_KEY, String.class);
        Set<PropertyName<?>> properties = extractProperties(data, PROPERTIES_KEY, 
                new SimplePropertyNameDecoder());
        Set<MultiplePropertyName<?,?>> multipleProperties = 
                extractProperties(data, MULTIPLE_PROPERTIES_KEY, new MultiplePropertyNameDecoder());
        String separator = extractAttribute(data, SEPARATOR_KEY, String.class);
        String nameFormat = extractAttribute(data, NAME_FORMAT_KEY, String.class);
        List<String> nameProperties = (List<String>) extractAttributeList(data, NAME_PROPERTIES_KEY, 
                String.class);
        return new PersonConfiguration(configurationName, 
                Collections.unmodifiableSet(properties), 
                Collections.unmodifiableSet(multipleProperties), 
                separator, nameFormat, 
                Collections.unmodifiableList(nameProperties));
    }
    
    @SuppressWarnings("unchecked")
    private static <T extends PropertyName<?>> Set<T> extractProperties(Map<String, Object> data, 
            String propertyKey, PropertyNameDecoder<T> parser){
        return parser.decodePropertyNames(
                (Iterable<Map<String, String>>) extractAttribute(data, propertyKey));
    }
    
    @Override
    protected List<String> registerSpecificErrors(Map<String, Object> data){
        List<String> errors = new ArrayList<>();
        registerInvalidNameDefinitionIfAppropriate(data, errors);
        return errors;
    }
    
    private static void registerInvalidNameDefinitionIfAppropriate(Map<String, Object> data, 
            List<String> errorsFound){
        if(data.containsKey(NAME_FORMAT_KEY) && data.containsKey(NAME_PROPERTIES_KEY)){
            String nameFormat = extractAttribute(data, NAME_FORMAT_KEY, String.class);
            List<String> nameProperties = extractAttributeList(data, NAME_PROPERTIES_KEY, 
                    String.class);
            if (! PersonConfiguration.isValidNameDefinition(nameFormat, nameProperties)){
                errorsFound.add(
                        "%s and %s do not constitute a valid name definition"
                                .formatted(nameFormat, nameProperties));
            }
        }
    }
    
    @Override
    protected Set<String> getExpectedKeys(){
        return EXPECTED_KEYS;
    }
}
