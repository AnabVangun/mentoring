package mentoring.viewmodel.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PojoCriteriaConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import mentoring.match.Matches;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.tasks.MultipleMatchTaskTest.MultipleMatchTaskArgs;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class MultipleMatchTaskTest implements TestFramework<MultipleMatchTaskArgs>{

    @Override
    public Stream<MultipleMatchTaskArgs> argumentsSupplier() {
        return Stream.of(new MultipleMatchTaskArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_updateViewModel(){
        return test("call() updates the input view model", args -> {
            PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
            List<Person> mentees = args.mentees;
            List<Person> mentors = args.mentors;
            MultipleMatchTask task = new MultipleMatchTask(updatedVM, null, args.configuration, 
                    mentees, mentors);
            runTask(task);
            task.succeeded();
            ArgumentCaptor<Matches<Person, Person>> captor = captureSetAllArguments(updatedVM);
            Matches<Person,Person> expectedMatches = args.makeMatches(Stream.of(
                    Pair.of(0,2),Pair.of(1,1),Pair.of(2,0)));
            assertMatchesEquals(expectedMatches, captor.getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_excludeManualMatches(){
        return test("call() updates the input view model excluding the manual matches", args -> {
            PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
            List<Person> mentees = args.mentees;
            List<Person> mentors = args.mentors;
            PersonMatchesViewModel exclusionVM = forgeExclusionVM(mentees.get(0), mentors.get(1));
            MultipleMatchTask task = new MultipleMatchTask(updatedVM, exclusionVM, 
                    args.configuration, mentees, mentors);
            runTask(task);
            task.succeeded();
            ArgumentCaptor<Matches<Person, Person>> captor = captureSetAllArguments(updatedVM);
            Matches<Person,Person> expectedMatches = args.makeMatches(Stream.of(
                    Pair.of(1,2),Pair.of(2,0)));
            assertMatchesEquals(expectedMatches, captor.getValue());
        });
    }
    
    static void runTask(MultipleMatchTask task){
        try{
            task.call();
        } catch (Exception e){
            Assertions.fail(e);
        }
    }
    
    static ArgumentCaptor<Matches<Person, Person>> captureSetAllArguments(PersonMatchesViewModel vm){
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Matches<Person, Person>> captor = ArgumentCaptor.forClass(Matches.class);
        Mockito.verify(vm).setAll(captor.capture());
        return captor;
    }
    
    static void assertMatchesEquals(Matches<Person, Person> expected, Matches<Person, Person> actual){
        Iterator<Match<Person, Person>> expectedIterator = expected.iterator();
        for (Match<Person, Person> actualMatch : actual){
            Match<Person, Person> expectedMatch = expectedIterator.next();
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedMatch.getMentee().getFullName(), 
                            actualMatch.getMentee().getFullName()),
                    () -> Assertions.assertEquals(expectedMatch.getMentor().getFullName(),
                            actualMatch.getMentor().getFullName()));
        }
    }
    
    static PersonMatchesViewModel forgeExclusionVM(Person mentee, Person mentor){
        PersonMatchesViewModel result = Mockito.mock(PersonMatchesViewModel.class);
        PersonMatchViewModel manualMatchVM = Mockito.mock(PersonMatchViewModel.class);
        Mockito.when(manualMatchVM.getData())
                .thenReturn(new MatchTest.MatchArgs("", mentee, mentor, 0)
                        .convertAs(Person.class, Person.class));
        Mockito.when(result.getContent()).thenReturn(List.of(manualMatchVM));
        return result;
    }
    
    static class MultipleMatchTaskArgs extends TestArgs{
        final RunConfiguration configuration;
        final List<Person> mentees;
        final List<Person> mentors;
        
        MultipleMatchTaskArgs(String testCase){
            super(testCase);
            PersonBuilder builder = new PersonBuilder();
            mentees = List.of(buildIndexedPerson(1, builder),
                    buildIndexedPerson(2, builder),
                    buildIndexedPerson(3, builder));
            mentors = List.of(buildIndexedPerson(1, builder),
                    buildIndexedPerson(2, builder),
                    buildIndexedPerson(3, builder));
            CriteriaConfiguration<Person, Person> criteria = new PojoCriteriaConfiguration(
                    "ad-hoc configuration", List.of((mentee, mentor) -> 
                            mentee.getPropertyAs("value", Integer.class) 
                                    * mentor.getPropertyAs("value", Integer.class)), 
                    List.of());
            configuration = Mockito.spy(RunConfiguration.class);
            Mockito.when(configuration.getCriteriaConfiguration()).thenReturn(criteria);
        }
        
        private static Person buildIndexedPerson(int index, PersonBuilder builder){
            return builder.withFullName("" + index).withProperty("value", index).build();
        }
        
        Matches<Person, Person> makeMatches(Stream<Pair<Integer, Integer>> indices){
            List<Pair<? extends Person, ? extends Person>> personPairs = indices
                    .map(pair -> Pair.of(mentees.get(pair.getLeft()), mentors.get(pair.getRight())))
                    .collect(Collectors.toList());
            return (new MatchesTest.MatchesArgs<>(personPairs)).convert();
        }
    }
}
