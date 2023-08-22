package mentoring.viewmodel.datastructure;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.stream.Stream;
import mentoring.viewmodel.datastructure.PersonViewModelModuleTest.PersonViewModelModuleTestArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class PersonViewModelModuleTest implements TestFramework<PersonViewModelModuleTestArgs> {

    @Override
    public Stream<PersonViewModelModuleTestArgs> argumentsSupplier() {
        return Stream.of(new PersonViewModelModuleTestArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> providePersonMatchesViewModel_notNull(){
        return test("providePersonMatchesViewModel() returns a non-null object", args -> {
                    Injector injector = Guice.createInjector(args.module);
                    Assertions.assertNotNull(injector.getInstance(PersonMatchesViewModel.class));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> providePersonMatchesViewModel_Singleton(){
        return test("providePersonMatchesViewModel() returns a different instance each time", args -> {
                    Injector injector = Guice.createInjector(args.module);
                    Assertions.assertNotSame(injector.getInstance(PersonMatchesViewModel.class),
                            injector.getInstance(PersonMatchesViewModel.class),
                            "injector should not return a singleton");
                });
    }
    
    static class PersonViewModelModuleTestArgs extends TestArgs {
        final PersonViewModelModule module = new PersonViewModelModule();
        
        PersonViewModelModuleTestArgs(String testCase){
            super(testCase);
        }
    }
}
