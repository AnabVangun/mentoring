package mentoring.viewmodel.base;

import java.util.function.Consumer;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

/**
 * Base class providing tests and utilities related to the {@link Observable} interface.
 * @param <VM> type of the observable class under test.
 * @param <A> type of the test argument class used to generate and manipulate the class under test.
 */
public abstract class ObservableTest<VM extends Observable, 
        A extends ObservableArgs<VM>> implements TestFramework<A>{
    
    /**
     * Assert that a given action notifies the registered listeners.
     * @param args the test configuration
     * @param vmSetUp actions to perform on the view model before registering the listeners
     * @param invalidationLauncher the action under test: it should notify the listeners
     */
    protected final void assertInvalidatedEventFired(A args, Consumer<VM> vmSetUp, 
            Consumer<VM> invalidationLauncher){
        VM viewModel = args.convert();
        vmSetUp.accept(viewModel);
        Observable[] notified = configureListeners(viewModel);
        invalidationLauncher.accept(viewModel);
        Assertions.assertAll(
                () -> Assertions.assertSame(viewModel, notified[0]),
                () -> Assertions.assertSame(viewModel, notified[1]));
    }
    
    /**
     * Assert that a given action notifies the registered listeners.
     * @param args the test configuration
     * @param invalidationLauncher the action under test: it should notify the listeners
     */
    protected final void assertInvalidatedEventFired(A args, Consumer<VM> invalidationLauncher){
        assertInvalidatedEventFired(args, this::noOperation, invalidationLauncher);
    }
    
    /**
     * Assert that a given action does not notify the registered listeners.
     * @param args the test configuration
     * @param vmSetUp actions to perform on the view model before registering the listeners
     * @param invalidationLauncher the action under test: it should not notify the listeners.
     */
    protected final void assertNoInvalidatedEventFired(A args, Consumer<VM> vmSetUp,
            Consumer<VM> invalidationLauncher){
        VM viewModel = args.convert();
        vmSetUp.accept(viewModel);
        Observable[] notified = configureListeners(viewModel);
        invalidationLauncher.accept(viewModel);
        Assertions.assertAll(
                () -> Assertions.assertNull(notified[0]),
                () -> Assertions.assertNull(notified[1]));
    }
    
    /**
     * Assert that a given action does not notify the registered listeners.
     * @param args the test configuration
     * @param invalidationLauncher the action under test: it should not notify the listeners.
     */
    protected final void assertNoInvalidatedEventFired(A args, Consumer<VM> invalidationLauncher){
        assertNoInvalidatedEventFired(args, this::noOperation, invalidationLauncher);
    }
    
    //This method does nothing and is a simple short-hand.
    private void noOperation(VM viewModel){};
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> addListener_NPE(){
        return test("addListener() throws an NPE when adding a null object", args -> {
            VM viewModel = args.convert();
            Assertions.assertThrows(NullPointerException.class, () -> viewModel.addListener(null));
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> removeListener_NPE(){
        return test("removeListener() throws an NPE when adding a null object", args -> {
            VM viewModel = args.convert();
            Assertions.assertThrows(NullPointerException.class, () -> viewModel.removeListener(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeListener_nominal(){
        return test("removeListener() removes exactly the input listener", args -> {
            Observable[] notified = new Observable[2];
            VM viewModel = args.convert();
            InvalidationListener removed = observable -> notified[0] = observable;
            viewModel.addListener(removed);
            viewModel.addListener(observable -> notified[1] = observable);
            viewModel.removeListener(removed);
            args.invalidate(viewModel);
            Assertions.assertAll(
                    () -> Assertions.assertNull(notified[0]),
                    () -> Assertions.assertSame(viewModel, notified[1]));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeListener_notPreviouslyRegistered(){
        return test("removeListener() does not cause issue when removing an absent listener", args -> {
        VM viewModel = args.convert();
        Observable[] notified = configureListeners(viewModel);
            Assertions.assertAll(
                    () -> Assertions.assertDoesNotThrow(
                            () -> viewModel.removeListener(observable -> {
                                throw new RuntimeException("listener should not be called");
                            })),
                    () -> Assertions.assertDoesNotThrow(() -> args.invalidate(viewModel), 
                            "Removed listener should not be added by accident"),
                    () -> Assertions.assertSame(viewModel, notified[0]),
                    () -> Assertions.assertSame(viewModel, notified[1])
            );
        });
    }
    
    private static Observable[] configureListeners(Observable toObserve){
        Observable[] result = new Observable[2];
        toObserve.addListener(observable -> result[0] = observable);
        toObserve.addListener(observable -> result[1] = observable);
        return result;
    }
}
