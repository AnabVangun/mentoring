package mentoring.viewmodel.datastructure;

import mentoring.datastructure.Person;

/**
 * ViewModel responsible for representing a forbidden match.
 * TODO: document and test
 */
public class ForbiddenMatchViewModel {
    private final Person mentee;
    private final Person mentor;
    
    ForbiddenMatchViewModel(Person mentee, Person mentor){
        this.mentee = mentee;
        this.mentor = mentor;
    }
    
    public String getMenteeName(){
        return mentee.getFullName();
    }
    
    Person getMentee(){
        return mentee;
    }
    
    public String getMentorName(){
        return mentor.getFullName();
    }
    
    Person getMentor(){
        return mentor;
    }
}
