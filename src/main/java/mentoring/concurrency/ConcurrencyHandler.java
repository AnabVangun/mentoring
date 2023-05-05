package mentoring.concurrency;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ConcurrencyHandler {
    /*
    FIXME: document class
    */
    private ExecutorService privateExecutor = null;
    
    //TODO: delete when dependency-injection in VM has been implemented.
    public final static ConcurrencyHandler globalHandler = new ConcurrencyHandler();
    
    public Future<?> submit(Runnable runnable) throws RejectedExecutionException{
        if(privateExecutor == null){
            initialise(true);
        }
        return privateExecutor.submit(runnable);
    }
    
    public void awaitTermination(int milliseconds){
        if(privateExecutor == null){
            initialise(false);
        } else {
            privateExecutor.shutdownNow();
            try {
                privateExecutor.awaitTermination(milliseconds, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public boolean isShutDown(){
        return (privateExecutor != null && privateExecutor.isShutdown());
    }
    
    private synchronized void initialise(boolean functional){
        if(privateExecutor == null){
            if(functional){
                //TODO: put magic number in global parameters
                privateExecutor = Executors.newFixedThreadPool(1);
            } else {
                privateExecutor = new ShutdownExecutorService();
            }
        }
    }
    
    private static class ShutdownExecutorService implements ExecutorService{

        @Override
        public void shutdown() {}

        @Override
        public List<Runnable> shutdownNow() {
            return List.of();
        }

        @Override
        public boolean isShutdown() {
            return true;
        }

        @Override
        public boolean isTerminated() {
            return true;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return true;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            throw new RejectedExecutionException(getRejectionMessage());
        }
        

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            throw new RejectedExecutionException(getRejectionMessage());
        }

        @Override
        public Future<?> submit(Runnable task) {
            throw new RejectedExecutionException(getRejectionMessage());
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) 
                throws InterruptedException {
            throw new RejectedExecutionException(getRejectionMessage());
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, 
                long timeout, TimeUnit unit) throws InterruptedException {
            throw new RejectedExecutionException(getRejectionMessage());
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) 
                throws InterruptedException, ExecutionException {
            throw new RejectedExecutionException(getRejectionMessage());
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) 
                throws InterruptedException, ExecutionException, TimeoutException {
            throw new RejectedExecutionException(getRejectionMessage());
        }

        @Override
        public void execute(Runnable command) {
            throw new RejectedExecutionException(getRejectionMessage());
        }
        
        private String getRejectionMessage(){
            return "Tried to perform task on shutdown service";
        }
    }
}
