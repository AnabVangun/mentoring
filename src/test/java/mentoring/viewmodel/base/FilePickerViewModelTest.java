package mentoring.viewmodel.base;

import java.io.File;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import test.tools.TestFramework;

abstract class FilePickerViewModelTest<T, VM extends FilePickerViewModel<T>, 
        A extends FilePickerViewModelArgs<T,VM>> implements TestFramework<A>{
    
    @TestFactory
    protected Stream<DynamicNode> getCurrentFile_default(){
        return test("getCurrentFile() has the expected behaviour before any action", args ->{
            VM viewModel = args.convert();
            verifyDefaultBehavior(args, viewModel);
        });
    }
    
    protected abstract void verifyDefaultBehavior(A args, VM viewModel);
    
    @TestFactory
    Stream<DynamicNode> getCurrentFile_modifiedBySetCurrentFile(){
        return test("getCurrentFile() returns an observable invalidated by setCurrentFile()", args -> {
            VM viewModel = args.convert();
            ReadOnlyObjectProperty<File> observable = viewModel.getCurrentFile();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            File expected = getOtherSelectableFile(args);
            viewModel.setCurrentFile(expected);
            Assertions.assertAll(
                    () -> Mockito.verify(listener).invalidated(observable),
                    () -> Assertions.assertEquals(expected, observable.getValue()));
        });
    }
    
    protected abstract File getOtherSelectableFile(A args);
    
    @TestFactory
    Stream<DynamicNode> getCurrentFile_sameReturnValue(){
        return test("getCurrentFile() always return the same observable", args -> {
            VM viewModel = args.convert();
            ReadOnlyObjectProperty<File> expected = viewModel.getCurrentFile();
            viewModel.setCurrentFile(getOtherSelectableFile(args));
            Assertions.assertSame(expected, viewModel.getCurrentFile());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> verifyOrCureFile_validInput(){
        return test("verifyOrCureFile() returns the expected file on valid input", args -> {
            VM viewModel = args.convert();
            assertVerifyOrCureFileOnValidInput(getOtherSelectableFile(args),
                    viewModel);
        });
    }
    
    protected abstract void assertVerifyOrCureFileOnValidInput(File file, VM viewModel);
    
    @TestFactory
    protected abstract Stream<DynamicNode> verifyOrCureFile_invalidInput();
    
    protected static record FileData(String defaultFilePath, File defaultFile,
        File defaultDirectory){}
    protected static final String PREFIX = "resources_test_mentoring_viewmodel_base"
            .replace('_', File.separatorChar);
    protected static final File PREFIX_FILE = new File(PREFIX);
    protected static final String FILE_NAME = "configurationPickerViewModelTestFile.txt";
    protected static final String FILE_PATH = PREFIX + File.separator + FILE_NAME;
    protected static final File FILE = new File(FILE_PATH);
    protected static final String OTHER_PREFIX = "%s%sconfigurationPickerViewModelTestDirectory"
            .formatted(PREFIX, File.separatorChar);
    protected static final String OTHER_FILE_NAME = "configurationPickerViewModelTestOtherFile.txt";
    protected static final String OTHER_FILE_PATH = OTHER_PREFIX + File.separator + OTHER_FILE_NAME;
    protected static final File OTHER_FILE = new File(OTHER_FILE_PATH);
    protected static final FileData DEFAULT_FILE_DATA = new FileData(FILE_PATH,
        FILE, PREFIX_FILE);
}
