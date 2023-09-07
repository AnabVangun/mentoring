package mentoring.viewmodel.datastructure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PropertyName;

/**
 * Viewmodel responsible for representing a {@link Person} object.
 */
public class PersonViewModel {
    //TODO document and test
    private final Map<String, String> personData;
    private final Person person;
    
    PersonViewModel(PersonConfiguration configuration, Person person){
        this.person = person;
        Map<String, String> modifiableMatch = new HashMap<>();
        for(PropertyName<?> property : configuration.getPropertiesNames()){
            modifiableMatch.put(property.getName(), property.getStringRepresentation(person));
        }
        for(PropertyName<?> property : configuration.getMultiplePropertiesNames()){
            modifiableMatch.put(property.getName(), property.getStringRepresentation(person));
        }
        //FIXME: if a column is called "Name", there will be a conflict here
        modifiableMatch.put("Name", person.getFullName());
        personData = Collections.unmodifiableMap(modifiableMatch);
    }
    
    public Map<String, String> getPersonData(){
        return personData;
    }
    
    public Person getPerson(){
        return person;
    }
}
