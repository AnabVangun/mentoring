package mentoring.io;

import mentoring.datastructure.Person;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.PersonBuilder;

final class PersonParser {
    private final Map<String, Integer> propertyNameIndices = new HashMap<>();
    private final PersonConfiguration configuration;
    
    PersonParser(PersonConfiguration configuration, String[] personFileHeader) 
            throws IOException{
        this.configuration = configuration;
        parseHeader(personFileHeader);
    }
    
    private void parseHeader(String[] header) throws IOException{
        Collection<String> neededProperties = 
                new HashSet<>(configuration.getAllPropertiesHeaderNames());
        for (int i = 0; i < header.length; i++){
            failIfDuplicateProperty(header, i);
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
    
    private void failIfDuplicateProperty(String[] header, int index)
            throws IOException{
        String propertyName = header[index];
        if (propertyNameIndices.containsKey(propertyName)){
            throw new IOException(String.format(
                    "Found duplicate property %s in header %s at indices %s and %s",
                    propertyName, Arrays.toString(header), propertyNameIndices.get(propertyName), 
                    index));
        }
    }
    
    private void failIfPropertiesAreMissing(String[] header, Collection<String> neededProperties)
            throws IOException{
        if (! neededProperties.isEmpty()){
            throw new IOException(String.format("Expected properties %s missing in header %s",
                    neededProperties, Arrays.toString(header)));
        }
    }
    
    Person parseLine(String[] line){
        PersonBuilder builder = new PersonBuilder();
        configuration.getPropertiesNames().forEach(property ->
            builder.withProperty(property.getName(), property.getType().parse(
                    line[propertyNameIndices.get(property.getHeaderName())])));
        configuration.getMultiplePropertiesNames().forEach(property -> {
            String[] splitValue = line[propertyNameIndices.get(property.getHeaderName())]
                    .split(configuration.getSeparator());
            Object[] parsedValue = new Object[splitValue.length];
            for (int i = 0; i < splitValue.length; i++){
                parsedValue[i] = property.getType().parse(splitValue[i]);
            }
            builder.withPropertySet(property.getName(), 
                    Arrays.stream(parsedValue).collect(Collectors.toSet()));
        });
        Object[] nameValues = configuration.getNamePropertiesHeaderNames().stream()
                .map(property -> line[propertyNameIndices.get(property)]).toArray();
        builder.withFullName(String.format(configuration.getNameFormat(), nameValues));
        return builder.build();
    }
}
