package mentoring.viewmodel.datastructure;

import java.util.Objects;
import mentoring.datastructure.Person;

/**
 * Immutable ViewModel responsible for representing a forbidden match.
 */
public class ForbiddenMatchViewModel {
    private final Person mentee;
    private final Person mentor;
    
    /**
     * Create a new instance.
     * @param mentee that must not be matched with the mentor
     * @param mentor that must not be matched with the mentee
     */
    ForbiddenMatchViewModel(Person mentee, Person mentor){
        this.mentee = Objects.requireNonNull(mentee);
        this.mentor = Objects.requireNonNull(mentor);
    }
    
    /**
     * Get the name of the ViewModel's mentee.
     * @return the name of the mentee that must not be matched with the mentor
     */
    public String getMenteeName(){
        return mentee.getFullName();
    }
    
    Person getMentee(){
        return mentee;
    }
    
    /**
     * Get the name of the ViewModel's mentor.
     * @return the name of the mentor that must not be matched with the mentee
     */
    public String getMentorName(){
        return mentor.getFullName();
    }
    
    Person getMentor(){
        return mentor;
    }
}
