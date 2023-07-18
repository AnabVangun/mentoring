package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.util.List;
import javafx.concurrent.Task;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.datastructure.Person;
import mentoring.match.Match;
import mentoring.match.MatchesBuilder;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

public class SingleMatchTask extends Task<Void> {
    
    private final PersonMatchesViewModel resultVM;
    private final RunConfiguration data;
    private final Person mentee;
    private final Person mentor;
    private Match<Person, Person> result;

    /**
     * Initialise a {@code SingleMatchTask} object.
     * @param resultVM the view model that will be updated when the task completes
     * @param data where to get data from
     * @param mentee the mentee to match
     * @param mentor the mentor to match
     */
    public SingleMatchTask(PersonMatchesViewModel resultVM, RunConfiguration data, Person mentee, 
            Person mentor) {
        this.resultVM = resultVM;
        this.data = data;
        this.mentee = mentee;
        this.mentor = mentor;
    }

    @Override
    protected Void call() throws Exception {
        result = makeMatchWithException(data, mentee, mentor);
        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        resultVM.addManualItem(result);
    }

    private static Match<Person, Person> makeMatchWithException(RunConfiguration data, Person mentee,
            Person mentor) throws IOException {
        //Get criteria configuration
        CriteriaConfiguration<Person, Person> criteriaConfiguration = data.getCriteriaConfiguration();
        //Build match
        return matchMenteeAndMentor(mentee, mentor, criteriaConfiguration);
    }

    private static Match<Person, Person> matchMenteeAndMentor(Person mentee, Person mentor, 
            CriteriaConfiguration<Person, Person> criteriaConfiguration) {
        //TODO: MatchMaker should handle the MatchesBuilder object so that there is no need to
        //recreate one each time.
        MatchesBuilder<Person, Person> solver = new MatchesBuilder<>(List.of(mentee), List.of(mentor),
                criteriaConfiguration.getProgressiveCriteria());
        solver.withNecessaryCriteria(criteriaConfiguration.getNecessaryCriteria());
        return solver.buildSingleMatch(mentee, mentor);
    }
    
}
