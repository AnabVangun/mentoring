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
import mentoring.viewmodel.base.FilePickerViewModelTest.FileData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import test.tools.TestArgs;
import test.tools.TestFramework;
import mentoring.viewmodel.base.function.FileParser;
import org.apache.commons.lang3.tuple.Pair;

class ConfigurationPickerViewModelTest implements TestFramework<ConfigurationPickerViewModelArgs>{
    @Override
    public Stream<ConfigurationPickerViewModelArgs> argumentsSupplier() {
        DummyConfiguration fileConfiguration = 
                new DummyConfiguration(DEFAULT_FILE_DATA.defaultFilePath());
        return Stream.of(
                new ConfigurationPickerViewModelArgs("known configuration", 
                        DummyConfiguration.first, List.of(DummyConfiguration.first), 
                        List.of(DummyConfiguration.first.toString()), DEFAULT_FILE_DATA,
                        ConfigurationPickerViewModel.ConfigurationType.KNOWN,
                        List.of(Pair.of("foo", List.of("*.bar","*.foobar")))),
                new ConfigurationPickerViewModelArgs("file configuration",
                        fileConfiguration, List.of(DummyConfiguration.second, fileConfiguration), 
                        List.of(DummyConfiguration.second.toString(), fileConfiguration.toString()), 
                        DEFAULT_FILE_DATA,
                        ConfigurationPickerViewModel.ConfigurationType.FILE,
                        List.of(Pair.of("foo", List.of("foobar")), Pair.of("bar", List.of("barfoo")))));
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
    Stream<DynamicNode> getStandardExtensions_expectedValue(){
        return test("getStandardExtensions() returns the expected value", args -> {
            DummyConfigurationPickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.expectedExtensions, viewModel.getStandardExtensions());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("unique test case"), "constructor throws NPE on null input", args -> {
            DummyConfiguration configuration = DummyConfiguration.first;
            String filePath = "foo";
            ConfigurationPickerViewModel.ConfigurationType type = 
                    ConfigurationPickerViewModel.ConfigurationType.FILE;
            FileParser<DummyConfiguration> parserSupplier = 
                    ConfigurationPickerViewModelArgs.parserSupplier;
            List<DummyConfiguration> values = List.of(configuration);
            List<Pair<String, List<String>>> extensions = List.of();
            Assertions.assertAll(
                    assertConstructorThrowsNPE(null, values, filePath, type, parserSupplier, extensions),
                    assertConstructorThrowsNPE(configuration, null, filePath, type, parserSupplier, extensions),
                    () -> Assertions.assertDoesNotThrow(() -> 
                            new ConfigurationPickerViewModel<>(configuration, values, null, type, 
                                    parserSupplier, extensions)),
                    assertConstructorThrowsNPE(configuration, values, filePath, null, parserSupplier, extensions),
                    assertConstructorThrowsNPE(configuration, values, filePath, type, null, extensions),
                    assertConstructorThrowsNPE(configuration, values, filePath, type, parserSupplier, null));
        });
    }
    
    static Executable assertConstructorThrowsNPE(
            DummyConfiguration defaultSelectedInstance, List<DummyConfiguration> values,
            String defaultFilePath,
            ConfigurationPickerViewModel.ConfigurationType defaultSelection, 
            FileParser<DummyConfiguration> parserSupplier, 
            List<Pair<String, List<String>>> extensions){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new ConfigurationPickerViewModel<>(defaultSelectedInstance, values,
                        defaultFilePath, defaultSelection, parserSupplier, extensions));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_IllegalArgumentException(){
        return test(
                Stream.of(new ConfigurationPickerViewModelArgs("invalid test case",
                        DummyConfiguration.first, List.of(DummyConfiguration.second), null, 
                        DEFAULT_FILE_DATA, ConfigurationPickerViewModel.ConfigurationType.FILE,
                        List.of())), 
                "constructor throws an exception when default instance not in values",
                args -> Assertions.assertThrows(IllegalArgumentException.class, () -> args.convert()));
    }
    
    static class DummyConfigurationPickerViewModel extends 
            ConfigurationPickerViewModel<DummyConfiguration>{
        
        public DummyConfigurationPickerViewModel(DummyConfiguration defaultSelectedInstance, 
                List<DummyConfiguration> values,
                String defaultFilePath, ConfigurationType defaultSelection, 
                FileParser<DummyConfiguration> parserGenerator, 
                List<Pair<String, List<String>>> extensions) {
            super(defaultSelectedInstance, values, defaultFilePath, defaultSelection, 
                    parserGenerator, extensions);
        }
    }
    
    static class ConfigurationPickerViewModelArgs extends TestArgs {
        final FileData defaultFileData;
        final ConfigurationPickerViewModel.ConfigurationType type;
        final DummyConfiguration configuration;
        final List<DummyConfiguration> inputValues;
        final List<String> expectedValues;
        static FileParser<DummyConfiguration> parserSupplier;
        final List<Pair<String, List<String>>> expectedExtensions;
        
        ConfigurationPickerViewModelArgs(String testCase, DummyConfiguration configuration,
                List<DummyConfiguration> inputValues, List<String> expectedValues,
                FileData defaultFileData, ConfigurationPickerViewModel.ConfigurationType type,
                List<Pair<String, List<String>>> expectedExtensions){
            super(testCase);
            this.configuration = configuration;
            this.inputValues = inputValues;
            this.expectedValues = expectedValues;
            this.defaultFileData = defaultFileData;
            this.type = type;
            this.expectedExtensions = expectedExtensions;
            parserSupplier = (input) -> new DummyConfiguration(input.getAbsolutePath());
        }
        
        DummyConfigurationPickerViewModel convert(){
            return new DummyConfigurationPickerViewModel(configuration, inputValues,
                    defaultFileData.defaultFilePath(), type, parserSupplier, expectedExtensions);
        }
    }
    
    static FileData DEFAULT_FILE_DATA = FilePickerViewModelTest.DEFAULT_FILE_DATA;
}
