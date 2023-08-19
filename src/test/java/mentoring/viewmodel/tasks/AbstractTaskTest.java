package mentoring.viewmodel.tasks;

import java.util.stream.Stream;
import mentoring.viewmodel.tasks.AbstractTask.TaskCompletionCallback;
import mentoring.viewmodel.tasks.AbstractTaskTest.AbstractTaskArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class AbstractTaskTest implements TestFramework<AbstractTaskArgs>{
    
    @Override
    public Stream<AbstractTaskArgs> argumentsSupplier(){
        return Stream.concat(successfulArgumentsSupplier(), failedArgumentsSupplier());
    }
    
    Stream<AbstractTaskArgs> successfulArgumentsSupplier(){
        return Stream.of(new AbstractTaskArgs("successful task", true));
    }
    
    Stream<AbstractTaskArgs> failedArgumentsSupplier(){
        return Stream.of(new AbstractTaskArgs("failed task", false));
    }
    
    @TestFactory
    Stream<DynamicNode> succeeded_callSpecificAction(){
        return test(successfulArgumentsSupplier(), 
                "succeeded() calls specificActionOnSuccess()", args ->{
                    DummyTask task = args.task;
                    task.callWithoutException();
                    task.runTerminalMethod();
                    Assertions.assertTrue(task.onSuccessCalled);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> failed_callSpecificAction(){
        return test(failedArgumentsSupplier(), 
                "failed() calls specificActionOnFailure", args -> {
                    DummyTask task = args.task;
                    task.callWithoutException();
                    task.runTerminalMethod();
                    Assertions.assertTrue(task.onFailureCalled);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> completed_callback(){
        return test("callback method is called when the task is completed", args -> {
            DummyTask task = args.task;
            task.callWithoutException();
            task.runTerminalMethod();
            Mockito.verify(args.callback).accept(task);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("specific test case"), "constructor throws an NPE on null input", args -> 
                Assertions.assertThrows(NullPointerException.class, () -> new DummyTask(null, true)));
    }
    
    //TODO refactor: extract into a generic tested test tool.
    /*
    static List<EnumSet<AbstractTask.ALERTING_TYPE>> getAllSetsContaining(AbstractTask.ALERTING_TYPE needed){
        List<EnumSet<AbstractTask.ALERTING_TYPE>> sets = new LinkedList<>();
        sets.add(EnumSet.noneOf(AbstractTask.ALERTING_TYPE.class));
        for (AbstractTask.ALERTING_TYPE type : AbstractTask.ALERTING_TYPE.values()){
            List<EnumSet<AbstractTask.ALERTING_TYPE>> tmpSets = new LinkedList<>();
            for (EnumSet<AbstractTask.ALERTING_TYPE> set : sets){
                if(! type.equals(needed)){
                    tmpSets.add(set);
                }
                EnumSet<AbstractTask.ALERTING_TYPE> newSet = EnumSet.copyOf(set);
                newSet.add(type);
                tmpSets.add(newSet);
            }
            sets = tmpSets;
        }
        return sets;
    }*/
    
    static class AbstractTaskArgs extends TestArgs {
        final DummyTask task;
        @SuppressWarnings("unchecked")
        final TaskCompletionCallback<Void, DummyTask> callback = 
                Mockito.mock(TaskCompletionCallback.class);
        
        AbstractTaskArgs(String testCase, boolean success){
            super(testCase);
            this.task = new DummyTask(callback, success);
        }
    }
    
    static class DummyTask extends AbstractTask<Void, DummyTask> {
        final boolean success;
        boolean onSuccessCalled = false;
        boolean onFailureCalled = false;

        public DummyTask(TaskCompletionCallback<Void, DummyTask> callback, boolean success) {
            super(callback);
            this.success = success;
        }

        @Override
        protected void specificActionOnSuccess() {
            onSuccessCalled = true;
        }

        @Override
        protected void specificActionOnFailure() {
            onFailureCalled = true;
        }

        @Override
        protected Void call() throws Exception {
            if(!success){
                throw new RuntimeException("failed");
            }
            return null;
        }
        
        @Override
        protected DummyTask self(){
            return this;
        }
        
        void runTerminalMethod(){
            if(success){
                succeeded();
            } else {
                failed();
            }
        }
        
        void callWithoutException(){
            try{
                call();
            } catch (Exception e){
                //Do nothing
            }
        }
    }
}
