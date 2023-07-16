package mentoring.concurrency;

class Parameters {
    private Parameters(){
        throw new UnsupportedOperationException("static class not meant to be instantiated");
    }
    
    //Modifying this parameter may break things in classes relying on a single-thread ConcurrencyHandler.
    private final static int NUMBER_OF_THREADS = 1;
    
    static int getNumberOfThreads(){
        return NUMBER_OF_THREADS;
    }
}