package mentoring.viewmodel.base;

import mentoring.configuration.DummyConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import mentoring.viewmodel.base.ConfigurationPickerViewModelTest.ConfigurationPickerViewModelArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;
import mentoring.viewmodel.base.function.ConfigurationParser;

class ConfigurationPickerViewModelTest implements TestFramework<ConfigurationPickerViewModelArgs>{
    @Override
    public Stream<ConfigurationPickerViewModelArgs> argumentsSupplier() {
        return Stream.of(
                new ConfigurationPickerViewModelArgs("known configuration", 
                        DummyConfiguration.first, "G:\\foo", 
                        ConfigurationPickerViewModel.ConfigurationType.KNOWN),
                new ConfigurationPickerViewModelArgs("file configuration",
                        new DummyConfiguration("Z:\\path"), "Z:\\path",
                        ConfigurationPickerViewModel.ConfigurationType.FILE));
    }
    
    @TestFactory
    Stream<DynamicNode> getKnownContent_expectedValues(){
        return test("getKnownContent() returns the expected values", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Assertions.assertEquals(DummyConfiguration.expectedKnownContent, 
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
            Assertions.assertEquals(args.configuration.toString(), 
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
    Stream<DynamicNode> getCurrentFile_defaultInstance(){
        return test("getCurrentFile() returns the default file before any action", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Assertions.assertEquals(new File(args.defaultFilePath), 
                    viewModel.getCurrentFile().getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFile_modifiedBySetCurrentFile(){
        return test("getCurrentFile() returns an observable invalidated by setCurrentFile()", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            ReadOnlyObjectProperty<File> observable = viewModel.getCurrentFile();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            File expected = new File("expected bar");
            viewModel.setCurrentFile(expected);
            Assertions.assertAll(
                    () -> Mockito.verify(listener).invalidated(observable),
                    () -> Assertions.assertEquals(expected, observable.getValue()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFile_sameReturnValue(){
        return test("getSeCurrentFile() always return the same observable", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            ReadOnlyObjectProperty<File> expected = viewModel.getCurrentFile();
            viewModel.setCurrentFile(new File("foo"));
            Assertions.assertSame(expected, viewModel.getCurrentFile());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFilePath_defaultValue(){
        return test("getCurrentFilePath() returns the default file path before any action", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.defaultFilePath, 
                    viewModel.getCurrentFilePath().getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFilePath_modifiedBySetCurrentFile(){
        return test("getCurrentFilePath() returns an observable invalidated by setCurrentFile()", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            ReadOnlyStringProperty observable = viewModel.getCurrentFilePath();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            String expected = "F:\\bar";
            viewModel.setCurrentFile(new File(expected));
            Assertions.assertAll(
                    () -> Mockito.verify(listener).invalidated(observable),
                    () -> Assertions.assertEquals(expected, observable.getValue()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFilePath_sameReturnValue(){
        return test("getSeCurrentFilePath() always return the same observable", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            ReadOnlyStringProperty expected = viewModel.getCurrentFilePath();
            viewModel.setCurrentFile(new File("foo"));
            Assertions.assertSame(expected, viewModel.getCurrentFilePath());
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
    Stream<DynamicNode> getConfiguration_expectedConfiguration(){
        return test("getConfiguration() returns the expected configuration", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            DummyConfiguration actual = new DummyConfiguration("unexpected configuration");
            try {
                actual = viewModel.getConfiguration();
            } catch (IOException e){
                Assertions.fail(e);
            }
            Assertions.assertEquals(args.configuration.toString(), actual.toString());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("unique test case"), "constructor throws NPE on null input", args -> {
            DummyConfiguration configuration = DummyConfiguration.first;
            String filePath = "foo";
            ConfigurationPickerViewModel.ConfigurationType type = 
                    ConfigurationPickerViewModel.ConfigurationType.FILE;
            ConfigurationParser<DummyConfiguration> parserSupplier = 
                    ConfigurationPickerViewModelArgs.parserSupplier;
            Assertions.assertAll(
                    assertConstructorThrowsNPE(null, filePath, type, parserSupplier),
                    assertConstructorThrowsNPE(configuration, null, type, parserSupplier),
                    assertConstructorThrowsNPE(configuration, filePath, null, parserSupplier),
                    assertConstructorThrowsNPE(configuration, filePath, type, null));
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> setCurrentFile_NPE(){
        return test("setCurrentFile throws NPE on null input", args -> {
            ConfigurationPickerViewModel<DummyConfiguration> viewModel = args.convert();
            Assertions.assertThrows(NullPointerException.class, () -> viewModel.setCurrentFile(null));
        });
    }
    
    static Executable assertConstructorThrowsNPE(
            DummyConfiguration defaultSelectedInstance, String defaultFilePath,
            ConfigurationPickerViewModel.ConfigurationType defaultSelection, 
            ConfigurationParser<DummyConfiguration> parserSupplier){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new ConfigurationPickerViewModel<>(defaultSelectedInstance, defaultFilePath, 
                        defaultSelection, parserSupplier));
    }
    
    static class DummyConfigurationPickerViewModel extends 
            ConfigurationPickerViewModel<DummyConfiguration>{
        
        public DummyConfigurationPickerViewModel(DummyConfiguration defaultSelectedInstance, 
                String defaultFilePath, ConfigurationType defaultSelection, 
                ConfigurationParser<DummyConfiguration> parserGenerator) {
            super(defaultSelectedInstance, defaultFilePath, defaultSelection, parserGenerator);
        }
    }
    
    static class ConfigurationPickerViewModelArgs extends TestArgs {
        final String defaultFilePath;
        final ConfigurationPickerViewModel.ConfigurationType type;
        final DummyConfiguration configuration;
        static ConfigurationParser<DummyConfiguration> parserSupplier;
        
        ConfigurationPickerViewModelArgs(String testCase, DummyConfiguration configuration,
                String defaultFilePath, ConfigurationPickerViewModel.ConfigurationType type){
            super(testCase);
            this.configuration = configuration;
            this.defaultFilePath = defaultFilePath;
            this.type = type;
            parserSupplier = (input) -> new DummyConfiguration(input.getAbsolutePath());
        }
        
        DummyConfigurationPickerViewModel convert(){
            return new DummyConfigurationPickerViewModel(configuration, defaultFilePath, type,
                    parserSupplier);
        }
    }
    
}
