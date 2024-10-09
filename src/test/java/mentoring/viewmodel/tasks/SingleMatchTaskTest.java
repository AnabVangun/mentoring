package mentoring.viewmodel.tasks;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import mentoring.match.MatchesBuilder;
import mentoring.match.MatchesBuilderHandler;
import mentoring.viewmodel.datastructure.MatchStatus;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonViewModel;
import mentoring.viewmodel.tasks.SingleMatchTaskTest.SingleMatchTaskArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class SingleMatchTaskTest implements TestFramework<SingleMatchTaskArgs>{

    @Override
    public Stream<SingleMatchTaskArgs> argumentsSupplier() {
        return Stream.of(
                new SingleMatchTaskArgs("standard match", SingleMatchTaskArgs.MENTEE,
                        SingleMatchTaskArgs.MENTOR, SingleMatchTaskArgs.PROGRESSIVE_COST),
                new SingleMatchTaskArgs("prohibited match", SingleMatchTaskArgs.PROHIBITED_MENTEE,
                        SingleMatchTaskArgs.MENTOR, SingleMatchTaskArgs.PROHIBITIVE_COST));
    }
    
    @TestFactory
    Stream<DynamicNode> makeSingleMatch_returnExpectedMatch(){
        return test("call() returns the expected match", args -> {
            SingleMatchTask task = args.convert();
            Match<Person,Person> expectedMatch = 
                    new MatchTest.MatchArgs("foo", args.mentee.getData(), args.mentor.getData(), 
                            args.expectedCost)
                    .convertAs(Person.class, Person.class);
            Assertions.assertEquals(expectedMatch, callTask(task));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> makeSingleMatch_updateViewModel(){
        return test("succeeded() updates the input view model", args -> {
            SingleMatchTask task = args.convert();
            Match<Person,Person> expectedMatch = callTask(task);
            task.succeeded();
            ArgumentCaptor<Match<Person, Person>> captor = captureAddedMatch(args.updatedVM);
            Match<Person, Person> actualMatch = captor.getValue();
            Assertions.assertEquals(expectedMatch, actualMatch);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> makeSingleMatch_updateMenteeViewModel(){
        return test("succeeded() updates the mentee view model", args -> {
            SingleMatchTask task = args.convert();
            callTask(task);
            task.succeeded();
            Assertions.assertTrue(args.menteeStatus.getStyleClass()
                    .contains(MatchStatus.MatchFlag.MANUAL_MATCH.getStyleClass()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> makeSingleMatch_updateMentorViewModel(){
        return test("succeeded() updates the mentee view model", args -> {
            SingleMatchTask task = args.convert();
            callTask(task);
            task.succeeded();
            Assertions.assertTrue(args.mentorStatus.getStyleClass()
                    .contains(MatchStatus.MatchFlag.MANUAL_MATCH.getStyleClass()));
        });
    }
    
    static Match<Person,Person> callTask(SingleMatchTask task){
        try {
            return task.call();
        } catch (Exception e){
            Assertions.fail(e);
            throw new IllegalStateException("unreachable code");
        }
    }
    static ArgumentCaptor<Match<Person, Person>> captureAddedMatch(PersonMatchesViewModel vm){
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Match<Person, Person>> captor = ArgumentCaptor.forClass(Match.class);
        Mockito.verify(vm).add(captor.capture());
        return captor;
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of(argumentsSupplier().iterator().next()), 
                "constructor throws NPE on null input", 
                args -> Assertions.assertAll(
                        assertConstructorThrowsNPE(null, args.builderHandler, args.mentee, 
                                args.mentor, args.callback),
                        assertConstructorThrowsNPE(args.updatedVM, null, args.mentee, 
                                args.mentor, args.callback),
                        assertConstructorThrowsNPE(args.updatedVM, args.builderHandler, null, 
                                args.mentor, args.callback),
                        assertConstructorThrowsNPE(args.updatedVM, args.builderHandler, args.mentee, 
                                null, args.callback),
                        assertConstructorThrowsNPE(args.updatedVM, args.builderHandler, args.mentee, 
                                args.mentor, null)));
    }
    
    static Executable assertConstructorThrowsNPE(PersonMatchesViewModel vm, 
            MatchesBuilderHandler<Person, Person> builderHandler, 
            PersonViewModel mentee, PersonViewModel mentor, 
            AbstractTask.TaskCompletionCallback<Object> callback){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new SingleMatchTask(vm, builderHandler, mentee, mentor, callback));
    }
    
    static class SingleMatchTaskArgs extends TestArgs {
        final PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
        @SuppressWarnings("unchecked")
        final MatchesBuilderHandler<Person, Person> builderHandler = 
                Mockito.mock(MatchesBuilderHandler.class);
        final PersonViewModel mentee;
        final MatchStatus menteeStatus = new MatchStatus();
        final PersonViewModel mentor;
        final MatchStatus mentorStatus = new MatchStatus();
        final int expectedCost;
        final AbstractTask.TaskCompletionCallback<Object> callback = task -> {};
        
        final static Person MENTEE = new PersonBuilder().withFullName("mentee").build();
        final static Person MENTOR = new PersonBuilder().withFullName("mentor").build();
        final static Person PROHIBITED_MENTEE = new PersonBuilder().withFullName("prohibited mentee")
                .withProperty("value", 0).build();
        final static int PROGRESSIVE_COST = 3;
        final static int PROHIBITIVE_COST = Integer.MAX_VALUE;
        
        SingleMatchTaskArgs(String testCase, Person mentee, Person mentor, int expectedCost){
            super(testCase);
            this.mentee = Mockito.mock(PersonViewModel.class);
            Mockito.when(this.mentee.getData()).thenReturn(mentee);
            Mockito.when(this.mentee.getStatus()).thenReturn(menteeStatus);
            this.mentor = Mockito.mock(PersonViewModel.class);
            Mockito.when(this.mentor.getData()).thenReturn(mentor);
            Mockito.when(this.mentor.getStatus()).thenReturn(mentorStatus);
            MatchesBuilder<Person, Person> builder = 
                    new MatchesBuilder<>(List.of(MENTEE, PROHIBITED_MENTEE),
                            List.of(MENTOR), List.of((x, y) -> PROGRESSIVE_COST));
            builder.withNecessaryCriteria(List.of((x,y) -> x != PROHIBITED_MENTEE));
            try {
                Mockito.when(builderHandler.get())
                        .thenReturn(builder);
            } catch (InterruptedException | ExecutionException e) {
                Assertions.fail("normally unreachable code", e);
            }
            this.expectedCost = expectedCost;
        }
        
        SingleMatchTask convert(){
            SingleMatchTask task = new SingleMatchTask(updatedVM, builderHandler, mentee, mentor, 
                    callback);
            return task;
        }
    }
}
