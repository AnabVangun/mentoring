package mentoring.viewmodel.base;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import mentoring.configuration.Configuration;
import mentoring.io.Parser;
import mentoring.io.datareader.DataReader;
import mentoring.viewmodel.base.ConfigurationPickerViewModelTest.ConfigurationPickerViewModelArgs;
import mentoring.viewmodel.tasks.PersonGetter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

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
            ConfigurationPickerViewModel.ConfigurationParserSupplier<DummyConfiguration, 
                    DummyConfigurationParser> parserSupplier = ConfigurationPickerViewModelArgs.parserSupplier;
            PersonGetter.ReaderGenerator readerGenerator = ConfigurationPickerViewModelArgs.readerGenerator;
            Assertions.assertAll(
                    assertConstructorThrowsNPE(null, filePath, type, parserSupplier, readerGenerator),
                    assertConstructorThrowsNPE(configuration, null, type, parserSupplier, readerGenerator),
                    assertConstructorThrowsNPE(configuration, filePath, null, parserSupplier, readerGenerator),
                    assertConstructorThrowsNPE(configuration, filePath, type, null, readerGenerator),
                    assertConstructorThrowsNPE(configuration, filePath, type, parserSupplier, null));
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> setCurrentFile_NPE(){
        return test("setCurrentFile throws NPE on null input", args -> {
            ConfigurationPickerViewModel<DummyConfiguration, DummyConfigurationParser> viewModel = args.convert();
            Assertions.assertThrows(NullPointerException.class, () -> viewModel.setCurrentFile(null));
        });
    }
    
    static Executable assertConstructorThrowsNPE(
            DummyConfiguration defaultSelectedInstance, String defaultFilePath,
            ConfigurationPickerViewModel.ConfigurationType defaultSelection, 
            ConfigurationPickerViewModel.ConfigurationParserSupplier<DummyConfiguration, DummyConfigurationParser> 
                    parserSupplier, 
            PersonGetter.ReaderGenerator readerGenerator){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new ConfigurationPickerViewModel<>(defaultSelectedInstance, defaultFilePath, 
                        defaultSelection, parserSupplier, readerGenerator));
    }
    
    static class DummyConfigurationPickerViewModel extends 
            ConfigurationPickerViewModel<DummyConfiguration, DummyConfigurationParser>{
        
        public DummyConfigurationPickerViewModel(DummyConfiguration defaultSelectedInstance, 
                String defaultFilePath, ConfigurationType defaultSelection, 
                ConfigurationPickerViewModel.ConfigurationParserSupplier<DummyConfiguration, 
                        DummyConfigurationParser> parserGenerator, 
                PersonGetter.ReaderGenerator readerGenerator) {
            super(defaultSelectedInstance, defaultFilePath, defaultSelection, parserGenerator, 
                    readerGenerator);
        }
    }
    
    static class ConfigurationPickerViewModelArgs extends TestArgs {
        final String defaultFilePath;
        final ConfigurationPickerViewModel.ConfigurationType type;
        final DummyConfiguration configuration;
        static ConfigurationPickerViewModel.ConfigurationParserSupplier<DummyConfiguration,
                DummyConfigurationParser> parserSupplier;
        static final PersonGetter.ReaderGenerator readerGenerator = input -> new StringReader(input);
        
        ConfigurationPickerViewModelArgs(String testCase, DummyConfiguration configuration,
                String defaultFilePath, ConfigurationPickerViewModel.ConfigurationType type){
            super(testCase);
            this.configuration = configuration;
            this.defaultFilePath = defaultFilePath;
            this.type = type;
            parserSupplier = () -> new DummyConfigurationParser(defaultFilePath);
        }
        
        DummyConfigurationPickerViewModel convert(){
            return new DummyConfigurationPickerViewModel(configuration, defaultFilePath, type,
                    parserSupplier, readerGenerator);
        }
    }
    
    static class DummyConfiguration extends Configuration<DummyConfiguration> {
        DummyConfiguration(String name){
            super(name);
        }
        final static List<String> expectedKnownContent = List.of("first", "second");
        static DummyConfiguration first = new DummyConfiguration("first");
        static DummyConfiguration second = new DummyConfiguration("second");
        @Override
        public List<DummyConfiguration> values() {
            return List.of(first, second);
        }
    }
    
    static class DummyConfigurationParser extends Parser<DummyConfiguration> {
        private final String expectedResult; 
        DummyConfigurationParser(String expectedResult){
            super(Mockito.mock(DataReader.class));
            this.expectedResult = expectedResult;
        }

        @Override
        protected List<String> registerSpecificErrors(Map<String, Object> data) {
            return List.of();
        }

        @Override
        protected Set<String> getExpectedKeys() {
            return Set.of();
        }

        @Override
        protected DummyConfiguration buildObject(Map<String, Object> data) {
            throw new UnsupportedOperationException("Not needed in these tests.");
        }
        
        @Override
        public DummyConfiguration parse(Reader reader){
            return new DummyConfiguration(expectedResult);
        }
    }
}
