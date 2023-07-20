package mentoring.viewmodel.base;

import javafx.beans.Observable;
import test.tools.TestArgs;

/**
 * Base class defining the arguments used to test subclasses of the {@link Observable} interface.
 * @param <VM> type of the observable class under test.
 */
public abstract class ObservableViewModelArgs<VM extends Observable> extends TestArgs{
    
    protected ObservableViewModelArgs(String testCase) {
        super(testCase);
    }
    
    protected abstract VM convert();
    
    protected abstract void invalidate(VM viewModel);
    
}
