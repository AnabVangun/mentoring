package mentoring.viewmodel.tasks;

import java.util.stream.Stream;
import mentoring.viewmodel.tasks.SingleMatchRemovalTaskTest.SingleMatchRemovalTaskArgs;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestFramework;

class SingleMatchRemovalTaskTest implements TestFramework<SingleMatchRemovalTaskArgs>{

    @Override
    public Stream<SingleMatchRemovalTaskArgs> argumentsSupplier() {
        return Stream.of(new SingleMatchRemovalTaskArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> removeSingleMatche_updateViewModel(){
        //TODO refactor test: move stuff to SingleMatchRemovalTaskArgs
        return test("call() updates the input view model", args -> {
            PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
            PersonMatchViewModel mockRemoved = Mockito.mock(PersonMatchViewModel.class);
            SingleMatchRemovalTask task = new SingleMatchRemovalTask(updatedVM, mockRemoved);
            try {
                task.call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            task.succeeded();
            @SuppressWarnings("unchecked")
            ArgumentCaptor<PersonMatchViewModel> captor = 
                    ArgumentCaptor.forClass(PersonMatchViewModel.class);
            Mockito.verify(updatedVM).remove(captor.capture());
            Assertions.assertSame(mockRemoved, captor.getValue());
        });
    }
    
    static record SingleMatchRemovalTaskArgs(String testCase){
        
        @Override 
        public String toString(){
            return testCase;
        }
    }
}
