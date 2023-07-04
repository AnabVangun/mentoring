package mentoring.viewmodel;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.concurrent.Task;
import javax.inject.Inject;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

/**
 * Class used to make matches and update an input view model.
 */
class ConcurrentMatchMaker {
    //TODO refactor: the runConfiguration should be at the ConcurrentMatchMaker level
    //TODO: make sure that a global match cannot be run while a single match is running
    //TODO: make sure that a single global match can be run at the same time
    //TODO: make sure that if several single matches are running, they only handle different persons
    
    private final ConcurrencyHandler handler;
    
    /**
     * Initialise a MatchMaker object.
     * @param handler a concurrency service 
     */
    @Inject
    ConcurrentMatchMaker(ConcurrencyHandler handler){
        this.handler = handler;
    }
    
    /**
     * Match multiple mentees and mentors in a background task.
     * @param resultVM the view model that will be updated when the task completes
     * @param data where to get data from
     * @param mentees the list of mentees to match
     * @param mentors the list of mentors to match
     * @return an object that can be used to get the status of the background task
     */
    Future<?> makeMultipleMatches(PersonMatchesViewModel resultVM, RunConfiguration data, 
            List<Person> mentees, List<Person> mentors) {
        return handler.submit(new MultipleMatchTask(resultVM, data, mentees, mentors));
    }
    
    static class MultipleMatchTask extends Task<Void> {
        private final PersonMatchesViewModel resultVM;
        private final RunConfiguration data;
        private final List<Person> mentees;
        private final List<Person> mentors;
        private ResultConfiguration<Person, Person> resultConfiguration;
        private Matches<Person, Person> results;
        
        /**
         * Initialise a {@code MultipleMatchTask} object.
         * @param resultVM the view model that will be updated when the task completes
         * @param data where to get data from
         * @param mentees the list of mentees to match
         * @param mentors the list of mentors to match
         */
        MultipleMatchTask(PersonMatchesViewModel resultVM, RunConfiguration data, 
                List<Person> mentees, List<Person> mentors) {
            this.resultVM = resultVM;
            this.data = data;
            this.mentees = mentees;
            this.mentors = mentors;
        }
        
        @Override
        protected Void call() throws Exception {
            List<Person> filteredMentees = filterAvailablePerson(mentees, 
                    resultVM.getTransferredItems(), t -> t.getMentee());
            List<Person> filteredMentors = filterAvailablePerson(mentors, 
                    resultVM.getTransferredItems(), t -> t.getMentor());
            results = makeMatchesWithException(data, filteredMentees, filteredMentors);
            resultConfiguration = getResultConfiguration(data);
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            resultVM.update(resultConfiguration, results);
        }
        
        private static List<Person> filterAvailablePerson(List<Person> toFilter, 
                List<PersonMatchViewModel> matches, Function<Match<Person, Person>, Person> personExtractor){
            Set<Person> unavailable = matches.stream()
                    .map(element -> personExtractor.apply(element.getData()))
                    .collect(Collectors.toSet());
            return toFilter.stream().filter(e -> !unavailable.contains(e)).toList();
        }

        private static Matches<Person, Person> makeMatchesWithException(RunConfiguration data,
                List<Person> mentees, List<Person> mentors) throws IOException {
            Person defaultMentee = data.getDefaultMentee();
            Person defaultMentor = data.getDefaultMentor();
            //Get criteria configuration
            CriteriaConfiguration<Person, Person> criteriaConfiguration = 
                    data.getCriteriaConfiguration();
            //Build matches
            return matchMenteesAndMentors(mentees, mentors, criteriaConfiguration, defaultMentee, 
                    defaultMentor);
        }

        private static ResultConfiguration<Person, Person> getResultConfiguration(RunConfiguration data)
                throws IOException {
            return data.getResultConfiguration();
        }

        private static Matches<Person, Person> matchMenteesAndMentors(List<Person> mentees, 
                List<Person> mentors, CriteriaConfiguration<Person, Person> criteriaConfiguration, 
                Person defaultMentee, Person defaultMentor) {
            MatchesBuilder<Person, Person> solver = new MatchesBuilder<>(mentees, mentors, 
                    criteriaConfiguration.getProgressiveCriteria());
            solver.withNecessaryCriteria(criteriaConfiguration.getNecessaryCriteria())
                    .withPlaceholderPersons(defaultMentee, defaultMentor);
            return solver.build();
        }
    }

    /**
     * Match a mentee and a mentor in a background task.
     * @param resultVM the view model that will be updated when the task completes
     * @param data where to get data from
     * @param mentee the mentee to match
     * @param mentor the mentor to match
     * @return an object that can be used to get the status of the background task
     */
    Future<?> makeSingleMatch(PersonMatchesViewModel resultVM, RunConfiguration data, 
            Person mentee, Person mentor) {
        return handler.submit(new SingleMatchTask(resultVM, data, mentee, mentor));
    }
    
    static class SingleMatchTask extends Task<Void> {
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
        SingleMatchTask(PersonMatchesViewModel resultVM, RunConfiguration data, 
                Person mentee, Person mentor) {
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

        private static Match<Person, Person> makeMatchWithException(RunConfiguration data,
                Person mentee, Person mentor) throws IOException {
            //Get criteria configuration
            CriteriaConfiguration<Person, Person> criteriaConfiguration = 
                    data.getCriteriaConfiguration();
            //Build match
            return matchMenteeAndMentor(mentee, mentor, criteriaConfiguration);
        }

        private static Match<Person, Person> matchMenteeAndMentor(Person mentee, 
                Person mentor, CriteriaConfiguration<Person, Person> criteriaConfiguration) {
            //TODO: MatchMaker should handle the MatchesBuilder object so that there is no need to
            //recreate one each time.
            MatchesBuilder<Person, Person> solver = new MatchesBuilder<>(List.of(mentee), 
                    List.of(mentor), 
                    criteriaConfiguration.getProgressiveCriteria());
            solver.withNecessaryCriteria(criteriaConfiguration.getNecessaryCriteria());
            return solver.buildSingleMatch(mentee, mentor);
        }
    }
    
    /**
     * Remove a match between a mentee and a mentor in a background task.
     * @param resultVM the view model that will be updated when the task completes
     * @param toRemove the view model representing the match to remove
     * @return an object that can be used to get the status of the background task
     */
    Future<?> removeSingleMatch(PersonMatchesViewModel resultVM, PersonMatchViewModel toRemove) {
        return handler.submit(new SingleMatchRemovalTask(resultVM, toRemove));
    }
    
    static class SingleMatchRemovalTask extends Task<Void> {
        private final PersonMatchesViewModel resultVM;
        private final PersonMatchViewModel toRemove;
        
        /**
         * Initialise a {@code SingleMatchDeletionTask} object.
         * @param resultVM the view model that will be updated when the task completes
         * @param toRemove the view model representing the match to remove
         */
        SingleMatchRemovalTask(PersonMatchesViewModel resultVM, PersonMatchViewModel toRemove) {
            this.resultVM = resultVM;
            this.toRemove = toRemove;
        }
        
        @Override
        protected Void call() throws Exception {
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            resultVM.removeManualItem(toRemove);
        }
    }
}
