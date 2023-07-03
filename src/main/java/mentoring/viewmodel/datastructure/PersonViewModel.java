package mentoring.viewmodel.datastructure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;

/**
 * Viewmodel responsible for representing a {@link Person} object.
 */
public class PersonViewModel {
    
    private final Map<String, String> personData;
    private final Person person;
    
    protected PersonViewModel(PersonConfiguration configuration, Person person){
        this.person = person;
        Map<String, String> modifiableMatch = new HashMap<>();
        //TODO: put all the configured properties here instead of simply the name
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
