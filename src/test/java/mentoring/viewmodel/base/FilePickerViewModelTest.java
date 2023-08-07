package mentoring.viewmodel.base;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import mentoring.viewmodel.base.FilePickerViewModelTest.FilePickerViewModelArgs;
import mentoring.viewmodel.base.function.FileParser;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class FilePickerViewModelTest implements TestFramework<FilePickerViewModelArgs> {
    
    @Override
    public Stream<FilePickerViewModelArgs> argumentsSupplier(){
        return Stream.of(new FilePickerViewModelArgs("default file", DEFAULT_FILE_DATA));
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFile_defaultInstance(){
        return test("getCurrentFile() returns the default file before any action", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.defaultFileData.defaultFile, 
                    viewModel.getCurrentFile().getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFile_modifiedBySetCurrentFile(){
        return test("getCurrentFile() returns an observable invalidated by setCurrentFile()", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
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
            DummyFilePickerViewModel viewModel = args.convert();
            ReadOnlyObjectProperty<File> expected = viewModel.getCurrentFile();
            viewModel.setCurrentFile(new File("foo"));
            Assertions.assertSame(expected, viewModel.getCurrentFile());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFilePath_defaultValue(){
        return test("getCurrentFilePath() returns the default file path before any action", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.defaultFileData.defaultFilePath, 
                    viewModel.getCurrentFilePath().getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFilePath_modifiedBySetCurrentFile(){
        return test("getCurrentFilePath() returns an observable invalidated by setCurrentFile()", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
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
            DummyFilePickerViewModel viewModel = args.convert();
            ReadOnlyStringProperty expected = viewModel.getCurrentFilePath();
            viewModel.setCurrentFile(OTHER_FILE);
            Assertions.assertSame(expected, viewModel.getCurrentFilePath());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFileDirectory_defaultInstance(){
        return test("getCurrentFileDirectory() returns the default directory before any action", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.defaultFileData.defaultDirectory, 
                    viewModel.getCurrentFileDirectory().getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFileDirectory_modifiedBySetCurrentFile(){
        return test("getCurrentFileDirectory() returns an observable invalidated by setCurrentFile()", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
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
            DummyFilePickerViewModel viewModel = args.convert();
            ReadOnlyObjectProperty<File> expected = viewModel.getCurrentFile();
            viewModel.setCurrentFile(OTHER_FILE);
            Assertions.assertSame(expected, viewModel.getCurrentFile());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("unique test case"), "constructor throws NPE on null input", args ->
                Assertions.assertAll(
                        () -> Assertions.assertThrows(NullPointerException.class, 
                                () -> new DummyFilePickerViewModel(FILE_PATH, null)),
                        () -> Assertions.assertDoesNotThrow(
                                () -> new DummyFilePickerViewModel(null, input -> input.toString()))));
    }
    
    static class DummyFilePickerViewModel extends FilePickerViewModel<String>{
        DummyFilePickerViewModel(String defaultPath, FileParser<String> parser){
            super(defaultPath, parser);
        }
        
        @Override
        public List<Pair<String, List<String>>> getStandardExtensions(){
            throw new UnsupportedOperationException("not needed for this test");
        }
    }
    
    static class FilePickerViewModelArgs extends TestArgs {
        final FileData defaultFileData;
        
        FilePickerViewModelArgs(String testCase, FileData defaultFileData){
            super(testCase);
            this.defaultFileData = defaultFileData;
        }
        
        DummyFilePickerViewModel convert(){
            return new DummyFilePickerViewModel(defaultFileData.defaultFilePath, 
                    input -> input.getName());
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
