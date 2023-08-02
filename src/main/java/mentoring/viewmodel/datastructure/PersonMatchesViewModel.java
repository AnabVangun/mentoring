package mentoring.viewmodel.datastructure;

import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.viewmodel.base.ConfigurableViewModel;
import mentoring.viewmodel.match.MatchesViewModel;

/**
 * ViewModel responsible for representing a {@link Matches} object between {@link Person} objects.
 */
public class PersonMatchesViewModel extends MatchesViewModel<Person, Person, PersonMatchViewModel> 
        implements ConfigurableViewModel<ResultConfiguration<Person, Person>> {
    PersonMatchesViewModel(){
        super(PersonMatchViewModel::new);
    }
}