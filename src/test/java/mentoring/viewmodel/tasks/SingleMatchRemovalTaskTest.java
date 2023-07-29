package mentoring.viewmodel.tasks;

import java.util.stream.Stream;
import mentoring.viewmodel.tasks.SingleMatchRemovalTaskTest.SingleMatchRemovalTaskArgs;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class SingleMatchRemovalTaskTest implements TestFramework<SingleMatchRemovalTaskArgs>{

    @Override
    public Stream<SingleMatchRemovalTaskArgs> argumentsSupplier() {
        return Stream.of(new SingleMatchRemovalTaskArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> removeSingleMatch_updateViewModel(){
        return test("call() updates the input view model", args -> {
            SingleMatchRemovalTask task = args.convert();
            try {
                task.call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            task.succeeded();
            @SuppressWarnings("unchecked")
            ArgumentCaptor<PersonMatchViewModel> captor = 
                    ArgumentCaptor.forClass(PersonMatchViewModel.class);
            Mockito.verify(args.updatedVM).remove(captor.capture());
            Assertions.assertSame(args.removedVM, captor.getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeSingleMatch_NPE(){
        return test("constructor throws an NPE on null input", args ->
                Assertions.assertAll(assertConstructorThrowsNPE(null, args.removedVM),
                        assertConstructorThrowsNPE(args.updatedVM, null)));
    }
    
    private static Executable assertConstructorThrowsNPE(PersonMatchesViewModel updatedVM,
            PersonMatchViewModel removedVM){
        return () -> Assertions.assertThrows(NullPointerException.class,
                () -> new SingleMatchRemovalTask(updatedVM, removedVM));
    }
    
    static class SingleMatchRemovalTaskArgs extends TestArgs{
        final PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
        final PersonMatchViewModel removedVM = Mockito.mock(PersonMatchViewModel.class);
        SingleMatchRemovalTaskArgs(String testCase){
            super(testCase);
        }
        
        SingleMatchRemovalTask convert(){
            return new SingleMatchRemovalTask(updatedVM, removedVM);
        }
    }
}
