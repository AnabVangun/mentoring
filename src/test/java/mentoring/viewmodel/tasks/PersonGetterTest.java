package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.datastructure.PersonType;
import mentoring.viewmodel.tasks.PersonGetter.ReaderGenerator;
import mentoring.viewmodel.tasks.PersonGetterTest.PersonGetterArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;
import test.tools.TestArgs;
import test.tools.TestFramework;

class PersonGetterTest implements TestFramework<PersonGetterArgs>{
    @Override
    public Stream<PersonGetterArgs> argumentsSupplier(){
        RunConfiguration menteeConfiguration = Mockito.mock(RunConfiguration.class);
        PersonConfiguration menteePersonConfiguration = new PersonConfiguration("mentee configuration", 
                Set.of(), Set.of(), "bar", "%s", List.of("name"));
        try {
            Mockito.when(menteeConfiguration.getMenteeConfiguration())
                    .thenReturn(menteePersonConfiguration);
            Mockito.when(menteeConfiguration.getMentorConfiguration())
                    .thenThrow(new AssertionFailedError("called mentor method on mentee configuration"));
            Mockito.when(menteeConfiguration.getMentorFilePath())
                    .thenThrow(new AssertionFailedError("called mentor method on mentee configuration"));
        } catch (IOException e){
            Assertions.fail(e);
        }
        RunConfiguration mentorConfiguration = Mockito.mock(RunConfiguration.class);
        PersonConfiguration mentorPersonConfiguration = new PersonConfiguration("mentor configuration", 
                Set.of(), Set.of(), "bar", "%s", List.of("name"));
        try {
            Mockito.when(mentorConfiguration.getMentorConfiguration())
                    .thenReturn(mentorPersonConfiguration);
            Mockito.when(mentorConfiguration.getMenteeConfiguration())
                    .thenThrow(new AssertionFailedError("called mentee method on mentor configuration"));
            Mockito.when(mentorConfiguration.getMenteeFilePath())
                    .thenThrow(new AssertionFailedError("called mentee method on mentor configuration"));
        } catch (IOException e){
            Assertions.fail(e);
        }
        return Stream.of(
                new PersonGetterArgs("mentee configuration", PersonType.MENTEE, menteeConfiguration,
                        menteePersonConfiguration),
                new PersonGetterArgs("mentor configuration", PersonType.MENTOR, mentorConfiguration,
                        mentorPersonConfiguration));
    }
    
    @TestFactory
    Stream<DynamicNode> getPersons_correctParsing(){
        return test("call() returns the expected list of persons", args -> {
            PersonGetter getter = args.convert();
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
            PersonGetter getter = args.convert();
            List<Person> expectedList = null;
            try {
                expectedList = getter.call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            getter.succeeded();
            Mockito.verify(args.updatedVM).update(args.expectedConfiguration, expectedList);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getPersons_NPE(){
        PersonListViewModel vm = Mockito.mock(PersonListViewModel.class);
        RunConfiguration configuration = Mockito.mock(RunConfiguration.class);
        PersonType type = PersonType.MENTOR;
        ReaderGenerator supplier = Mockito.mock(ReaderGenerator.class);
        return test(Stream.of("unique test case"), "constructor throws an NPE on null input", args -> {
            Assertions.assertAll(assertConstructorThrowsNPE(null, configuration, type, supplier),
                    assertConstructorThrowsNPE(vm, null, type, supplier),
                    assertConstructorThrowsNPE(vm, configuration, null, supplier),
                    assertConstructorThrowsNPE(vm, configuration, type, null));
        });
    }
    
    static Executable assertConstructorThrowsNPE(PersonListViewModel vm, 
            RunConfiguration configuration, PersonType type, 
            ReaderGenerator supplier){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new PersonGetter(vm, configuration, type, supplier));
    }
    
    static class PersonGetterArgs extends TestArgs {
        final Reader reader;
        final ReaderGenerator generator;
        final PersonListViewModel updatedVM = Mockito.mock(PersonListViewModel.class);
        final PersonType type;
        final RunConfiguration configuration;
        final List<Person> expectedResults;
        final PersonConfiguration expectedConfiguration;
        PersonGetterArgs(String testCase, PersonType type, RunConfiguration configuration,
                PersonConfiguration expectedConfiguration){
            super(testCase);
            String input = """
                           name
                           1
                           2""";
            reader = new StringReader(input);
            generator = s -> reader;
            this.type = type;
            this.configuration = configuration;
            PersonBuilder builder = new PersonBuilder();
            expectedResults = List.of(builder.withFullName("1").build(), 
                    builder.withFullName("2").build());
            this.expectedConfiguration = expectedConfiguration;
        }
        
        PersonGetter convert(){
            return new PersonGetter(updatedVM, configuration, type, generator);
        }
    }
}
