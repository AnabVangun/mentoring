package mentoring.viewmodel.tasks;

import java.util.stream.Stream;
import mentoring.viewmodel.datastructure.MatchStatus;
import mentoring.viewmodel.tasks.SingleMatchRemovalTaskTest.SingleMatchRemovalTaskArgs;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonViewModel;
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
    Stream<DynamicNode> removeSingleMatch_updateMenteeViewModel(){
        return test("call() updates the mentee view model", args -> {
            SingleMatchRemovalTask task = args.convert();
            try {
                task.call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            task.succeeded();
            Assertions.assertFalse(args.menteeVM.getStatus().getStyleClass()
                    .contains(MatchStatus.MatchFlag.MANUAL_MATCH.getStyleClass()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeSingleMatch_updateMentorViewModel(){
        return test("call() updates the mentor view model", args -> {
            SingleMatchRemovalTask task = args.convert();
            try {
                task.call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            task.succeeded();
            Assertions.assertFalse(args.mentorVM.getStatus().getStyleClass()
                    .contains(MatchStatus.MatchFlag.MANUAL_MATCH.getStyleClass()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeSingleMatch_NPE(){
        return test("constructor throws an NPE on null input", args ->
                Assertions.assertAll(
                        assertConstructorThrowsNPE(null, args.removedVM, args.menteeVM, 
                                args.mentorVM, args.callback),
                        assertConstructorThrowsNPE(args.updatedVM, null, args.menteeVM, 
                                args.mentorVM, args.callback),
                        assertConstructorThrowsNPE(args.updatedVM, args.removedVM, null, 
                                args.mentorVM, args.callback),
                        assertConstructorThrowsNPE(args.updatedVM, args.removedVM, args.menteeVM, 
                                null, args.callback),
                        assertConstructorThrowsNPE(args.updatedVM, args.removedVM, args.menteeVM,
                                args.mentorVM, null)));
    }
    
    private static Executable assertConstructorThrowsNPE(PersonMatchesViewModel updatedVM,
            PersonMatchViewModel removedVM,PersonViewModel menteeVM, PersonViewModel mentorVM, 
            AbstractTask.TaskCompletionCallback<? super Void> callback){
        return () -> Assertions.assertThrows(NullPointerException.class,
                () -> new SingleMatchRemovalTask(updatedVM, removedVM, menteeVM, mentorVM, callback));
    }
    
    static class SingleMatchRemovalTaskArgs extends TestArgs{
        final PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
        final PersonMatchViewModel removedVM = Mockito.mock(PersonMatchViewModel.class);
        final PersonViewModel menteeVM = Mockito.mock(PersonViewModel.class);
        final PersonViewModel mentorVM = Mockito.mock(PersonViewModel.class);
        final MatchStatus menteeStatus = new MatchStatus();
        final MatchStatus mentorStatus = new MatchStatus();
        final AbstractTask.TaskCompletionCallback<Object> callback = task -> {};
        
        SingleMatchRemovalTaskArgs(String testCase){
            super(testCase);
            menteeStatus.add(MatchStatus.MatchFlag.MANUAL_MATCH);
            mentorStatus.add(MatchStatus.MatchFlag.MANUAL_MATCH);
            Mockito.when(menteeVM.getStatus()).thenReturn(menteeStatus);
            Mockito.when(mentorVM.getStatus()).thenReturn(mentorStatus);
        }
        
        SingleMatchRemovalTask convert(){
            return new SingleMatchRemovalTask(updatedVM, removedVM, menteeVM, mentorVM, callback);
        }
    }
}
