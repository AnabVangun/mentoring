package mentoring.viewmodel.base;

import mentoring.configuration.DummyConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
        DummyConfiguration fileConfiguration = 
                new DummyConfiguration(DEFAULT_FILE_DATA.defaultFilePath);
        return Stream.of(
                new ConfigurationPickerViewModelArgs("known configuration", 
                        DummyConfiguration.first, List.of(DummyConfiguration.first), 
                        List.of(DummyConfiguration.first.toString()), DEFAULT_FILE_DATA,
                        ConfigurationPickerViewModel.ConfigurationType.KNOWN),
                new ConfigurationPickerViewModelArgs("file configuration",
                        fileConfiguration, List.of(DummyConfiguration.second, fileConfiguration), 
                        List.of(DummyConfiguration.second.toString(), fileConfiguration.toString()), 
                        DEFAULT_FILE_DATA,
                        ConfigurationPickerViewModel.ConfigurationType.FILE));
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
            Assertions.assertEquals(args.defaultFileData.defaultFile, 
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
            File expected = OTHER_FILE;
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
            Assertions.assertEquals(args.defaultFileData.defaultFilePath, 
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
            String expected = OTHER_FILE_PATH;
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
            viewModel.setCurrentFile(OTHER_FILE);
            Assertions.assertSame(expected, viewModel.getCurrentFilePath());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFileDirectory_defaultInstance(){
        return test("getCurrentFileDirectory() returns the default directory before any action", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.defaultFileData.defaultDirectory, 
                    viewModel.getCurrentFileDirectory().getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFileDirectory_modifiedBySetCurrentFile(){
        return test("getCurrentFileDirectory() returns an observable invalidated by setCurrentFile()", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            ReadOnlyObjectProperty<File> observable = viewModel.getCurrentFile();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            File expected = OTHER_FILE;
            viewModel.setCurrentFile(expected);
            Assertions.assertAll(
                    () -> Mockito.verify(listener).invalidated(observable),
                    () -> Assertions.assertEquals(expected, observable.getValue()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFileDirectory_sameReturnValue(){
        return test("getSeCurrentFile() always return the same observable", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            ReadOnlyObjectProperty<File> expected = viewModel.getCurrentFile();
            viewModel.setCurrentFile(OTHER_FILE);
            Assertions.assertSame(expected, viewModel.getCurrentFile());
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
            DummyConfiguration actualConfiguration = new DummyConfiguration("unexpected configuration");
            try {
                actualConfiguration = viewModel.getConfiguration();
            } catch (IOException e){
                Assertions.fail(e);
            }
            //Compare the file name to avoid false negative due to absolute vs relative path
            String expected = new File(args.configuration.toString()).getName();
            String actual = new File(actualConfiguration.toString()).getName();
            Assertions.assertEquals(expected, actual);
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
            List<DummyConfiguration> values = List.of(configuration);
            Assertions.assertAll(
                    assertConstructorThrowsNPE(null, values, filePath, type, parserSupplier),
                    assertConstructorThrowsNPE(configuration, null, filePath, type, parserSupplier),
                    () -> Assertions.assertDoesNotThrow(() -> 
                            new ConfigurationPickerViewModel<>(configuration, values, null, type, 
                                    parserSupplier)),
                    assertConstructorThrowsNPE(configuration, values, filePath, null, parserSupplier),
                    assertConstructorThrowsNPE(configuration, values, filePath, type, null));
        });
    }
    
    static Executable assertConstructorThrowsNPE(
            DummyConfiguration defaultSelectedInstance, List<DummyConfiguration> values,
            String defaultFilePath,
            ConfigurationPickerViewModel.ConfigurationType defaultSelection, 
            ConfigurationParser<DummyConfiguration> parserSupplier){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new ConfigurationPickerViewModel<>(defaultSelectedInstance, values,
                        defaultFilePath, defaultSelection, parserSupplier));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_IllegalArgumentException(){
        return test(
                Stream.of(new ConfigurationPickerViewModelArgs("invalid test case",
                        DummyConfiguration.first, List.of(DummyConfiguration.second), null, 
                        DEFAULT_FILE_DATA, ConfigurationPickerViewModel.ConfigurationType.FILE)), 
                "constructor throws an exception when default instance not in values",
                args -> Assertions.assertThrows(IllegalArgumentException.class, () -> args.convert()));
    }
    
    static class DummyConfigurationPickerViewModel extends 
            ConfigurationPickerViewModel<DummyConfiguration>{
        
        public DummyConfigurationPickerViewModel(DummyConfiguration defaultSelectedInstance, 
                List<DummyConfiguration> values,
                String defaultFilePath, ConfigurationType defaultSelection, 
                ConfigurationParser<DummyConfiguration> parserGenerator) {
            super(defaultSelectedInstance, values, defaultFilePath, defaultSelection, 
                    parserGenerator);
        }
    }
    
    static class ConfigurationPickerViewModelArgs extends TestArgs {
        final FileData defaultFileData;
        final ConfigurationPickerViewModel.ConfigurationType type;
        final DummyConfiguration configuration;
        final List<DummyConfiguration> inputValues;
        final List<String> expectedValues;
        static ConfigurationParser<DummyConfiguration> parserSupplier;
        
        ConfigurationPickerViewModelArgs(String testCase, DummyConfiguration configuration,
                List<DummyConfiguration> inputValues, List<String> expectedValues,
                FileData defaultFileData, ConfigurationPickerViewModel.ConfigurationType type){
            super(testCase);
            this.configuration = configuration;
            this.inputValues = inputValues;
            this.expectedValues = expectedValues;
            this.defaultFileData = defaultFileData;
            this.type = type;
            parserSupplier = (input) -> new DummyConfiguration(input.getAbsolutePath());
        }
        
        DummyConfigurationPickerViewModel convert(){
            return new DummyConfigurationPickerViewModel(configuration, inputValues,
                    defaultFileData.defaultFilePath, type, parserSupplier);
        }
    }
    
    static record FileData(String defaultFilePath, File defaultFile, File defaultDirectory){}
    static final String PREFIX = "resources_test_mentoring_viewmodel_base"
            .replace('_', File.separatorChar);
    static final File PREFIX_FILE = new File(PREFIX);
    static final String FILE_NAME = "configurationPickerViewModelTestFile.txt";
    static final String FILE_PATH = PREFIX + File.separator + FILE_NAME;
    static final File FILE = new File(FILE_PATH);
    static final String OTHER_PREFIX = "%s%sconfigurationPickerViewModelTestDirectory"
            .formatted(PREFIX, File.separatorChar);
    static final String OTHER_FILE_NAME = "configurationPickerViewModelTestOtherFile.txt";
    static final String OTHER_FILE_PATH = OTHER_PREFIX + File.separator + OTHER_FILE_NAME;
    static final File OTHER_FILE = new File(OTHER_FILE_PATH);
    static final FileData DEFAULT_FILE_DATA = new FileData(FILE_PATH,
        FILE, PREFIX_FILE);
    
}
