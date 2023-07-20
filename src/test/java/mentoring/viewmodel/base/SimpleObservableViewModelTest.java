package mentoring.viewmodel.base;

import java.util.stream.Stream;
import mentoring.viewmodel.base.SimpleObservableViewModelTest.SimpleObservableViewModelTestArgs;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;


class SimpleObservableViewModelTest extends ObservableViewModelTest<SimpleObservableViewModel, 
        SimpleObservableViewModelTestArgs>{
    
    @Override
    public Stream<SimpleObservableViewModelTestArgs> argumentsSupplier(){
        return Stream.of(new SimpleObservableViewModelTestArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> notifyListeners_EventFired(){
        return test("notifyListeners() notifies the listeners", 
                args -> assertInvalidatedEventFired(args, 
                        observable -> args.invalidate(observable)));
    }
    
    @TestFactory
    Stream<DynamicNode> noOp_noEventFired(){
        return test("no operation does not notify the listeners",
                args -> assertNoInvalidatedEventFired(args, observable -> {}));
    }
    
    static class SimpleObservableViewModelTestArgs extends 
            ObservableViewModelArgs<SimpleObservableViewModel> {
        
        public SimpleObservableViewModelTestArgs(String testCase) {
            super(testCase);
        }

        @Override
        protected SimpleObservableViewModel convert() {
            return new SimpleObservableViewModel() {};
        }

        @Override
        protected void invalidate(SimpleObservableViewModel viewModel) {
            viewModel.notifyListeners();
        }
    }
}
