package mentoring.viewmodel.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PojoCriteriaConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.match.MatchesBuilderHandler;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.tasks.MultipleMatchTaskTest.MultipleMatchTaskArgs;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class MultipleMatchTaskTest implements TestFramework<MultipleMatchTaskArgs>{

    @Override
    public Stream<MultipleMatchTaskArgs> argumentsSupplier() {
        return Stream.of(
                new MultipleMatchTaskArgs("with exclusion VM", true),
                new MultipleMatchTaskArgs("without exclusion VM", false));
    }
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_updateViewModel(){
        return test("call() updates the input view model", args -> {
            MultipleMatchTask task = args.convert();
            runTask(task);
            task.succeeded();
            ArgumentCaptor<Matches<Person, Person>> captor = captureArgumentsForSetAll(args.resultVM);
            Matches<Person,Person> expectedMatches = args.makeMatches(Stream.of(
                    Pair.of(0,2),Pair.of(1,1),Pair.of(2,0)));
            assertMatchesEquals(expectedMatches, captor.getValue());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_excludeManualMatches(){
        return test(argumentsSupplier().filter(args -> args.excludedMatchesVM != null), 
                "call() updates the input view model excluding the manual matches", args -> {
                    args.setManualMatch(args.mentees.get(0), args.mentors.get(1));
                    MultipleMatchTask task = args.convert();
                    runTask(task);
                    task.succeeded();
                    ArgumentCaptor<Matches<Person, Person>> captor = 
                            captureArgumentsForSetAll(args.resultVM);
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
    
    static ArgumentCaptor<Matches<Person, Person>> captureArgumentsForSetAll(PersonMatchesViewModel vm){
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
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of(new MultipleMatchTaskArgs("specific test case", true)), 
                "constructor throws an NPE on null input", args ->
                        Assertions.assertAll(
                                assertConstructorThrowsNPE("result VM", null, 
                                        args.excludedMatchesVM, args.handler,
                                        args.mentees, args.mentors, args.callback),
                                () -> Assertions.assertDoesNotThrow(() -> new MultipleMatchTask(args.resultVM,
                                        null, 
                                        args.handler,
                                        args.mentees, args.mentors, args.callback), "excluded matches VM"),
                                assertConstructorThrowsNPE("handler", args.resultVM, 
                                        args.excludedMatchesVM, 
                                        null, 
                                        args.mentees, args.mentors, args.callback),
                                assertConstructorThrowsNPE("mentees", args.resultVM, 
                                        args.excludedMatchesVM, 
                                        args.handler, 
                                        null, args.mentors, args.callback),
                                assertConstructorThrowsNPE("mentors", args.resultVM, 
                                        args.excludedMatchesVM, 
                                        args.handler, 
                                        args.mentees, null, args.callback),
                                assertConstructorThrowsNPE("callback", args.resultVM, 
                                        args.excludedMatchesVM, 
                                        args.handler, 
                                        args.mentees, args.mentors, null)));
    }
    
    Executable assertConstructorThrowsNPE(String label, PersonMatchesViewModel resultVM, 
            PersonMatchesViewModel excludedMatchesVM,
            MatchesBuilderHandler<Person, Person> handler,
            List<Person> mentees, 
            List<Person> mentors,
            AbstractTask.TaskCompletionCallback<? super Void> callback){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new MultipleMatchTask(resultVM, excludedMatchesVM, handler, 
                        mentees, mentors, callback), label);
    }
    
    static class MultipleMatchTaskArgs extends TestArgs{
        final PersonMatchesViewModel resultVM = Mockito.mock(PersonMatchesViewModel.class);
        final PersonMatchesViewModel excludedMatchesVM;
        final List<Person> mentees;
        final List<Person> mentors;
        @SuppressWarnings("unchecked")
        final MatchesBuilderHandler<Person, Person> handler = Mockito.mock(MatchesBuilderHandler.class);
        final AbstractTask.TaskCompletionCallback<Object> callback = task -> {};
        
        MultipleMatchTaskArgs(String testCase, boolean withExclusionVM){
            super(testCase);
            excludedMatchesVM = withExclusionVM ? Mockito.mock(PersonMatchesViewModel.class) : null;
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
            try {
                Mockito.when(handler.get()).thenReturn(new MatchesBuilder<>(mentees, mentors, 
                        criteria.getProgressiveCriteria())
                        .withNecessaryCriteria(criteria.getNecessaryCriteria()));
            } catch (InterruptedException | ExecutionException e){
                Assertions.fail("normally unreachable code", e);
            }
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
        
        MultipleMatchTask convert(){
            return new MultipleMatchTask(resultVM, excludedMatchesVM, handler, mentees, mentors, 
                    callback);
        }
        
        void setManualMatch(Person mentee, Person mentor){
            PersonMatchViewModel manualMatchVM = Mockito.mock(PersonMatchViewModel.class);
            Mockito.when(manualMatchVM.getData())
                    .thenReturn(new MatchTest.MatchArgs("", mentee, mentor, 0)
                            .convertAs(Person.class, Person.class));
            Mockito.when(excludedMatchesVM.getContent()).thenReturn(List.of(manualMatchVM));
        }
    }
}
