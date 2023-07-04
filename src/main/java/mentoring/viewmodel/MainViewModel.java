package mentoring.viewmodel;

import mentoring.viewmodel.datastructure.PersonType;
import java.util.concurrent.Future;
import javax.inject.Inject;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonViewModel;

/**
 * ViewModel responsible for handling the main window of the application.
 */
public class MainViewModel {
    private final ConcurrencyHandler executor;
    private final ConcurrentMatchMaker matchMaker;
    
    /**
     * Create a new {@code MainViewModel}.
     * @param executor Executor service that will receive the task to run the application.
     */
    @Inject
    MainViewModel(ConcurrencyHandler executor){
        this.executor = executor;
        //TODO use dependency injection instead
        this.matchMaker = new ConcurrentMatchMaker(executor);
    }
    
    /**
     * Get a list of persons.
     * @param resultVM the ViewModel to update with the results
     * @param data how to get the person data and configuration
     * @param type the type of persons to get
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> getPersons(PersonListViewModel resultVM, RunConfiguration data,
            PersonType type){
        return executor.submit(new PersonGetter(resultVM, data, type));
    }
    
    /**
     * Run the application: get the relevant data, and make matches.
     * @param menteeVM the ViewModel containing the mentees
     * @param mentorVM the ViewModel containing the mentors
     * @param resultVM the ViewModel to update with the results
     * @param data how to get the configuration data
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeMatches(PersonListViewModel menteeVM, PersonListViewModel mentorVM,
            PersonMatchesViewModel resultVM, RunConfiguration data){
        return matchMaker.makeMultipleMatches(resultVM, data,
                menteeVM.getUnderlyingData(),
                mentorVM.getUnderlyingData());
    }
    
    /**
     * Create a match between two selected persons.
     * @param menteeVM the ViewModel containing the mentee
     * @param mentorVM the ViewModel containing the mentor
     * @param resultVM the ViewModel to update with the results
     * @param data how to get the configuration data
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeSingleMatch(PersonViewModel menteeVM, PersonViewModel mentorVM,
            PersonMatchesViewModel resultVM, RunConfiguration data){
        return matchMaker.makeSingleMatch(resultVM, data, menteeVM.getPerson(), 
                mentorVM.getPerson());
    }
    
    /**
     * Remove a match between two persons.
     * @param toRemove the ViewModel containing the match to remove
     * @param resultVM the ViewModel to update
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> removeSingleMatch(PersonMatchViewModel toRemove, PersonMatchesViewModel resultVM){
        return matchMaker.removeSingleMatch(resultVM, toRemove);
    }
    
}
