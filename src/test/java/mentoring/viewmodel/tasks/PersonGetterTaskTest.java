package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.FilePickerViewModel;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.tasks.PersonGetterTaskTest.PersonGetterTaskArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class PersonGetterTaskTest implements TestFramework<PersonGetterTaskArgs>{
    @Override
    public Stream<PersonGetterTaskArgs> argumentsSupplier(){
        PersonBuilder builder = new PersonBuilder();
        return Stream.of(new PersonGetterTaskArgs("unique test case", List.of(
                builder.withFullName("1").build(), builder.withFullName("2").build())));
    }
    
    @TestFactory
    Stream<DynamicNode> getPersons_correctParsing(){
        return test("call() returns the expected list of persons", args -> {
            PersonGetterTask getter = args.convert();
            List<Person> actualResults = null;
            try {
                actualResults = getter.call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            Assertions.assertEquals(args.expectedResults, actualResults);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getPersons_updateVM(){
        return test("call() correctly updates the viewmodel", args -> {
            PersonGetterTask getter = args.convert();
            List<Person> expectedList = null;
            PersonConfiguration expectedConfiguration = null;
            try {
                expectedList = getter.call();
                expectedConfiguration = args.configurationPicker.getConfiguration();
            } catch (Exception e){
                Assertions.fail(e);
            }
            getter.succeeded();
            Mockito.verify(args.updatedVM).update(expectedConfiguration, expectedList);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getPersons_NPE(){
        return test(Stream.of(new PersonGetterTaskArgs("unique test case", List.of())),
                "constructor throws an NPE on null input", 
                args -> Assertions.assertAll(
                        assertConstructorThrowsNPE(null, args.personPicker, args.configurationPicker),
                        assertConstructorThrowsNPE(args.updatedVM, null, args.configurationPicker),
                        assertConstructorThrowsNPE(args.updatedVM, args.personPicker, null)));
    }
    
    static Executable assertConstructorThrowsNPE(PersonListViewModel vm, 
            FilePickerViewModel<List<Person>> personPicker, 
            ConfigurationPickerViewModel<PersonConfiguration> configurationPicker){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new PersonGetterTask(vm, personPicker, configurationPicker));
    }
    
    static class PersonGetterTaskArgs extends TestArgs {
        final PersonListViewModel updatedVM = Mockito.mock(PersonListViewModel.class);
        final List<Person> expectedResults;
        final FilePickerViewModel<List<Person>> personPicker;
        final ConfigurationPickerViewModel<PersonConfiguration> configurationPicker;
        @SuppressWarnings("unchecked")
        PersonGetterTaskArgs(String testCase, List<Person> expectedResults){
            super(testCase);
            this.expectedResults = expectedResults;
            personPicker = new FilePickerViewModel<>("foo", file -> expectedResults, List.of());
            configurationPicker = Mockito.mock(ConfigurationPickerViewModel.class);
            try {
                Mockito.when(configurationPicker.getConfiguration())
                        .thenReturn(new PersonConfiguration("configuration", Set.of(), Set.of(), 
                                "", "", List.of()));
            } catch (IOException e){
                Assertions.fail("normally unreachable code", e);
            }
        }
        
        PersonGetterTask convert(){
            return new PersonGetterTask(updatedVM, personPicker, configurationPicker);
        }
    }
}
