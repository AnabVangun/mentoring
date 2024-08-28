package mentoring.viewmodel.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.datastructure.Person;
import mentoring.match.MatchesBuilderHandler;
import mentoring.match.NecessaryCriterion;

/**
 * ViewModel responsible for representing a list of forbidden matches.
 */
public class ForbiddenMatchListViewModel{
    //TODO concurrency: handle synchronization on menteeToForbiddenMentors
    /*TODO refactor this class should not need to access MatchesBuilderHandler, 
    only a list of forbidden matches.
    */
    
    private final Map<Person,Set<Person>> menteeToForbiddenMentors = new HashMap<>();
    private final ObservableList<ForbiddenMatchViewModel> modifiableItems = 
            FXCollections.observableArrayList();
    private final ObservableList<ForbiddenMatchViewModel> items =
            FXCollections.unmodifiableObservableList(modifiableItems);
    
    /**
     * Return the forbidden matches represented by this instance.
     * @return an unmodifiable list of forbidden matches
     */
    public ObservableList<ForbiddenMatchViewModel> getContent(){
        return items;
    }
    
    /**
     * Define a match as impossible. Optional operation: no-op if the match is already marked as 
     * impossible.
     * @param mentee that must not be matched with the mentor
     * @param mentor that must not be matched with the mentee
     * @param handler object to notify of the action
     * @return true if the match was not already marked as impossible
     */
    public boolean addForbiddenMatch(Person mentee, Person mentor, 
            MatchesBuilderHandler<Person, Person> handler){
        Objects.requireNonNull(mentee, "expected mentee, received null");
        Objects.requireNonNull(mentor, "expected mentor, received null");
        Set<Person> forbiddenMentors = menteeToForbiddenMentors.computeIfAbsent(mentee, 
                person -> new HashSet<>());
        if(forbiddenMentors.contains(mentor)){
            return false;
        }
        forbiddenMentors.add(mentor);
        menteeToForbiddenMentors.put(mentee, forbiddenMentors);
        modifiableItems.add(new ForbiddenMatchViewModel(mentee, mentor));
        handler.forbidMatch(mentee, mentor);
        return true;
    }
    
    /**
     * Return a criterion prohibiting all the forbidden matches. This criterion will return 
     * {@code false} if and only if the pair corresponds to a forbidden match. The criterion is 
     * unmodifiable but there is no contract on its immutability: modifying this ViewModel 
     * MIGHT modify the criterion.
     * @return the criterion corresponding to this ViewModel
     */
    public NecessaryCriterion<Person,Person> getCriterion(){
        return (mentee, mentor) -> (! (menteeToForbiddenMentors.containsKey(mentee)
                && menteeToForbiddenMentors.get(mentee).contains(mentor)));
    }
    
    /**
     * Define a match as possible. Optional operation: no-op if the match is not already marked as
     * impossible.
     * @param forbiddenVM ViewModel representing the match to unmark as impossible
     * @param handler object to notify of the action
     * @return true if the match was unmarked as impossible
     */
    public boolean removeForbiddenMatch(ForbiddenMatchViewModel forbiddenVM,
            MatchesBuilderHandler<Person, Person> handler){
        Person mentee = forbiddenVM.getMentee();
        if(! menteeToForbiddenMentors.containsKey(mentee)){
            return false;
        }
        Person mentor = forbiddenVM.getMentor();
        Set<Person> forbiddenMentors = menteeToForbiddenMentors.get(mentee);
        boolean result = forbiddenMentors.remove(mentor);
        if(forbiddenMentors.isEmpty()){
            menteeToForbiddenMentors.remove(mentee);
        }
        result = result && modifiableItems.remove(forbiddenVM);
        handler.allowMatch(mentee, mentor);
        return result;
    }
    
    /**
     * Remove all the forbidden matches from this ViewModel.
     */
    public void clear(){
        modifiableItems.clear();
        menteeToForbiddenMentors.clear();
    }
}
