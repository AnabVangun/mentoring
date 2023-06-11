package mentoring.viewmodel;

import java.util.concurrent.Future;
import javax.inject.Inject;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

/**
 * ViewModel responsible for handling the main window of the application.
 */
public class MainViewModel {
    private final ConcurrencyHandler executor;
    
    /**
     * Create a new {@code MainViewModel}.
     * @param executor Executor service that will receive the task to run the application.
     */
    @Inject
    MainViewModel(ConcurrencyHandler executor){
        this.executor = executor;
    }
    
    /**
     * Run the application: get the relevant data, make matches and update the {@code status} 
     * property.
     * @param resultVM the ViewModel to update with the results
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeMatches(PersonMatchesViewModel resultVM){
        return executor.submit(new MatchMaker(resultVM));
    }
    
}
