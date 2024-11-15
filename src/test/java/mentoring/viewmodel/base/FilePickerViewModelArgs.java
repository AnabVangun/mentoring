package mentoring.viewmodel.base;

import test.tools.TestArgs;

abstract class FilePickerViewModelArgs<T, VM extends FilePickerViewModel<T>> 
        extends TestArgs {
    
    protected FilePickerViewModelArgs(String testCase) {
        super(testCase);
    }

    protected abstract VM convert();
    
}
