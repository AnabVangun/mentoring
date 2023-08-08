package mentoring.viewmodel.base;

import java.util.stream.Stream;
import mentoring.viewmodel.base.SimpleObservableTest.SimpleObservablelTestArgs;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;


class SimpleObservableTest extends ObservableTest<SimpleObservable, 
        SimpleObservablelTestArgs>{
    
    @Override
    public Stream<SimpleObservablelTestArgs> argumentsSupplier(){
        return Stream.of(new SimpleObservablelTestArgs("unique test case"));
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
    
    static class SimpleObservablelTestArgs extends 
            ObservableArgs<SimpleObservable> {
        
        public SimpleObservablelTestArgs(String testCase) {
            super(testCase);
        }

        @Override
        protected SimpleObservable convert() {
            return new SimpleObservable() {};
        }

        @Override
        protected void invalidate(SimpleObservable viewModel) {
            viewModel.notifyListeners();
        }
    }
}
