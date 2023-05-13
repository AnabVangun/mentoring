package mentoring.viewmodel.datastructure;

import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.match.Match;
import mentoring.viewmodel.match.MatchViewModel;

/**
 * Viewmodel responsible for representing a {@link Match} object between {@link Person} objects.
 */
public class PersonMatchViewModel extends MatchViewModel<Person, Person>{
    
    /**
     * Builds a new [@code PersonMatchViewModel}.
     * @param configuration used to select the attributes to represent
     * @param match to represent
     */
    PersonMatchViewModel(ResultConfiguration<Person, Person> configuration, 
            Match<Person, Person> match){
        super(configuration, match);
    }
}
