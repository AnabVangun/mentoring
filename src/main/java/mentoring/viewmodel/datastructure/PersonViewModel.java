package mentoring.viewmodel.datastructure;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import mentoring.datastructure.Person;

/**
 * ViewModel responsible for representing a {@link Person} object.
 */
public class PersonViewModel extends DataViewModel<Person>{
    PersonViewModel(Person person, Function<Person, Iterator<Map.Entry<String, Object>>> formatter){
        super(person, formatter);
    }
    
    private final MatchStatus status = new MatchStatus();
    
    public MatchStatus getStatus(){
        return status;
    }
}
