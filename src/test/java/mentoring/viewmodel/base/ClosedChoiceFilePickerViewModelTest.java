package mentoring.viewmodel.base;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import mentoring.viewmodel.base.ClosedChoiceFilePickerViewModelTest.ClosedChoiceFilePickerViewModelArgs;
import mentoring.viewmodel.base.ClosedChoiceFilePickerViewModelTest.DummyFilePickerViewModel;
import mentoring.viewmodel.base.function.FileParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class ClosedChoiceFilePickerViewModelTest extends 
        FilePickerViewModelTest<String, DummyFilePickerViewModel, 
        ClosedChoiceFilePickerViewModelArgs>{

    @Override
    public Stream<ClosedChoiceFilePickerViewModelArgs> argumentsSupplier() {
        return Stream.of(
                new ClosedChoiceFilePickerViewModelArgs("more than one element", 
                        List.of(FILE, OTHER_FILE)));
    }

    @Override
    protected void verifyDefaultBehavior(ClosedChoiceFilePickerViewModelArgs args, 
            DummyFilePickerViewModel viewModel) {
        Assertions.assertEquals(args.list.get(0), 
                viewModel.getCurrentFile().get());
    }

    @Override
    protected File getOtherSelectableFile(ClosedChoiceFilePickerViewModelArgs args) {
        return args.list.get(args.list.size() - 1);
    }

    @Override
    protected void assertVerifyOrCureFileOnValidInput(File file,
            DummyFilePickerViewModel viewModel) {
        //No modification on valid input
        Assertions.assertEquals(file, viewModel.verifyOrCureFile(file));
    }

    @Override
    @SuppressWarnings("ThrowableResultIgnored")
    protected Stream<DynamicNode> verifyOrCureFile_invalidInput() {
        return test("verifyOrCureFile() throws an exception on invalid input", args -> {
            DummyFilePickerViewModel vm = args.convert();
            File invalid = new File("foo");
            while (args.list.contains(invalid)){
                invalid = new File(invalid.getName() + "bar");
            }
            final File finalInvalid = invalid;
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> vm.verifyOrCureFile(finalInvalid));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("Unique test case"), "Constructor throws NPE on null input", args -> {
            Assertions.assertAll(
                    () -> Assertions.assertThrows(NullPointerException.class,
                            () -> new ClosedChoiceFilePickerViewModel<>(null, 
                                    input -> input.getName())),
                    () -> Assertions.assertThrows(NullPointerException.class,
                            () -> new ClosedChoiceFilePickerViewModel<>(List.of(new File("foo")),
                                    null)));
        });
    }
    
    static class DummyFilePickerViewModel extends ClosedChoiceFilePickerViewModel<String> {
        DummyFilePickerViewModel(List<File> list, FileParser<String> parser) {
            super(list, parser);
        }
    }
    
    static class ClosedChoiceFilePickerViewModelArgs
            extends FilePickerViewModelArgs<String, DummyFilePickerViewModel> {
        final List<File> list;
        ClosedChoiceFilePickerViewModelArgs(String testCase, List<File> list){
            super(testCase);
            this.list = list;
        }
        
        @Override
        protected DummyFilePickerViewModel convert(){
            return new DummyFilePickerViewModel(list, input -> input.getName());
        }
    }
    
}
