package mentoring.viewmodel;

import mentoring.viewmodel.tasks.PersonGetter;
import java.io.File;
import mentoring.viewmodel.datastructure.PersonType;
import java.util.concurrent.Future;
import javax.inject.Inject;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonViewModel;
import mentoring.viewmodel.tasks.MatchExportTask;
import mentoring.viewmodel.tasks.MultipleMatchTask;
import mentoring.viewmodel.tasks.SingleMatchRemovalTask;
import mentoring.viewmodel.tasks.SingleMatchTask;

/**
 * ViewModel responsible for handling the main window of the application.
 */
public class MainViewModel {
    /*
    TODO: handle concurrency. For each subtask, test and document method
        make sure that a global match cannot be run while a single match is running
        make sure that only one global match can be run at the same time
        make sure that if several single matches are running, they only handle different persons
    */
    private final ConcurrencyHandler matchMaker;
    
    /**
     * Create a new {@code MainViewModel}.
     * @param executor Executor service that will receive the task to run the application.
     */
    @Inject
    MainViewModel(ConcurrencyHandler concurrencyHandler){
        this.matchMaker = concurrencyHandler;
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
        return matchMaker.submit(new PersonGetter(resultVM, data, type));
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
        return matchMaker.submit(new MultipleMatchTask(resultVM, data,
                menteeVM.getUnderlyingData(),
                mentorVM.getUnderlyingData()));
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
        return matchMaker.submit(new SingleMatchTask(resultVM, data, menteeVM.getPerson(), 
                mentorVM.getPerson()));
    }
    
    /**
     * Remove a match between two persons.
     * @param toRemove the ViewModel containing the match to remove
     * @param resultVM the ViewModel to update
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> removeSingleMatch(PersonMatchViewModel toRemove, PersonMatchesViewModel resultVM){
        return matchMaker.submit(new SingleMatchRemovalTask(resultVM, toRemove));
    }
    
    /**
     * Export the current matches in a file.
     * @param toExport the ViewModel containing the matches to export
     * @param outputFile the destination file
     * @param data how to get the configuration data
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> exportMatches(PersonMatchesViewModel toExport, File outputFile, 
            RunConfiguration data){
        return matchMaker.submit(new MatchExportTask(toExport, outputFile, data));
    }
    
}
