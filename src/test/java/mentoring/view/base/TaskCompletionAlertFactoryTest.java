package mentoring.view.base;

import java.util.stream.Stream;
import javafx.concurrent.Worker.State;
import javafx.stage.Stage;
import mentoring.view.base.TaskCompletionAlertFactoryTest.TaskCompletionAlertFactoryArgs;
import mentoring.viewmodel.tasks.AbstractTask;
import mentoring.viewmodel.tasks.AbstractTask.TaskCompletionCallback;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import test.tools.TestArgs;
import test.tools.TestFramework;

@ExtendWith(ApplicationExtension.class)
class TaskCompletionAlertFactoryTest implements TestFramework<TaskCompletionAlertFactoryArgs>{
    
    @Start
    private void start(Stage stage) {
        //no-op
    }
    
    @Override
    public Stream<TaskCompletionAlertFactoryArgs> argumentsSupplier(){
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    @TestFactory
    Stream<DynamicNode> alertOnFailure_alert(FxRobot robot){
        return test(Stream.of("specific test case"), "alertOnFailure() alert the user on task failure", args -> {
            TaskCompletionCallback<Void> callback = 
                    TaskCompletionAlertFactory.alertOnFailure(Void.class, except -> "foo");
            @SuppressWarnings("unchecked")
            AbstractTask<Void> task = Mockito.mock(AbstractTask.class);
            /* TODO: actually implement test: check that an alert window appears and its label
            TODO: extend test to alertOnSuccessAndFailure
            Mockito.when(task.getState()).thenReturn(State.FAILED);
            callback.accept(task);
            robot.robotContext().getWindowFinder().
            Dialog alert = robot.lookup(".dialog").queryAs(Dialog.class);
            */
        });
    }
    
    @TestFactory
    Stream<DynamicNode> alertOnSuccessAndFailure_NPE(){
        return test(Stream.of("unique test case"), "alertOnSuccessAndFailure throws NPE", args ->
            Assertions.assertAll(
                    () -> assertThrowsNPE(
                            () -> TaskCompletionAlertFactory.alertOnSuccessAndFailure(null,
                                    () -> "foo", except -> "bar")),
                    () -> assertThrowsNPE(
                            () -> TaskCompletionAlertFactory.alertOnSuccessAndFailure(Void.class, 
                                    null, except -> "bar")),
                    () -> assertThrowsNPE(
                            () -> TaskCompletionAlertFactory.alertOnSuccessAndFailure(String.class, 
                                    () -> "foo", null))
            ));
    }
    
    @TestFactory
    Stream<DynamicNode> alertOnFailure_NPE(){
        return test(Stream.of("unique test case"), "alertOnFailure throws NPE", args ->
            Assertions.assertAll(
                    () -> assertThrowsNPE(
                            () -> TaskCompletionAlertFactory.alertOnFailure(null, except -> "bar")),
                    () -> assertThrowsNPE(
                            () -> TaskCompletionAlertFactory.alertOnFailure(String.class, null))
            ));
    }
    
    static void assertThrowsNPE(Executable executable){
        Assertions.assertThrows(NullPointerException.class, executable);
    }
    
    static abstract class TaskCompletionAlertFactoryArgs extends TestArgs{
        TaskCompletionAlertFactoryArgs(String testCase){
            super(testCase);
        }
    }
}
