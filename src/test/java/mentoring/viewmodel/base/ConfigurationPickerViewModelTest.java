package mentoring.viewmodel.base;

import mentoring.configuration.DummyConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import mentoring.viewmodel.base.ConfigurationPickerViewModelTest.ConfigurationPickerViewModelArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import test.tools.TestArgs;
import test.tools.TestFramework;
import org.mockito.Mockito;

class ConfigurationPickerViewModelTest implements TestFramework<ConfigurationPickerViewModelArgs>{
    @Override
    public Stream<ConfigurationPickerViewModelArgs> argumentsSupplier() {
        return Stream.of(
                new ConfigurationPickerViewModelArgs("known configuration", 
                        DummyConfiguration.first, DummyConfiguration.first, 
                        List.of(DummyConfiguration.first), 
                        List.of(DummyConfiguration.first.toString()),
                        ConfigurationPickerViewModel.ConfigurationType.KNOWN, "foo"),
                new ConfigurationPickerViewModelArgs("file configuration",
                        ConfigurationPickerViewModelArgs.FILE_CONFIGURATION, 
                        DummyConfiguration.second,
                        List.of(DummyConfiguration.first, DummyConfiguration.second), 
                        List.of(DummyConfiguration.first.toString(), 
                                DummyConfiguration.second.toString()),
                        ConfigurationPickerViewModel.ConfigurationType.FILE,
                        FilePickerViewModelTest.DEFAULT_FILE_DATA.defaultFilePath()),
                new CopyConfigurationPickerViewModelArgs("known copy configuration", 
                        DummyConfiguration.first, DummyConfiguration.first, 
                        List.of(DummyConfiguration.first), 
                        List.of(DummyConfiguration.first.toString()),
                        ConfigurationPickerViewModel.ConfigurationType.KNOWN, "foo"),
                new CopyConfigurationPickerViewModelArgs("file copy configuration",
                        ConfigurationPickerViewModelArgs.FILE_CONFIGURATION, 
                        DummyConfiguration.second,
                        List.of(DummyConfiguration.first, DummyConfiguration.second), 
                        List.of(DummyConfiguration.first.toString(), 
                                DummyConfiguration.second.toString()),
                        ConfigurationPickerViewModel.ConfigurationType.FILE,
                        FilePickerViewModelTest.DEFAULT_FILE_DATA.defaultFilePath()));
    }
    
