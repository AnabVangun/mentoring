package mentoring.concurrency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import mentoring.concurrency.ConcurrencyHandlerTest.ConcurrencyHandlerTestArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ConcurrencyHandlerTest implements TestFramework<ConcurrencyHandlerTestArgs>{

    @Override
    public Stream<ConcurrencyHandlerTestArgs> argumentsSupplier() {
        return Stream.of(new TypelessConcurrencyHandlerTestArgs("test case for submit(Runnable)"),
                new TypeAwareConcurrencyHandlerTestArgs("test case for submit(Callable)"));
    }
    
    Stream<ConcurrencyHandlerTestArgs> timeoutArgumentsSupplier(){
        return Stream.of(new TypelessConcurrencyHandlerTestArgs(100), 
                new TypelessConcurrencyHandlerTestArgs(0));
    }
    
    @TestFactory
    Stream<DynamicNode> submit_first(){
        return test("submit() performs the expected task when called for the first time", args -> {
            boolean[] finished = new boolean[]{false};
            Future<?> future = args.submitTaskToSetIndexTrue(finished, 0);
            assertTaskCompleted(future, finished, 0);
            args.assertResultAsExpected(0, future);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> submit_repeated(){
        return test("submit() performs all the expected tasks when called repeatedly", args -> {
            boolean[] finished = new boolean[]{false, false};
            Future<?> future1 = args.submitTaskToSetIndexTrue(finished, 0);
            Future<?> future2 = args.submitTaskToSetIndexTrue(finished, 1);
            assertTaskCompleted(future1, finished, 0);
            args.assertResultAsExpected(0, future1);
            assertTaskCompleted(future2, finished, 1);
            args.assertResultAsExpected(1, future2);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> submit_terminated(){
        return test("submit() throws the expected exception after a complete termination", args -> {
            boolean[] finished = new boolean[]{false, false};
            //Make sure that handler is fully initiated
            args.submitTaskToSetIndexTrue(finished, 0);
            args.assertShutdown();
            Assertions.assertThrows(RejectedExecutionException.class, 
                    () -> args.submitTaskToSetIndexTrue(finished, 0));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> shutdown_noSubmit(){
        return test(timeoutArgumentsSupplier(),
                "shutdown() terminates properly when no calls to submit have been made",
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
        return test("shutdown() terminates properly when all tasks have completed",
                args -> {
                    Future<?> future = args.submitTaskToSetIndexTrue(new boolean[1], 0);
                    Assertions.assertDoesNotThrow(() -> future.get(100, TimeUnit.MILLISECONDS));
                    args.assertShutdown();
                });
    }
    
    @TestFactory
    Stream<DynamicNode> shutdown_invalidInput_beforeSubmit(){
        return test(Stream.of(new TypelessConcurrencyHandlerTestArgs(-1)), 
                "shutdown() throws the expected exception on invalid input before tasks have "
                        + "been submitted",
                args -> Assertions.assertThrows(IllegalArgumentException.class, 
                            () -> args.handler.shutdown(args.timeout)));
    }
    
    @TestFactory
    Stream<DynamicNode> shutdown_invalidInput_afterSubmit(){
        return test(Stream.of(new TypelessConcurrencyHandlerTestArgs(-50)), 
                "shutdown() throws the expected exception on invalid input after tasks have "
                        + "been submitted",
                args -> {
                    args.submitTaskToSetIndexTrue(new boolean[1], 0);
                    Assertions.assertThrows(IllegalArgumentException.class, 
                            () -> args.handler.shutdown(args.timeout));
                });
    }
    
    private static void assertTaskCompleted(Future<?> task, boolean[] resultHolder, int resultIndex){
        Assertions.assertDoesNotThrow(() -> task.get(500, TimeUnit.MILLISECONDS));
        Assertions.assertTrue(resultHolder[resultIndex]);
    }
    
    static abstract class ConcurrencyHandlerTestArgs extends TestArgs{
        final ConcurrencyHandler handler = new ConcurrencyHandler();
        final int timeout;

        public ConcurrencyHandlerTestArgs(String testCase, int timeout) {
            super(testCase);
            this.timeout = timeout;
        }
        
        void assertShutdown(){
            handler.shutdown(timeout);
            Assertions.assertTrue(handler.isShutDown());
        }
        
        abstract Future<?> submitTaskToSetIndexTrue(boolean[] resultCollector, int index);
        
        abstract void assertResultAsExpected(int expected, Future<?> actual);
    }
    
    private static class TypelessConcurrencyHandlerTestArgs extends ConcurrencyHandlerTestArgs {
        TypelessConcurrencyHandlerTestArgs(String testCase){
            super(testCase, 100);
        }
        
        TypelessConcurrencyHandlerTestArgs(int timeout){
            super("%s milliseconds timeout".formatted(timeout), timeout);
        }
        
        @Override
        Future<?> submitTaskToSetIndexTrue(boolean[] resultCollector, int index){
            return handler.submit(() -> resultCollector[index] = true);
        }
        
        @Override
        void assertResultAsExpected(int expected, Future<?> actual){/*no-op*/}
    }
    
    private static class TypeAwareConcurrencyHandlerTestArgs extends ConcurrencyHandlerTestArgs {
        TypeAwareConcurrencyHandlerTestArgs(String testCase){
            super(testCase, 100);
        }
        
        @Override
        Future<Integer> submitTaskToSetIndexTrue(boolean[] resultCollector, int index){
            return handler.submit(() -> {
                resultCollector[index] = true;
                return index;
            });
        }
        
        @Override
        void assertResultAsExpected(int expected, Future<?> actual){
            try {
                Assertions.assertEquals((Integer) expected, (Integer) actual.get());
            } catch (InterruptedException | ExecutionException e){
                Assertions.fail(e);
            }
        }
    }
}
