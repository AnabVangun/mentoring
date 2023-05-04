package mentoring.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Utilities {
    /*
    TODO: instead of exposing an actual ExecutorService object, expose the few actually used methods:
        1. submit(Runnable)
        2. awaitTermination(int)
    Handle all the internal mechanisms: initiate only when called, shutdown properly, 
    revive if called after shutdown.
    */
    private Utilities(){}
    //TODO: put magic number in global parameters
    private final static ExecutorService executor = Executors.newFixedThreadPool(1);
    
    public static ExecutorService getExecutorService(){
        return executor;
    }
}
