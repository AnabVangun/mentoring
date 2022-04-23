package mentoring.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import mentoring.configuration.PersonConfiguration;

public class PersonParser {
    private final Map<String, Integer> propertyIndices = new HashMap<>();
    private final PersonConfiguration configuration;
    
    public PersonParser(PersonConfiguration configuration, String[] personFileHeader) throws IOException{
        this.configuration = configuration;
        parseHeader(personFileHeader);
    }
    
    private void parseHeader(String[] header) throws IOException{
        Map<String, Integer> allIndices = new HashMap<>();
        for (int i = 0; i < header.length; i++){
            allIndices.put(header[i], i);
        }
        for (String property: configuration.allProperties){
            if (!allIndices.containsKey(property)){
                throw new IOException(String.format("Missing property %s in header %s",
                        property, Arrays.toString(header)));
            } else {
                propertyIndices.put(property, allIndices.get(property));
            }
        }
    }
    
    public Person parseLine(String[] line) throws IOException{
        Person result = new Person();
        configuration.booleanProperties.forEach(property -> {
            Boolean value = Boolean.parseBoolean(line[propertyIndices.get(property)]);
            result.setBooleanProperty(property, value);
        });
        configuration.integerProperties.forEach(property -> {
            Integer value = Integer.parseInt(line[propertyIndices.get(property)]);
            result.setIntegerProperty(property, value);
        });
        configuration.multipleStringProperties.forEach(property -> {
            String[] splitValue = line[propertyIndices.get(property)]
                .split(configuration.separator);
            Set<String> value = Set.of(splitValue);
            result.setMultipleProperty(property, value);
        });
        Object[] nameValues = configuration.nameProperties.stream()
            .map(property -> line[propertyIndices.get(property)]).toArray();
        result.fullName = String.format(configuration.nameFormat, nameValues);
        return result;
    }
}
