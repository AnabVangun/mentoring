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
Constructor: 
    1. check that NPEs are thrown.
    2. check that IOExceptions are thrown if properties are missing
    3. check that no exception are thrown if all properties are present
ParseLine:
    1. check that parseline returns a person consistent with the line.
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
        configuration.getPropertiesNames().forEach(property ->
            builder.withProperty(property.getName(), parseProperty(
                    line[propertyNameIndices.get(property.getHeaderName())], property.getType())));
        configuration.getMultiplePropertiesNames().forEach(property -> {
            String[] splitValue = line[propertyNameIndices.get(property.getHeaderName())]
                    .split(configuration.getSeparator());
            Object[] parsedValue = new Object[splitValue.length];
            for (int i = 0; i < splitValue.length; i++){
                parsedValue[i] = parseProperty(splitValue[i], property.getType());
            }
            builder.withPropertySet(property.getName(), Set.of(parsedValue));
        });
        Object[] nameValues = configuration.getNamePropertiesHeaderNames().stream()
                .map(property -> line[propertyNameIndices.get(property)]).toArray();
        builder.withFullName(String.format(configuration.getNameFormat(), nameValues));
        return builder.build();
    }
    
    private <T> Object parseProperty (String value, Class<T> expectedType){
            if (expectedType.isAssignableFrom(Boolean.class)){
                return TRUE_VALUES.contains(value.toLowerCase());
            } else if (expectedType.isAssignableFrom(Integer.class)){
                return Integer.parseInt(value);
            } else if (expectedType.isAssignableFrom(String.class)){
                return value;
            } else {
                throw new UnsupportedOperationException("No parser available for properties of class " 
                        + expectedType.getCanonicalName());
            }
    }
}
