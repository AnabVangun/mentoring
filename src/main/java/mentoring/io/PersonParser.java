package mentoring.io;

import mentoring.datastructure.Person;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.PersonBuilder;
/*
TODO: test this class.
First: implement a test in PersonConfiguration to check that the properties returned by 
getAllPropertiesHeaderNames are exactly all the properties
Constructor: 
    1. check that NPEs are thrown.
    2. check that IOExceptions are thrown if properties are missing
    3. check that no exception are thrown if all properties are present
ParseLine:
    1. check that parseline returns a person consistent with the line.

Finally, rewrite Person and PersonBuilder to use a map for the properties.
    Issues: where to define what type of treatment is expected by each class?
        Possibility: 
            Person has a Map<String, Object> for its properties and get(Class<T> class, String property)
            PersonBuilder has a method withProperty(String property, T value)
            PersonConfiguration has a Set<Property<Class<T>>> for its expected properties
            PersonParser has a Map<Class<T>, Function<String, T>> for the parsing of each type
        To add a new type, impact only on PersonConfiguration instance and PersonParser code.
Then, rewrite PersonConfiguration and refactor PersonParser
*/

public class PersonParser {
    private final Map<String, Integer> propertyNameIndices = new HashMap<>();
    private final PersonConfiguration configuration;
    private static final Set<String> TRUE_VALUES = Set.of("oui","vrai","yes","true");
    
    public PersonParser(PersonConfiguration configuration, String[] personFileHeader) 
            throws IOException{
        this.configuration = configuration;
        parseHeader(personFileHeader);
    }
    
    private void parseHeader(String[] header) throws IOException{
        Collection<String> neededProperties = 
                new HashSet<>(configuration.getAllPropertiesHeaderNames());
        for (int i = 0; i < header.length; i++){
            recordPropertyIndexIfNeeded(header[i], i, neededProperties);
        }
        failIfPropertiesAreMissing(header, neededProperties);
    }
    
    private void recordPropertyIndexIfNeeded(String propertyName, int index, 
            Collection<String> neededProperties){
        if (neededProperties.contains(propertyName)){
            propertyNameIndices.put(propertyName, index);
            neededProperties.remove(propertyName);
        }
    }
    
    private void failIfPropertiesAreMissing(String[] header, Collection<String> neededProperties)
            throws IOException{
        if (! neededProperties.isEmpty()){
            throw new IOException(String.format("Expected properties %s missing in header %s",
                    neededProperties, Arrays.toString(header)));
        }
    }
    
    public Person parseLine(String[] line) throws IOException{
        PersonBuilder builder = new PersonBuilder();
        configuration.getBooleanPropertiesNames().forEach(property -> {
            Boolean value = TRUE_VALUES.contains(
                    line[propertyNameIndices.get(property.getHeaderName())].toLowerCase());
            builder.withBooleanProperty(property.getName(), value);
        });
        configuration.getIntegerPropertiesNames().forEach(property -> {
            Integer value = Integer.parseInt(
                    line[propertyNameIndices.get(property.getHeaderName())]);
            builder.withIntegerProperty(property.getName(), value);
        });
        configuration.getStringPropertiesNames().forEach(property -> {
            builder.withStringProperty(property.getName(),
                    line[propertyNameIndices.get(property.getHeaderName())]);
        });
        configuration.getMultipleStringPropertiesNames().forEach(property -> {
            String[] splitValue = line[propertyNameIndices.get(property.getHeaderName())]
                    .split(configuration.getSeparator());
            Set<String> value = Set.of(splitValue);
            builder.withMultipleStringProperty(property.getName(), value);
        });
        Object[] nameValues = configuration.getNamePropertiesHeaderNames().stream()
                .map(property -> line[propertyNameIndices.get(property)]).toArray();
        builder.withFullName(String.format(configuration.getNameFormat(), nameValues));
        return builder.build();
    }
}
