package mentoring.viewmodel.datastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PropertyDescription;

/**
 * Factory used to build a list of {@link PersonViewModel} instances with the same configuration.
 */
class PersonViewModelFactory {
    //TODO internationalise
    final static String DEFAULT_NAME_PROPERTY_NAME = "Name";
    private String namePropertyName = DEFAULT_NAME_PROPERTY_NAME;
    private final Function<Person, Iterator<Map.Entry<String, Object>>> dataFormatter;
    
    /**
     * Initialise a factory ready to make {@link PersonViewModel} instances.
     * @param configuration to which all the future {@link Person} inputs of this factory MUST 
     *      conform
     */
    PersonViewModelFactory(PersonConfiguration configuration){
        if (configuration.containsPropertyName(namePropertyName)){
            namePropertyName = namePropertyName + " (%s)";
            int i = 1;
            while (configuration.containsPropertyName(namePropertyName.formatted(i))){
                i += 1;
            }
            namePropertyName = namePropertyName.formatted(i);
        }
        dataFormatter = person -> formatPerson(configuration, person, namePropertyName);
    }
    
    private static Iterator<Map.Entry<String, Object>> formatPerson(
            PersonConfiguration configuration, Person person, String nameProperty) {
        Map<String, Object> result = new HashMap<>();
        fillMapWithProperties(result, configuration, person);
        result.put(nameProperty, person.getFullName());
        return result.entrySet().iterator();
    }
    
    private static void fillMapWithProperties(Map<String, Object> map, 
            PersonConfiguration configuration, Person person){
        for(PropertyDescription<?> property : configuration.getSimplePropertiesNames()){
            map.put(property.getName(), property.getStringRepresentation(person));
        }
        for(PropertyDescription<?> property : configuration.getMultiplePropertiesNames()){
            map.put(property.getName(), property.getStringRepresentation(person));
        }
    }
    
    /**
     * Get the name of the property used to store the full name of the persons.
     * @return the name of property storing the result of {@link Person#getFullName() }.
     */
    String getFullNamePropertyName(){
        return namePropertyName;
    }
    
    /**
     * Encapsulate the input {@link Person} instances into {@link PersonViewModel} instances.
     * @param persons to encapsulate
     * @return a collection representing the input persons
     */
    Collection<PersonViewModel> create(Iterable<Person> persons){
        Collection<PersonViewModel> result = new ArrayList<>();
        for(Person person: persons){
            result.add(new PersonViewModel(person, dataFormatter));
        }
        return result;
    }
}
