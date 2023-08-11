package mentoring.viewmodel.base;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import mentoring.configuration.DummyConfiguration;
import mentoring.viewmodel.base.BoundableConfigurationPickerViewModelTest.BoundableConfigurationPickerViewModelArgs;
import mentoring.viewmodel.base.ConfigurationPickerViewModelTest.DummyConfigurationPickerViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class BoundableConfigurationPickerViewModelTest implements 
        TestFramework<BoundableConfigurationPickerViewModelArgs>{
    
    @Override
    public Stream<BoundableConfigurationPickerViewModelArgs> argumentsSupplier(){
        return Stream.of(new BoundableConfigurationPickerViewModelArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> bind_boundToTwin(){
        return test("bind() actually binds VM to its twin", args -> {
            DummyConfigurationPickerViewModel twin = args.getTwin();
            BoundableConfigurationPickerViewModel<DummyConfiguration> viewModel = 
                    new BoundableConfigurationPickerViewModel<>(twin);
            viewModel.bind();
            modifyViewModel(twin, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
            assertViewModelIsAsExpected(viewModel, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> unbind_unboundFromTwin(){
        return test("unbind() actually unbinds VM from its twin", args -> {
            DummyConfigurationPickerViewModel twin = args.getTwin();
            BoundableConfigurationPickerViewModel<DummyConfiguration> viewModel = 
                    new BoundableConfigurationPickerViewModel<>(twin);
            viewModel.bind();
            modifyViewModel(twin, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
            viewModel.unbind();
            modifyViewModel(twin, INITIAL_TYPE, INITIAL_FILE, INITIAL_VALUE);
            assertViewModelIsAsExpected(viewModel, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> unbind_NoOpIfNotBound(){
        return test("unbind() has no effect if VM was not bound", args -> {
            DummyConfigurationPickerViewModel twin = args.getTwin();
            BoundableConfigurationPickerViewModel<DummyConfiguration> viewModel = 
                    new BoundableConfigurationPickerViewModel<>(twin);
            viewModel.unbind();
            modifyViewModel(twin, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
            assertViewModelIsAsExpected(viewModel, INITIAL_TYPE, INITIAL_FILE, INITIAL_VALUE);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> bind_noOpWhenRepeated(){
        return test("repeated calls to bind() leave VM bounded", args -> {
            DummyConfigurationPickerViewModel twin = args.getTwin();
            BoundableConfigurationPickerViewModel<DummyConfiguration> viewModel = 
                    new BoundableConfigurationPickerViewModel<>(twin);
            for (int i = 0; i < 3; i++){
                viewModel.bind();
            }
            modifyViewModel(twin, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
            assertViewModelIsAsExpected(viewModel, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> bind_noOpWhenRepeated_unbindOnce(){
        return test("a single call to unbind() suffice after repeated calls to bind()", args -> {
            DummyConfigurationPickerViewModel twin = args.getTwin();
            BoundableConfigurationPickerViewModel<DummyConfiguration> viewModel = 
                    new BoundableConfigurationPickerViewModel<>(twin);
            for (int i = 0; i < 3; i++){
                viewModel.bind();
            }
            modifyViewModel(twin, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
            viewModel.unbind();
            modifyViewModel(twin, INITIAL_TYPE, INITIAL_FILE, INITIAL_VALUE);
            assertViewModelIsAsExpected(viewModel, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> bind_bindAgainAfterUnbind(){
        return test("bind() works after unbind()", args -> {
            DummyConfigurationPickerViewModel twin = args.getTwin();
            BoundableConfigurationPickerViewModel<DummyConfiguration> viewModel = 
                    new BoundableConfigurationPickerViewModel<>(twin);
            viewModel.bind();
            modifyViewModel(twin, OTHER_TYPE, OTHER_FILE, OTHER_VALUE);
            viewModel.unbind();
            viewModel.bind();
            modifyViewModel(twin, INITIAL_TYPE, INITIAL_FILE, INITIAL_VALUE);
            assertViewModelIsAsExpected(viewModel, INITIAL_TYPE, INITIAL_FILE, INITIAL_VALUE);
        });
    }
    
    static void modifyViewModel(DummyConfigurationPickerViewModel bindingTarget,
            ConfigurationPickerViewModel.ConfigurationType type, File file, String value){
        bindingTarget.getConfigurationSelectionType().setValue(type);
        bindingTarget.getFilePicker().setCurrentFile(file);
        bindingTarget.getSelectedItem().setValue(value);
    }
    
    static void assertViewModelIsAsExpected(
            BoundableConfigurationPickerViewModel<DummyConfiguration> bound,
            ConfigurationPickerViewModel.ConfigurationType expectedType, File expectedFile,
            String expectedValue){
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedType, 
                        bound.getConfigurationSelectionType().getValue()),
                () -> Assertions.assertEquals(expectedFile.getName(), 
                        bound.getFilePicker().getCurrentFile().get().getName()),
                () -> Assertions.assertEquals(expectedValue,
                        bound.getSelectedItem().getValue()));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("unique test case"), "constructor throws an NPE on null input", 
                args -> Assertions.assertThrows(NullPointerException.class, 
                        () -> new BoundableConfigurationPickerViewModel<>(null)));
    }
    
    static final String INITIAL_VALUE = "barfoo";
    static final String OTHER_VALUE = "foobar";
    static final DummyConfiguration INITIAL_CONFIGURATION = new DummyConfiguration(INITIAL_VALUE);
    static final List<DummyConfiguration> VALUES = List.of(INITIAL_CONFIGURATION,
            new DummyConfiguration(OTHER_VALUE));
    static final ConfigurationPickerViewModel.ConfigurationType INITIAL_TYPE = 
            ConfigurationPickerViewModel.ConfigurationType.KNOWN;
    static final ConfigurationPickerViewModel.ConfigurationType OTHER_TYPE = 
            ConfigurationPickerViewModel.ConfigurationType.FILE;
    static final File INITIAL_FILE = FilePickerViewModelTest.FILE;
    static final File OTHER_FILE = FilePickerViewModelTest.OTHER_FILE;
    
    static class BoundableConfigurationPickerViewModelArgs extends TestArgs{
        
        BoundableConfigurationPickerViewModelArgs(String testCase) {
            super(testCase);
        }
        
        DummyConfigurationPickerViewModel getTwin(){
            return new DummyConfigurationPickerViewModel(INITIAL_CONFIGURATION, VALUES,
                    new FilePickerViewModel<>(INITIAL_FILE.getPath(), input -> null, List.of()), 
                    INITIAL_TYPE);
        }
    }
}
