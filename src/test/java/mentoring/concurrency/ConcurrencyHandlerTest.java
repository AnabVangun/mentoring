package mentoring.concurrency;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import mentoring.concurrency.ConcurrencyHandlerTest.ConcurrencyHandlerTestArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

class ConcurrencyHandlerTest implements TestFramework<ConcurrencyHandlerTestArgs>{

    @Override
    public Stream<ConcurrencyHandlerTestArgs> argumentsSupplier() {
        return Stream.of(new ConcurrencyHandlerTestArgs("unique test case"));
    }
    
    Stream<ConcurrencyHandlerTestArgs> timeoutArgumentsSupplier(){
        return Stream.of(new ConcurrencyHandlerTestArgs(100), new ConcurrencyHandlerTestArgs(0));
    }
    
    @TestFactory
    Stream<DynamicNode> submit_first(){
        return test("submit() performs the expected task when called for the first time", args -> {
            boolean[] finished = new boolean[]{false};
            Future<?> future = submitTaskToSetIndexTrue(args.handler, finished, 0);
            assertTaskCompleted(future, finished, 0);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> submit_repeated(){
        return test("submit() performs all the expected tasks when called repeatedly", args -> {
            boolean[] finished = new boolean[]{false, false};
            Future<?> future1 = submitTaskToSetIndexTrue(args.handler, finished, 0);
            Future<?> future2 = submitTaskToSetIndexTrue(args.handler, finished, 1);
            assertTaskCompleted(future1, finished, 0);
            assertTaskCompleted(future2, finished, 1);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> submit_terminated(){
        return test("submit() throws the expected exception after a complete termination", args -> {
            boolean[] finished = new boolean[]{false, false};
            submitTaskToSetIndexTrue(args.handler, finished, 0);
            args.assertShutdown();
            Assertions.assertThrows(RejectedExecutionException.class, 
                    () -> submitTaskToSetIndexTrue(args.handler, finished, 0));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> shutdown_noSubmit(){
        return test("shutdown() terminates properly when no calls to submit have been made",
                args -> args.assertShutdown());
    }
    
    @TestFactory
    Stream<DynamicNode> shutdown_ongoingTasks(){
        return test(timeoutArgumentsSupplier(), 
                "shutdown() terminates properly when tasks are ongoing",
                args -> {
                    args.handler.submit(() -> {
                        try{
                            wait();
                        } catch (InterruptedException e) {}});
                    args.assertShutdown();
                });
    }
    
    @TestFactory
    Stream<DynamicNode> shutdown_afterSubmit(){
        return test(timeoutArgumentsSupplier(),
                "shutdown() terminates properly when all tasks have completed",
                args -> {
                    Future<?> future = submitTaskToSetIndexTrue(args.handler, new boolean[1], 0);
                    Assertions.assertDoesNotThrow(() -> future.get(100, TimeUnit.MILLISECONDS));
                    args.assertShutdown();
                });
    }
    
    @TestFactory
    Stream<DynamicNode> shutdown_invalidInput_beforeSubmit(){
        return test(Stream.of(new ConcurrencyHandlerTestArgs(-1)), 
                "shutdown() throws the expected exception on invalid input before tasks have "
                        + "been submitted",
                args -> Assertions.assertThrows(IllegalArgumentException.class, 
                            () -> args.handler.shutdown(args.timeout)));
    }
    
    @TestFactory
    Stream<DynamicNode> shutdown_invalidInput_afterSubmit(){
        return test(Stream.of(new ConcurrencyHandlerTestArgs(-50)), 
                "shutdown() throws the expected exception on invalid input after tasks have "
                        + "been submitted",
                args -> {
                    submitTaskToSetIndexTrue(args.handler, new boolean[1], 0);
                    Assertions.assertThrows(IllegalArgumentException.class, 
                            () -> args.handler.shutdown(args.timeout));
                });
    }
    
    private static Future<?> submitTaskToSetIndexTrue(ConcurrencyHandler handler,
            boolean[] resultCollector, int index){
        return handler.submit(() -> resultCollector[index] = true);
    }
    
    private static void assertTaskCompleted(Future<?> task, boolean[] resultHolder, int resultIndex){
        Assertions.assertDoesNotThrow(() -> task.get(500, TimeUnit.MILLISECONDS));
        Assertions.assertTrue(resultHolder[resultIndex]);
    }
    
    static record ConcurrencyHandlerTestArgs(String testCase, int timeout, 
            ConcurrencyHandler handler){
        ConcurrencyHandlerTestArgs(String testCase){
            this(testCase, 100, new ConcurrencyHandler());
        }
        
        ConcurrencyHandlerTestArgs(int timeout){
            this("%s milliseconds timeout".formatted(timeout), timeout, new ConcurrencyHandler());
        }
        @Override
        public String toString(){
            return testCase;
        }
        
        void assertShutdown(){
            handler.shutdown(timeout);
            Assertions.assertTrue(handler.isShutDown());
        }
    }
}
