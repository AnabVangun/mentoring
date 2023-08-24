package mentoring.viewmodel.tasks;

import java.util.stream.Stream;
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
            ArgumentCaptor<ForbiddenMatchViewModel> captor = 
                    ArgumentCaptor.forClass(ForbiddenMatchViewModel.class);
            Mockito.verify(args.list).removeForbiddenMatch(captor.capture());
            Assertions.assertSame(args.toRemove, captor.getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeForbiddenMatch_NPE(){
        return test(Stream.of(new ForbiddenMatchRemovalTaskArgs("specific test case")), 
                "constructor throws an NPE on null input", args ->
                        Assertions.assertAll(
                                assertConstructorThrowsNPE(null, args.toRemove),
                                assertConstructorThrowsNPE(args.list, null)));
    }
    
    private static Executable assertConstructorThrowsNPE(ForbiddenMatchListViewModel list,
            ForbiddenMatchViewModel toRemove){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new ForbiddenMatchRemovalTask(list, toRemove));
    }
    
    static class ForbiddenMatchRemovalTaskArgs extends TestArgs {
        final ForbiddenMatchListViewModel list = Mockito.mock(ForbiddenMatchListViewModel.class);
        final ForbiddenMatchViewModel toRemove = Mockito.mock(ForbiddenMatchViewModel.class);

        ForbiddenMatchRemovalTaskArgs(String testCase) {
            super(testCase);
        }
        
        ForbiddenMatchRemovalTask convert(){
            return new ForbiddenMatchRemovalTask(list, toRemove);
        }
    }
}
