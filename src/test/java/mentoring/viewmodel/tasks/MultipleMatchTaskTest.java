package mentoring.viewmodel.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.configuration.PojoCriteriaConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.match.MatchesBuilderHandler;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.datastructure.MatchStatus;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonViewModel;
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
                    args.setManualMatch(args.mentees.getUnderlyingData().get(0), 
                            args.mentors.getUnderlyingData().get(1));
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
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_updateMenteeMatchStatus(){
        return test("call() updates the mentees match status", args -> {
            MultipleMatchTask task = args.convert();
            runTask(task);
            task.succeeded();
            String expected = MatchStatus.MatchFlag.COMPUTED_MATCH.getStyleClass();
            Assertions.assertAll(
                    args.mentees.getContent().stream().map(vm -> 
                            () -> Assertions.assertTrue(
                                    vm.getStatus().getStyleClass().contains(expected),
                                    "%s should contain %s".formatted(vm.getData().getFullName(), 
                                            expected))));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_updateMentorMatchStatus(){
        return test("call() updates the mentors match status", args -> {
            MultipleMatchTask task = args.convert();
            runTask(task);
            task.succeeded();
            String expected = MatchStatus.MatchFlag.COMPUTED_MATCH.getStyleClass();
            Assertions.assertAll(
                    args.mentors.getContent().stream().map(vm -> 
                            () -> Assertions.assertTrue(
                                    vm.getStatus().getStyleClass().contains(expected),
                                    "%s should contain %s".formatted(vm.getData().getFullName(), 
                                            expected))));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_updateMatchStatus_includedMatches(){
        return test(argumentsSupplier().filter(args -> args.excludedMatchesVM != null), 
                "call() updates the match status excluding the manual matches", args -> {
                    args.setManualMatch(args.mentees.getUnderlyingData().get(0), 
                            args.mentors.getUnderlyingData().get(1));
                    MultipleMatchTask task = args.convert();
                    runTask(task);
                    task.succeeded();
                    String expected = MatchStatus.MatchFlag.COMPUTED_MATCH.getStyleClass();
                    List<PersonViewModel> mentees = List.of(args.mentees.getContent().get(1),
                            args.mentees.getContent().get(2));
                    List<PersonViewModel> mentors = List.of(args.mentors.getContent().get(0),
                            args.mentors.getContent().get(2));
                    Assertions.assertAll(
                            () -> Assertions.assertTrue(mentees.get(0)
                                    .getStatus().getStyleClass().contains(expected),
                                    "Mentee %s should contain %s".formatted(
                                            mentees.get(0).getData().getFullName(), expected)),
                            () -> Assertions.assertTrue(mentees.get(1)
                                    .getStatus().getStyleClass().contains(expected),
                                    "Mentee %s should contain %s".formatted(
                                            mentees.get(1).getData().getFullName(), expected)),
                            () -> Assertions.assertTrue(mentors.get(0)
                                    .getStatus().getStyleClass().contains(expected),
                                    "Mentor %s should contain %s".formatted(
                                            mentors.get(0).getData().getFullName(), expected)),
                            () -> Assertions.assertTrue(mentors.get(1)
                                    .getStatus().getStyleClass().contains(expected),
                                    "Mentor %s should contain %s".formatted(
                                            mentors.get(1).getData().getFullName(), expected)));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_updateMatchStatus_excludedMatches(){
        return test(argumentsSupplier().filter(args -> args.excludedMatchesVM != null), 
                "call() updates the match status of the excluded manual matches", args -> {
                    PersonViewModel mentee = args.mentees.getContent().get(0);
                    PersonViewModel mentor = args.mentors.getContent().get(1);
                    mentee.getStatus().add(MatchStatus.MatchFlag.COMPUTED_MATCH);
                    mentor.getStatus().add(MatchStatus.MatchFlag.COMPUTED_MATCH);
                    args.setManualMatch(mentee.getData(),mentor.getData());
                    MultipleMatchTask task = args.convert();
                    runTask(task);
                    task.succeeded();
                    String expected = MatchStatus.MatchFlag.COMPUTED_MATCH.getStyleClass();
                    Assertions.assertAll(
                            () -> Assertions.assertFalse(
                                    mentee.getStatus().getStyleClass().contains(expected),
                                    "Mentee %s should not contain %s".formatted(
                                            mentee.getData().getFullName(), expected)),
                            () -> Assertions.assertFalse(
                                    mentor.getStatus().getStyleClass().contains(expected),
                                    "Mentor %s should not contain %s".formatted(
                                            mentor.getData().getFullName(), expected)));
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
            PersonListViewModel mentees, 
            PersonListViewModel mentors,
            AbstractTask.TaskCompletionCallback<? super Void> callback){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new MultipleMatchTask(resultVM, excludedMatchesVM, handler, 
                        mentees, mentors, callback), label);
    }
    
    static class MultipleMatchTaskArgs extends TestArgs{
        final PersonMatchesViewModel resultVM = Mockito.mock(PersonMatchesViewModel.class);
        final PersonMatchesViewModel excludedMatchesVM;
        final PersonListViewModel mentees;
        final PersonListViewModel mentors;
        @SuppressWarnings("unchecked")
        final MatchesBuilderHandler<Person, Person> handler = Mockito.mock(MatchesBuilderHandler.class);
        final AbstractTask.TaskCompletionCallback<Object> callback = task -> {};
        
        MultipleMatchTaskArgs(String testCase, boolean withExclusionVM){
            super(testCase);
            excludedMatchesVM = withExclusionVM ? Mockito.mock(PersonMatchesViewModel.class) : null;
            PersonBuilder builder = new PersonBuilder();
            List<Person> menteesList = List.of(buildIndexedPerson(1, builder),
                    buildIndexedPerson(2, builder),
                    buildIndexedPerson(3, builder));
            List<Person> mentorsList = List.of(buildIndexedPerson(1, builder),
                    buildIndexedPerson(2, builder),
                    buildIndexedPerson(3, builder));
            PersonConfiguration configuration = new PersonConfiguration("configuration", Set.of(), 
                    Set.of(), "", "", List.of());
            mentees = new PersonListViewModel();
            mentees.update(configuration, menteesList);
            mentors = new PersonListViewModel();
            mentors.update(configuration, mentorsList);
            CriteriaConfiguration<Person, Person> criteria = new PojoCriteriaConfiguration(
                    "ad-hoc configuration", List.of((mentee, mentor) -> 
                            mentee.getPropertyAs("value", Integer.class) 
                                    * mentor.getPropertyAs("value", Integer.class)), 
                    List.of());
            try {
                Mockito.when(handler.get())
                        .thenReturn(new MatchesBuilder<>(mentees.getUnderlyingData(), 
                                mentors.getUnderlyingData(), criteria.getProgressiveCriteria())
                        .withNecessaryCriteria(criteria.getNecessaryCriteria()));
            } catch (InterruptedException | ExecutionException e){
                Assertions.fail("normally unreachable code", e);
            }
        }
        
        private static Person buildIndexedPerson(int index, PersonBuilder builder){
            return builder.withFullName(Integer.toString(index)).withProperty("value", index).build();
        }
        
        Matches<Person, Person> makeMatches(Stream<Pair<Integer, Integer>> indices){
            List<Pair<? extends Person, ? extends Person>> personPairs = indices
                    .map(pair -> Pair.of(mentees.getUnderlyingData().get(pair.getLeft()), 
                            mentors.getUnderlyingData().get(pair.getRight())))
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
