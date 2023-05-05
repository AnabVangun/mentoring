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
    
    @TestFactory
    Stream<DynamicNode> submit_first(){
        return test("submit() performs the expected task when called for the first time", args -> {
            ConcurrencyHandler handler = new ConcurrencyHandler();
            boolean[] finished = new boolean[]{false};
            Future<?> future = submitTaskToSetIndexTrue(handler, finished, 0);
            assertTaskCompleted(future, finished, 0);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> submit_repeated(){
        return test("submit() performs all the expected tasks when called repeatedly", args -> {
            ConcurrencyHandler handler = new ConcurrencyHandler();
            boolean[] finished = new boolean[]{false, false};
            Future<?> future1 = submitTaskToSetIndexTrue(handler, finished, 0);
            Future<?> future2 = submitTaskToSetIndexTrue(handler, finished, 1);
            assertTaskCompleted(future1, finished, 0);
            assertTaskCompleted(future2, finished, 1);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> submit_terminated(){
        return test("submit() throws the expected exception after a complete termination", args -> {
            ConcurrencyHandler handler = new ConcurrencyHandler();
            boolean[] finished = new boolean[]{false, false};
            submitTaskToSetIndexTrue(handler, finished, 0);
            assertShutDown(handler, 100);
            Assertions.assertThrows(RejectedExecutionException.class, 
                    () -> submitTaskToSetIndexTrue(handler, finished, 0));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> awaitTermination_noSubmit(){
        return test("awaitTermination() terminates properly when no calls to submit have been made",
                args -> assertShutDown(new ConcurrencyHandler(), 50));
    }
    
    @TestFactory
    Stream<DynamicNode> awaitTermination_ongoingTasks(){
        return test("awaitTermination() terminates properly when tasks are ongoing",
                args -> {
                    ConcurrencyHandler handler = new ConcurrencyHandler();
                    handler.submit(() -> {
                        try{
                            wait();
                        } catch (InterruptedException e) {}});
                    assertShutDown(handler, 100);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> awaitTermination_afterSubmit(){
        return test("awaitTermination() terminates properly when tasks are ongoing",
                args -> {
                    ConcurrencyHandler handler = new ConcurrencyHandler();
                    Future<?> future = submitTaskToSetIndexTrue(handler, new boolean[1], 0);
                    Assertions.assertDoesNotThrow(() -> future.get(100, TimeUnit.MILLISECONDS));
                    assertShutDown(handler, 100);
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
    
    private static void assertShutDown(ConcurrencyHandler handler, int milliseconds){
        handler.awaitTermination(milliseconds);
        Assertions.assertTrue(handler.isShutDown());
    }
    
    static record ConcurrencyHandlerTestArgs(String testCase){
        @Override
        public String toString(){
            return testCase;
        }
    }
}
