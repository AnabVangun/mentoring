package mentoring.viewmodel.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.datastructure.Person;
import mentoring.match.NecessaryCriterion;

/**
 * ViewModel responsible for representing a list of forbidden matches.
 * TODO document, test, implement
 */
public class ForbiddenMatchListViewModel{
    
    private final Map<Person,Set<Person>> menteeToForbiddenMentors = new HashMap<>();
    private final ObservableList<ForbiddenMatchViewModel> modifiableItems = 
            FXCollections.observableArrayList();
    private final ObservableList<ForbiddenMatchViewModel> items =
            FXCollections.unmodifiableObservableList(modifiableItems);
    
    public ObservableList<ForbiddenMatchViewModel> getContent(){
        return items;
    }
    
    public boolean addForbiddenMatch(Person mentee, Person mentor){
        Set<Person> forbiddenMentors = menteeToForbiddenMentors.computeIfAbsent(mentee, 
                person -> new HashSet<>());
        if(forbiddenMentors.contains(mentor)){
            return false;
        }
        forbiddenMentors.add(mentor);
        menteeToForbiddenMentors.put(mentee, forbiddenMentors);
        modifiableItems.add(new ForbiddenMatchViewModel(mentee, mentor));
        return true;
    }
    
    public NecessaryCriterion<Person,Person> getCriterion(){
        return (mentee, mentor) -> (! (menteeToForbiddenMentors.containsKey(mentee)
                && menteeToForbiddenMentors.get(mentee).contains(mentor)));
    }
}