    @TestFactory
    Stream<DynamicNode> getKnownContent_expectedValues(){
        return test("getKnownContent() returns the expected values", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.expectedValues, 
                    viewModel.getKnownContent());
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> getKnownContent_unmodifiableList(){
        return test("getKnownContent() returns an unmodifiable list", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Assertions.assertThrows(UnsupportedOperationException.class,
                    () -> viewModel.getKnownContent().add("failure"));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getSelectedItem_defaultInstance(){
        return test("getSelectedItem() returns the default instance before any action", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.selectedConfiguration.toString(), 
                    viewModel.getSelectedItem().getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getSelectedItem_modifiable(){
        return test("getSelectedItem() returns a modifiable property", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Property<String> property = viewModel.getSelectedItem();
            ReadOnlyStringWrapper boundProperty = new ReadOnlyStringWrapper();
            boundProperty.bindBidirectional(property);
            String expectedValue = "expected bar";
            Assertions.assertAll(
                    () -> Assertions.assertDoesNotThrow(() -> boundProperty.setValue(expectedValue)),
                    () -> Assertions.assertEquals(expectedValue, property.getValue()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getSelectedItem_sameReturnValue(){
        return test("getSelectedItem() always return the same property", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Property<String> expectedProperty = viewModel.getSelectedItem();
            expectedProperty.setValue("foo");
            Assertions.assertSame(expectedProperty, viewModel.getSelectedItem());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getSelectedItem_modifiableIndependentlyForCopy(){
        return test("getSelectedItem() is independent for a copied instance", args -> {
            DummyConfigurationPickerViewModel baseViewModel = args.convert();
            DummyConfigurationPickerViewModel viewModel = 
                    new DummyConfigurationPickerViewModel(baseViewModel);
            String baseValue = baseViewModel.getSelectedItem().getValue();
            viewModel.getSelectedItem().setValue("foo");
            Assertions.assertEquals(baseValue, baseViewModel.getSelectedItem().getValue(),
                    "the base view model should be independent from the copy");
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getConfigurationSelectionType_defaultInstance(){
        return test("getConfigurationSelectionType() returns the default instance before any action", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.type, 
                    viewModel.getConfigurationSelectionType().getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getConfigurationSelectionType_modifiable(){
        return test("getConfigurationSelectionType() returns a modifiable property", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Property<ConfigurationPickerViewModel.ConfigurationType> property = 
                    viewModel.getConfigurationSelectionType();
            Property<ConfigurationPickerViewModel.ConfigurationType> boundProperty = 
                    new SimpleObjectProperty<>();
            boundProperty.bindBidirectional(property);
            ConfigurationPickerViewModel.ConfigurationType expectedValue = 
                    ConfigurationPickerViewModel.ConfigurationType.FILE;
            Assertions.assertAll(
                    () -> Assertions.assertDoesNotThrow(() -> boundProperty.setValue(expectedValue)),
                    () -> Assertions.assertEquals(expectedValue, property.getValue()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getConfigurationSelectionType_sameReturnValue(){
        return test("getSelectedItem() always return the same property", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Property<ConfigurationPickerViewModel.ConfigurationType> expectedProperty = 
                    viewModel.getConfigurationSelectionType();
            expectedProperty.setValue(ConfigurationPickerViewModel.ConfigurationType.FILE);
            Assertions.assertSame(expectedProperty, viewModel.getConfigurationSelectionType());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getConfigurationSelectionType_modifiableIndependentlyForCopy(){
        return test("getConfigurationSelectionType() is independent for a copied instance", args -> {
            DummyConfigurationPickerViewModel baseViewModel = args.convert();
            DummyConfigurationPickerViewModel viewModel = 
                    new DummyConfigurationPickerViewModel(baseViewModel);
            ConfigurationPickerViewModel.ConfigurationType baseValue = 
                    baseViewModel.getConfigurationSelectionType().getValue();
            viewModel.getConfigurationSelectionType()
                    .setValue(ConfigurationPickerViewModel.ConfigurationType.FILE);
            Assertions.assertEquals(baseValue,
                    baseViewModel.getConfigurationSelectionType().getValue(),
                    "the base view model should be independent from the copy");
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getConfiguration_expectedConfiguration(){
        return test("getConfiguration() returns the expected configuration", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            DummyConfiguration actualConfiguration = new DummyConfiguration("unexpected configuration");
            try {
                actualConfiguration = viewModel.getConfiguration();
            } catch (IOException e){
                Assertions.fail(e);
            }
            //Compare the file name to avoid false negative due to absolute vs relative path
            String expected = new File(args.expectedConfiguration.toString()).getName();
            String actual = new File(actualConfiguration.toString()).getName();
            Assertions.assertEquals(expected, actual);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getConfiguration_expectedConfiguration_repeatedWithoutChange(){
        return test("getConfiguration() returns the expected configuration", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            DummyConfiguration actualConfiguration = new DummyConfiguration("unexpected configuration");
            try {
                viewModel.getConfiguration();
                actualConfiguration = viewModel.getConfiguration();
            } catch (IOException e){
                Assertions.fail(e);
            }
            //Compare the file name to avoid false negative due to absolute vs relative path
            String expected = new File(args.expectedConfiguration.toString()).getName();
            String actual = new File(actualConfiguration.toString()).getName();
            Assertions.assertEquals(expected, actual);
        });
    }
    
    
    @TestFactory
    Stream<DynamicNode> getConfiguration_expectedConfiguration_repeatedWithChange(){
        return test("getConfiguration() returns the expected configuration", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            ConfigurationPickerViewModel.ConfigurationType oldType = args.type;
            ConfigurationPickerViewModel.ConfigurationType otherType = switch(oldType){
                case FILE -> ConfigurationPickerViewModel.ConfigurationType.KNOWN;
                case KNOWN -> ConfigurationPickerViewModel.ConfigurationType.FILE;
            };
            viewModel.configurationType.setValue(otherType);
            DummyConfiguration actualConfiguration = new DummyConfiguration("unexpected configuration");
            try {
                viewModel.getConfiguration();
                viewModel.configurationType.setValue(oldType);
                actualConfiguration = viewModel.getConfiguration();
            } catch (IOException e){
                Assertions.fail(e);
            }
            //Compare the file name to avoid false negative due to absolute vs relative path
            String expected = new File(args.expectedConfiguration.toString()).getName();
            String actual = new File(actualConfiguration.toString()).getName();
            Assertions.assertEquals(expected, actual);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("unique test case"), "constructor throws NPE on null input", args -> {
            DummyConfiguration configuration = DummyConfiguration.first;
            ConfigurationPickerViewModel.ConfigurationType type = 
                    ConfigurationPickerViewModel.ConfigurationType.FILE;
            @SuppressWarnings("unchecked")
            FilePickerViewModel<DummyConfiguration> filePicker = 
                    Mockito.mock(FilePickerViewModel.class);
            List<DummyConfiguration> values = List.of(configuration);
            Assertions.assertAll(
                    assertConstructorThrowsNPE(null, values, filePicker, type),
                    assertConstructorThrowsNPE(configuration, null, filePicker, type),
                    assertConstructorThrowsNPE(configuration, values, null, type),
                    assertConstructorThrowsNPE(configuration, values, filePicker, null));
        });
    }
    
    static Executable assertConstructorThrowsNPE(
            DummyConfiguration defaultSelectedInstance, List<DummyConfiguration> values,
            FilePickerViewModel<DummyConfiguration> filePicker,
            ConfigurationPickerViewModel.ConfigurationType defaultSelection){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new ConfigurationPickerViewModel<>(defaultSelectedInstance, values,
                        filePicker, defaultSelection));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_IllegalArgumentException(){
        return test(
                Stream.of(new ConfigurationPickerViewModelArgs("invalid test case", null,
                        DummyConfiguration.first, List.of(DummyConfiguration.second), null,
                        ConfigurationPickerViewModel.ConfigurationType.FILE, "")), 
                "constructor throws an exception when default instance not in values",
                args -> Assertions.assertThrows(IllegalArgumentException.class, () -> args.convert()));
    }
    
    @TestFactory
    Stream<DynamicNode> copyConstructor_NPE(){
        return test(Stream.of("unique testCase"), 
                "copy constructor throws an exception on null input",
                args -> Assertions.assertThrows(NullPointerException.class, 
                        () -> new ConfigurationPickerViewModel<>(null)));
    }
    
    static class DummyConfigurationPickerViewModel extends 
            ConfigurationPickerViewModel<DummyConfiguration>{
        
        public DummyConfigurationPickerViewModel(DummyConfiguration defaultSelectedInstance, 
                List<DummyConfiguration> values,
                FilePickerViewModel<DummyConfiguration> filePicker, 
                ConfigurationType defaultSelection) {
            super(defaultSelectedInstance, values, filePicker, defaultSelection);
        }
        
        public DummyConfigurationPickerViewModel(DummyConfigurationPickerViewModel toCopy){
            super(toCopy);
        }
    }
    
    static class ConfigurationPickerViewModelArgs extends TestArgs {
        final ConfigurationPickerViewModel.ConfigurationType type;
        final DummyConfiguration expectedConfiguration;
        final DummyConfiguration selectedConfiguration;
        final List<DummyConfiguration> inputValues;
        final List<String> expectedValues;
        final FilePickerViewModel<DummyConfiguration> filePicker;
        
        @SuppressWarnings("unchecked")
        ConfigurationPickerViewModelArgs(String testCase, DummyConfiguration expectedConfiguration,
                DummyConfiguration selectedConfiguration, List<DummyConfiguration> inputValues, 
                List<String> expectedValues, ConfigurationPickerViewModel.ConfigurationType type,
                String filePath){
            super(testCase);
            this.expectedConfiguration = expectedConfiguration;
            filePicker = new FilePickerViewModel<>(filePath, 
                    input -> new DummyConfiguration(input.getName()), List.of());
            this.selectedConfiguration = selectedConfiguration;
            this.inputValues = inputValues;
            this.expectedValues = expectedValues;
            this.type = type;
        }
        
        DummyConfigurationPickerViewModel convert(){
            return new DummyConfigurationPickerViewModel(selectedConfiguration, inputValues,
                    filePicker, type);
        }
        
        final static DummyConfiguration FILE_CONFIGURATION = 
                new DummyConfiguration(FilePickerViewModelTest.DEFAULT_FILE_DATA.defaultFilePath());
    }
    
    static class CopyConfigurationPickerViewModelArgs extends ConfigurationPickerViewModelArgs {
        
        CopyConfigurationPickerViewModelArgs(String testCase, DummyConfiguration expectedConfiguration,
                DummyConfiguration selectedConfiguration, List<DummyConfiguration> inputValues, 
                List<String> expectedValues, ConfigurationPickerViewModel.ConfigurationType type,
                String filePath){
            super(testCase, expectedConfiguration, selectedConfiguration, inputValues,
                    expectedValues, type, filePath);
        }
        
        @Override
        DummyConfigurationPickerViewModel convert(){
            return new DummyConfigurationPickerViewModel(super.convert());
        }
    }
}
