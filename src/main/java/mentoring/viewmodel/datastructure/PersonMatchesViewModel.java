package mentoring.viewmodel.datastructure;

import mentoring.datastructure.Person;
import mentoring.viewmodel.match.MatchesViewModel;

/**
 * ViewModel responsible for representing a {@link Matches} object between {@link Person} objects.
 */
public class PersonMatchesViewModel extends MatchesViewModel<Person, Person, PersonMatchViewModel> {
    PersonMatchesViewModel(){
        super(PersonMatchViewModel::new);
    }
}