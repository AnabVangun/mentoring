package mentoring.viewmodel.datastructure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PropertyName;

/**
 * ViewModel responsible for representing a {@link Person} object.
 */
public class PersonViewModel extends DataViewModel<Person>{
    
    PersonViewModel(PersonConfiguration configuration, Person person){
        super(person, p -> formatPerson(configuration, p));
    }
    
    private static Iterator<Map.Entry<String, Object>> formatPerson(
            PersonConfiguration configuration, Person person) {
        //TODO test
        Map<String, Object> result = new HashMap<>();
        for(PropertyName<?> property : configuration.getSimplePropertiesNames()){
            result.put(property.getName(), property.getStringRepresentation(person));
        }
        for(PropertyName<?> property : configuration.getMultiplePropertiesNames()){
            result.put(property.getName(), property.getStringRepresentation(person));
        }
        //FIXME: if a column is called "Name", there will be a conflict here + internationalise
        result.put("Name", person.getFullName());
        return result.entrySet().iterator();
    }
}
