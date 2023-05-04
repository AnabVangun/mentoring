package mentoring.viewmodel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import mentoring.viewmodel.MainViewModelTest.MainViewModelArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import test.tools.TestFramework;

class MainViewModelTest implements TestFramework<MainViewModelArgs>{

    @Override
    public Stream<MainViewModelArgs> argumentsSupplier() {
        return Stream.of(new MainViewModelArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> makeMatches_updateStatus(){
        return test("makeMatches() updates the view model properties", args -> {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            MainViewModel vm = new MainViewModel();
            vm.status.addListener(listener);
            Future<?> taskStatus = vm.makeMatches(executor);
            try{
                taskStatus.get(500, TimeUnit.MILLISECONDS);
            } catch (ExecutionException|InterruptedException|TimeoutException e){
                Assertions.fail(e);
            }
            Mockito.verify(listener, Mockito.atLeast(2)).invalidated(Mockito.any());
            Assertions.assertTrue(taskStatus.isDone());
            Assertions.assertEquals("""
                                    "Mentoré","Mentor","Coût"
                                    "Marceau Moussa (X2020)","Gaspard Marion (X2000)","383"
                                    "Leon Arthur (X2020)","Sandro Keelian (X1975)","700"
                                    "Rafael Pablo (X2020)","Lhya Elias (X1999)","410"
                                    "Laula Anthonin (X2020)","Hyacine Maddi (X2000)","800"
                                    "Paul Pierre (X2020)","Elsa Margaux (X1999)","210"
                                    """,
                    vm.status.get());
        });
    }
    
    static record MainViewModelArgs(String testCase){
        @Override 
        public String toString(){
            return testCase;
        }
    }
}
