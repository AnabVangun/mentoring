package mentoring.concurrency;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.stream.Stream;
import mentoring.concurrency.ConcurrencyModuleTest.ConcurrencyModuleTestArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ConcurrencyModuleTest implements TestFramework<ConcurrencyModuleTestArgs> {

    @Override
    public Stream<ConcurrencyModuleTestArgs> argumentsSupplier() {
        return Stream.of(new ConcurrencyModuleTestArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> provideConcurrencyHandler_notNull(){
        return test("provideConcurrencyHandler() returns a non-null object", args -> {
                    Injector injector = Guice.createInjector(args.module);
                    Assertions.assertNotNull(injector.getInstance(ConcurrencyHandler.class));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> provideConcurrencyHandler_Singleton(){
        return test("provideConcurrencyHandler() returns a singleton", args -> {
                    Injector injector = Guice.createInjector(args.module);
                    Assertions.assertSame(injector.getInstance(ConcurrencyHandler.class),
                            injector.getInstance(ConcurrencyHandler.class),
                            "injector should return a singleton");
                });
    }
    
    static class ConcurrencyModuleTestArgs extends TestArgs {
        final ConcurrencyModule module = new ConcurrencyModule();
        
        ConcurrencyModuleTestArgs(String testCase){
            super(testCase);
        }
    }
}
