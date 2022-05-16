package mentoring.io;

import mentoring.datastructure.Person;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import mentoring.configuration.PojoPersonConfiguration;
import mentoring.datastructure.PersonBuilder;

public class PersonParser {
    private final Map<String, Integer> propertyIndices = new HashMap<>();
    private final PojoPersonConfiguration configuration;
    
    public PersonParser(PojoPersonConfiguration configuration, String[] personFileHeader) throws IOException{
        this.configuration = configuration;
        parseHeader(personFileHeader);
    }
    
    private void parseHeader(String[] header) throws IOException{
        Map<String, Integer> headerToIndex = mapHeaderToIndex(header);
        Collection<String> missingProperties = new ArrayList<>();
        for (String property: configuration.getAllPropertiesHeaderNames()){
            if (!headerToIndex.containsKey(property)){
                missingProperties.add(property);
            } else {
                propertyIndices.put(property, headerToIndex.get(property));
            }
        }
        if (! missingProperties.isEmpty()){
            throw new IOException(String.format("Missing property %s in header %s",
                    missingProperties, Arrays.toString(header)));
        }
    }
    
    private Map<String, Integer> mapHeaderToIndex(String[] header){
        Map<String, Integer> allIndices = new HashMap<>();
        for (int i = 0; i < header.length; i++){
            allIndices.put(header[i], i);
        }
        return allIndices;
    }
    
    public Person parseLine(String[] line) throws IOException{
        PersonBuilder builder = new PersonBuilder();
        configuration.getBooleanProperties().forEach(property -> {
            Boolean value = Set.of("oui","vrai","true","yes")
                .contains(line[propertyIndices.get(property.getHeaderName())].toLowerCase());
            builder.withBooleanProperty(property.getName(), value);
        });
        configuration.getIntegerProperties().forEach(property -> {
            Integer value = Integer.parseInt(line[propertyIndices.get(property.getHeaderName())]);
            builder.withIntegerProperty(property.getName(), value);
        });
        configuration.getStringProperties().forEach(property -> {
            builder.withStringProperty(property.getName(), 
                line[propertyIndices.get(property.getHeaderName())]);
        });
        configuration.getMultipleStringProperties().forEach(property -> {
            String[] splitValue = line[propertyIndices.get(property.getHeaderName())]
                .split(configuration.getSeparator());
            Set<String> value = Set.of(splitValue);
            builder.withMultipleStringProperty(property.getName(), value);
        });
        Object[] nameValues = configuration.getNamePropertiesHeaderNames().stream()
            .map(property -> line[propertyIndices.get(property)]).toArray();
        builder.withFullName(String.format(configuration.getNameFormat(), nameValues));
        return builder.build();
    }
}
