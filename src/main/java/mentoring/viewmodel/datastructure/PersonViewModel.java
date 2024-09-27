package mentoring.viewmodel.datastructure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PropertyName;

/**
 * ViewModel responsible for representing a {@link Person} object.
 */
public class PersonViewModel extends DataViewModel<Person>{
    //TODO test
    //TODO make a PersonViewModelFactory generating a group of PersonViewModel
    
    PersonViewModel(PersonConfiguration configuration, Person person){
        super(person, formatPersonGenerator(configuration));
    }
    
    private static Function<Person, Iterator<Map.Entry<String, Object>>> 
            formatPersonGenerator(PersonConfiguration configuration){
                //TODO internationalize
                //TODO refactor: make this a static method of a PersonViewModelFactory
                String name = "Name";
                if (configuration.containsPropertyName(name)){
                    name = name + " (%s)";
                    int i = 1;
                    while (configuration.containsPropertyName(name.formatted(i))){
                        i += 1;
                    }
                    name = name.formatted(i);
                }
                final String nameProperty = name;
                return (p) -> formatPerson(configuration, p, nameProperty);
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
        for(PropertyName<?> property : configuration.getSimplePropertiesNames()){
            map.put(property.getName(), property.getStringRepresentation(person));
        }
        for(PropertyName<?> property : configuration.getMultiplePropertiesNames()){
            map.put(property.getName(), property.getStringRepresentation(person));
        }
    }
}
