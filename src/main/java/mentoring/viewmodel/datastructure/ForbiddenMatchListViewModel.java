package mentoring.viewmodel.datastructure;

import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.datastructure.Person;
import mentoring.match.ForbiddenMatches;

/**
 * ViewModel responsible for representing a list of forbidden matches.
 * This class does not guarantee any safety mechanism: forbidding a match multiple times MAY or 
 * MAY NOT have weird effects on how many times said match must be allowed for it to effectively be
 * allowed.
 */
public class ForbiddenMatchListViewModel{
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
     * Define a match as impossible.
     * @param mentee that must not be matched with the mentor
     * @param mentor that must not be matched with the mentee
     */
    public void addForbiddenMatch(Person mentee, Person mentor){
        Objects.requireNonNull(mentee, "expected mentee, received null");
        Objects.requireNonNull(mentor, "expected mentor, received null");
        synchronized(modifiableItems){
            modifiableItems.add(new ForbiddenMatchViewModel(mentee, mentor));
        }
    }
    
    /**
     * Define a match as possible.
     * @param forbiddenVM ViewModel representing the match to unmark as impossible
     * @param handler object to notify of the action
     */
    public void removeForbiddenMatch(ForbiddenMatchViewModel forbiddenVM,
            ForbiddenMatches<Person, Person> handler){
        Person mentee = forbiddenVM.getMentee();
        Person mentor = forbiddenVM.getMentor();
        if(handler.allowMatch(mentee, mentor)){
            synchronized(modifiableItems){
                modifiableItems.remove(forbiddenVM);
            }
        }
    }
    
    /**
     * Remove all the forbidden matches from this ViewModel.
     */
    public void clear(){
        synchronized(modifiableItems){
            modifiableItems.clear();
        }
    }
}
