package mentoring.viewmodel.tasks;

import java.util.stream.Stream;
import mentoring.datastructure.Person;
import mentoring.match.ForbiddenMatches;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;
import mentoring.viewmodel.datastructure.ForbiddenMatchViewModel;
import mentoring.viewmodel.tasks.ForbiddenMatchRemovalTaskTest.ForbiddenMatchRemovalTaskArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ForbiddenMatchRemovalTaskTest implements TestFramework<ForbiddenMatchRemovalTaskArgs>{
    
    @Override
    public Stream<ForbiddenMatchRemovalTaskArgs> argumentsSupplier(){
        return Stream.of(new ForbiddenMatchRemovalTaskArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> removeForbiddenMatch_updateViewModel(){
        return test("call() updates the input view model", args -> {
            ForbiddenMatchRemovalTask task = args.convert();
            try {
                task.call();
            } catch (Exception e){
                Assertions.fail("normally unreachable code", e);
            }
            task.succeeded();
            ArgumentCaptor<ForbiddenMatchViewModel> vmCaptor = 
                    ArgumentCaptor.forClass(ForbiddenMatchViewModel.class);
            Mockito.verify(args.list).removeForbiddenMatch(vmCaptor.capture(), Mockito.any());
            Assertions.assertSame(args.toRemove, vmCaptor.getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeForbiddenMatch_NPE(){
        return test(Stream.of(new ForbiddenMatchRemovalTaskArgs("specific test case")), 
                "constructor throws an NPE on null input", args ->
                        Assertions.assertAll(
                                assertConstructorThrowsNPE(null, args.toRemove, args.handler, args.callback),
                                assertConstructorThrowsNPE(args.list, null, args.handler, args.callback),
                                assertConstructorThrowsNPE(args.list, args.toRemove, null, args.callback),
                                assertConstructorThrowsNPE(args.list, args.toRemove, args.handler, null)));
    }
    
    private static Executable assertConstructorThrowsNPE(ForbiddenMatchListViewModel list,
            ForbiddenMatchViewModel toRemove, ForbiddenMatches<Person, Person> handler,
            AbstractTask.TaskCompletionCallback<Object> callback){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new ForbiddenMatchRemovalTask(list, toRemove, handler, callback));
    }
    
    static class ForbiddenMatchRemovalTaskArgs extends TestArgs {
        final ForbiddenMatchListViewModel list = Mockito.mock(ForbiddenMatchListViewModel.class);
        final ForbiddenMatchViewModel toRemove = Mockito.mock(ForbiddenMatchViewModel.class);
        final ForbiddenMatches<Person, Person> handler = new ForbiddenMatches<>();
        final AbstractTask.TaskCompletionCallback<Object> callback = task -> {};

        ForbiddenMatchRemovalTaskArgs(String testCase) {
            super(testCase);
        }
        
        ForbiddenMatchRemovalTask convert(){
            return new ForbiddenMatchRemovalTask(list, toRemove, handler, callback);
        }
    }
}
