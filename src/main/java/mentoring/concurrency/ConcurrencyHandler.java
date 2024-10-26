package mentoring.concurrency;

import jakarta.inject.Singleton;
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

/**
 * Utility class handling providing concurrency services.
 * A singleton SHOULD be shared throughout the application, and properly shutdown by the main thread
 * at application shutdown.
 */
@Singleton
public final class ConcurrencyHandler {
    private ExecutorService privateExecutor = null;
    
    /**
     * Submit a task to the workers pool for a background execution.
     * @param runnable the task to perform in the background.
     * @return an object representing the execution state.
     * @throws RejectedExecutionException if the workers pool rejected the task.
     */
    public Future<?> submit(Runnable runnable) throws RejectedExecutionException{
        initialiseIfNeeded();
        return privateExecutor.submit(runnable);
    }
    
    private void initialiseIfNeeded(){
        if(privateExecutor == null){
            initialise(true);
        }
    }
    
    /**
     * Shutdown the service. After the service has been shutdown, it will reject any future incoming
     * tasks. The call will block until all ongoing tasks have stopped or timeout has been reached.
     * @param timeout the maximum time to wait (in milliseconds)
     * @throws IllegalArgumentException if timeout is strictly negative
     */
    public void shutdown(int timeout) throws IllegalArgumentException {
        if (timeout < 0){
            throw new IllegalArgumentException(
                    "Tried to call method with timeout %s, should be positive".formatted(timeout));
        }
        if(privateExecutor == null){
            initialise(false);
        } else {
            privateExecutor.shutdownNow();
            try {
                privateExecutor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
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
                privateExecutor = Executors.newFixedThreadPool(Parameters.getNumberOfThreads());
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