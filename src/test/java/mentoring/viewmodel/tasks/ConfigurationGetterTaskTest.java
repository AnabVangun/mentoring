package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import mentoring.configuration.DummyConfiguration;
import mentoring.viewmodel.base.ConfigurableViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.tasks.AbstractTask.TaskCompletionCallback;
import mentoring.viewmodel.tasks.ConfigurationGetterTaskTest.ConfigurationGetterTaskArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ConfigurationGetterTaskTest implements TestFramework<ConfigurationGetterTaskArgs>{
    
    @Override
    public Stream<ConfigurationGetterTaskArgs> argumentsSupplier(){
        return Stream.of(new ConfigurationGetterTaskArgs("unique test case", 2));
    }
    
    @TestFactory
    Stream<DynamicNode> call_expectedResult(){
        return test("call() returns the expected result", args -> {
            ConfigurationGetterTask<DummyConfiguration> task = args.convert();
            Assertions.assertEquals(args.expectedConfiguration, callTask(task));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> succeeded_updatesVM(){
        return test("succeeded() updates the configurable view models", args -> {
            ConfigurationGetterTask<DummyConfiguration> task = args.convert();
            callTask(task);
            task.succeeded();
            Assertions.assertAll(
                    args.resultViewModels.stream().map(vm -> 
                            () -> Mockito.verify(vm).setConfiguration(args.expectedConfiguration)));
        });
    }
    
    @TestFactory
    @SuppressWarnings("unchecked")
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("unique test case"), "constructor throws NPE on null input", args -> {
            ConfigurationPickerViewModel<DummyConfiguration> configurationVM = 
                    Mockito.mock(ConfigurationPickerViewModel.class);
            List<ConfigurableViewModel<DummyConfiguration>> resultVM = 
                    List.of(Mockito.mock(ConfigurableViewModel.class),
                            Mockito.mock(ConfigurableViewModel.class));
            List<ConfigurableViewModel<DummyConfiguration>> resultVMWithNull = new ArrayList<>();
            resultVMWithNull.add(Mockito.mock(ConfigurableViewModel.class));
            resultVMWithNull.add(null);
            TaskCompletionCallback<DummyConfiguration> callback = configuration -> {};
            Assertions.assertAll(assertConstructorThrowsNPE(null, resultVM, callback),
                    assertConstructorThrowsNPE(configurationVM, null, callback),
                    assertConstructorThrowsNPE(configurationVM, resultVMWithNull, callback),
                    assertConstructorThrowsNPE(configurationVM, resultVM, null));
        });
    }
    
    Executable assertConstructorThrowsNPE(
            ConfigurationPickerViewModel<DummyConfiguration> configurationVM,
            List<ConfigurableViewModel<DummyConfiguration>> resultVM,
            TaskCompletionCallback<DummyConfiguration> callback){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new ConfigurationGetterTask<>(configurationVM, resultVM, callback));
    }
    
    DummyConfiguration callTask(ConfigurationGetterTask<DummyConfiguration> task){
        try {
            return task.call();
        } catch (Exception ex) {
            Assertions.fail("Unreachable code", ex);
            throw new IllegalStateException("unreachable code");
        }
    }
    
    static class ConfigurationGetterTaskArgs extends TestArgs {
        @SuppressWarnings("unchecked")
        final ConfigurationPickerViewModel<DummyConfiguration> configurationVM = 
                Mockito.mock(ConfigurationPickerViewModel.class);
        final List<ConfigurableViewModel<DummyConfiguration>> resultViewModels;
        final DummyConfiguration expectedConfiguration = 
                new DummyConfiguration("expected configuration");
        final TaskCompletionCallback<DummyConfiguration> callback = configuration -> {};
        @SuppressWarnings("unchecked")
        ConfigurationGetterTaskArgs(String testCase, int numberOfResultVM){
            super(testCase);
            try {
                Mockito.when(configurationVM.getConfiguration()).thenReturn(expectedConfiguration);
            } catch (IOException ex) {
                Assertions.fail("unreachable code", ex);
            }
            List<ConfigurableViewModel<DummyConfiguration>> tmpResultVM = 
                    new ArrayList<>(numberOfResultVM);
            for (int i = 0; i < numberOfResultVM ; i++){
                tmpResultVM.add(Mockito.mock(ConfigurableViewModel.class));
            }
            resultViewModels = List.copyOf(tmpResultVM);
        }
        
        ConfigurationGetterTask<DummyConfiguration> convert(){
            return new ConfigurationGetterTask<>(configurationVM, resultViewModels, callback);
        }
    }
}
