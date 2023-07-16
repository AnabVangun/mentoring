package mentoring.viewmodel.tasks;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;
import javax.inject.Inject;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.datastructure.Person;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

/**
 * Class used to make matches and update an input view model.
 */
public class ConcurrentMatchMaker {
    //TODO: make sure that a global match cannot be run while a single match is running
    //TODO: make sure that a single global match can be run at the same time
    //TODO: make sure that if several single matches are running, they only handle different persons
    
    private final ConcurrencyHandler handler;
    
    /**
     * Initialise a MatchMaker object.
     * @param handler a concurrency service 
     */
    @Inject
    public ConcurrentMatchMaker(ConcurrencyHandler handler){
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
    public Future<?> makeMultipleMatches(PersonMatchesViewModel resultVM, RunConfiguration data, 
            List<Person> mentees, List<Person> mentors) {
        return handler.submit(new MultipleMatchTask(resultVM, data, mentees, mentors));
    }
    
    /**
     * Match a mentee and a mentor in a background task.
     * @param resultVM the view model that will be updated when the task completes
     * @param data where to get data from
     * @param mentee the mentee to match
     * @param mentor the mentor to match
     * @return an object that can be used to get the status of the background task
     */
    public Future<?> makeSingleMatch(PersonMatchesViewModel resultVM, RunConfiguration data, 
            Person mentee, Person mentor) {
        return handler.submit(new SingleMatchTask(resultVM, data, mentee, mentor));
    }
    
    /**
     * Remove a match between a mentee and a mentor in a background task.
     * @param resultVM the view model that will be updated when the task completes
     * @param toRemove the view model representing the match to remove
     * @return an object that can be used to get the status of the background task
     */
    public Future<?> removeSingleMatch(PersonMatchesViewModel resultVM, PersonMatchViewModel toRemove) {
        return handler.submit(new SingleMatchRemovalTask(resultVM, toRemove));
    }
    
    /**
     * Export matches to a file in a background task.
     * @param exportedVM the view model that contains the data to export
     * @param outputFile the file where to export the data
     * @param data where to get data from
     * @return an object that can be used to get the status of the background task
     */
    public Future<?> exportMatches(PersonMatchesViewModel exportedVM, File outputFile, RunConfiguration data){
        return handler.submit(new MatchExportTask(exportedVM, outputFile, data));
    }
    
}
