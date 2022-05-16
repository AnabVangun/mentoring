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
        Collection<String> pendingProperties = 
                new HashSet<>(configuration.getAllPropertiesHeaderNames());
        for (int i = 0; i < header.length; i++){
            recordPropertyIndexIfPending(header[i], i, pendingProperties);
        }
        failIfPropertiesAreMissing(header, pendingProperties);
    }
    
    private void recordPropertyIndexIfPending(String propertyName, int index, 
            Collection<String> pendingProperties){
        if (pendingProperties.contains(propertyName)){
            propertyNameIndices.put(propertyName, index);
            pendingProperties.remove(propertyName);
        }
    }
    
    private void failIfPropertiesAreMissing(String[] header, Collection<String> pendingProperties)
            throws IOException{
        if (! pendingProperties.isEmpty()){
            throw new IOException(String.format("Missing property %s in header %s",
                    pendingProperties, Arrays.toString(header)));
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
