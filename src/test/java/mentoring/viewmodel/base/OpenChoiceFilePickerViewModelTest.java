package mentoring.viewmodel.base;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import mentoring.viewmodel.base.OpenChoiceFilePickerViewModelTest.DummyFilePickerViewModel;
import mentoring.viewmodel.base.OpenChoiceFilePickerViewModelTest.OpenChoiceFilePickerViewModelArgs;
import mentoring.viewmodel.base.function.FileParser;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;

class OpenChoiceFilePickerViewModelTest extends FilePickerViewModelTest<String, 
        DummyFilePickerViewModel, OpenChoiceFilePickerViewModelArgs> {
    
    @Override
    public Stream<OpenChoiceFilePickerViewModelArgs> argumentsSupplier(){
        return Stream.of(new OpenChoiceFilePickerViewModelArgs("default file", DEFAULT_FILE_DATA, 
                List.of(Pair.of("foo", List.of("*.foo", "*.bar")))),
                new CopyFilePickerViewModelArgs("default file", DEFAULT_FILE_DATA, 
                List.of(Pair.of("foo", List.of("*.foo", "*.bar")))));
    }
    
    @Override
    protected void verifyDefaultBehavior(OpenChoiceFilePickerViewModelArgs args,
            DummyFilePickerViewModel viewModel){
        Assertions.assertEquals(args.defaultFileData.defaultFile(),
                viewModel.getCurrentFile().getValue());
    }
    
    @Override
    protected File getOtherSelectableFile(OpenChoiceFilePickerViewModelArgs args){
        return OTHER_FILE;
    }
    
    @Override
    protected void assertVerifyOrCureFileOnValidInput(File file, 
            DummyFilePickerViewModel viewModel){
        //No modification on valid file
        Assertions.assertEquals(file, viewModel.verifyOrCureFile(file));
    }
    
    @Override
    @TestFactory
    protected Stream<DynamicNode> verifyOrCureFile_invalidInput(){
        return test("verifyOrCureFile returns a default file on invalid input", args -> {
            DummyFilePickerViewModel vm = args.convert();
            File expected = Parameters.getDefaultDirectory();
            Assertions.assertEquals(expected, vm.verifyOrCureFile(new File("/&&/")));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFile_modifiableIndependentlyForCopy(){
        return test("getCurrentFile() is independent for a copied instance", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            DummyFilePickerViewModel copyViewModel = new DummyFilePickerViewModel(viewModel);
            ReadOnlyObjectProperty<File> observable = viewModel.getCurrentFile();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            copyViewModel.setCurrentFile(OTHER_FILE);
            Assertions.assertAll("The base view model should not be modified by its copy",
                    () -> Mockito.verify(listener, Mockito.never()).invalidated(observable),
                    () -> Assertions.assertEquals(args.defaultFileData.defaultFile(), 
                            observable.getValue()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFilePath_defaultValue(){
        return test("getCurrentFilePath() returns the default file path before any action", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.defaultFileData.defaultFilePath(), 
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
        return test("getCurrentFilePath() always return the same observable", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            ReadOnlyStringProperty expected = viewModel.getCurrentFilePath();
            viewModel.setCurrentFile(OTHER_FILE);
            Assertions.assertSame(expected, viewModel.getCurrentFilePath());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFilePath_modifiableIndependentlyForCopy(){
        return test("getCurrentFilePath() is independent for a copied instance", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            DummyFilePickerViewModel copyViewModel = new DummyFilePickerViewModel(viewModel);
            ReadOnlyStringProperty observable = viewModel.getCurrentFilePath();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            copyViewModel.setCurrentFile(OTHER_FILE);
            Assertions.assertAll("The base view model should not be modified by its copy",
                    () -> Mockito.verify(listener, Mockito.never()).invalidated(observable),
                    () -> Assertions.assertEquals(args.defaultFileData.defaultFilePath(), 
                            observable.getValue()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFileDirectory_defaultInstance(){
        return test("getCurrentFileDirectory() returns the default directory before any action", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            Assertions.assertEquals(args.defaultFileData.defaultDirectory(), 
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
        return test("getSeCurrentFileDirectory() always return the same observable", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            ReadOnlyObjectProperty<File> expected = viewModel.getCurrentFileDirectory();
            viewModel.setCurrentFile(OTHER_FILE);
            Assertions.assertSame(expected, viewModel.getCurrentFileDirectory());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getCurrentFileDirectory_modifiableIndependentlyForCopy(){
        return test("getCurrentFileDirectory() is independent for a copied instance", args -> {
            DummyFilePickerViewModel viewModel = args.convert();
            DummyFilePickerViewModel copyViewModel = new DummyFilePickerViewModel(viewModel);
            ReadOnlyObjectProperty<File> observable = viewModel.getCurrentFileDirectory();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            copyViewModel.setCurrentFile(OTHER_FILE);
            Assertions.assertAll("The base view model should not be modified by its copy",
                    () -> Mockito.verify(listener, Mockito.never()).invalidated(observable),
                    () -> Assertions.assertEquals(args.defaultFileData.defaultDirectory(), 
                            observable.getValue()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("unique test case"), "constructor throws NPE on null input", args ->
                Assertions.assertAll(
                        () -> Assertions.assertDoesNotThrow(
                                () -> new DummyFilePickerViewModel(null, input -> input.toString(), 
                                        List.of())),
                        () -> Assertions.assertThrows(NullPointerException.class, 
                                () -> new DummyFilePickerViewModel(FILE_PATH, null, List.of())),
                        () -> Assertions.assertThrows(NullPointerException.class,
                                () -> new DummyFilePickerViewModel(FILE_PATH, input -> input.toString(), 
                                        null)),
                        () -> Assertions.assertThrows(NullPointerException.class,
                                () -> new DummyFilePickerViewModel(null))));
    }
    
    static class DummyFilePickerViewModel extends OpenChoiceFilePickerViewModel<String>{
        DummyFilePickerViewModel(String defaultPath, FileParser<String> parser, 
                List<Pair<String, List<String>>> extensions){
            super(defaultPath, parser, extensions);
        }

        DummyFilePickerViewModel(DummyFilePickerViewModel toCopy) {
            super(toCopy);
        }
    }
    
    static class OpenChoiceFilePickerViewModelArgs 
            extends FilePickerViewModelArgs<String, DummyFilePickerViewModel> {
        final FileData defaultFileData;
        final List<Pair<String, List<String>>> expectedExtensions;
        
        OpenChoiceFilePickerViewModelArgs(String testCase, FileData defaultFileData, 
                List<Pair<String, List<String>>> extensions){
            super(testCase);
            this.defaultFileData = defaultFileData;
            this.expectedExtensions = extensions;
        }
        
        @Override
        protected DummyFilePickerViewModel convert(){
            return new DummyFilePickerViewModel(defaultFileData.defaultFilePath(), 
                    input -> input.getName(), expectedExtensions);
        }
    }
    
    static class CopyFilePickerViewModelArgs extends OpenChoiceFilePickerViewModelArgs {
        CopyFilePickerViewModelArgs(String testCase, FileData defaultFileData,
                List<Pair<String, List<String>>> extensions){
            super(testCase, defaultFileData, extensions);
        }
        
        @Override
        protected DummyFilePickerViewModel convert(){
            return new DummyFilePickerViewModel(super.convert());
        }
    }
}
