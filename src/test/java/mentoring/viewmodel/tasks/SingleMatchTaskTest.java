package mentoring.viewmodel.tasks;

import java.util.List;
import java.util.stream.Stream;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
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
                        SingleMatchTaskArgs.MENTOR, Integer.MAX_VALUE));
    }
    
    @TestFactory
    Stream<DynamicNode> makeSingleMatch_returnExpectedMatch(){
        return test("call() returns the expected match", args -> {
            SingleMatchTask task = args.convert();
            Match<Person,Person> expectedMatch = 
                    new MatchTest.MatchArgs("foo", args.mentee, args.mentor, args.expectedCost)
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
                        assertConstructorThrowsNPE(null, args.configuration, args.mentee, args.mentor),
                        assertConstructorThrowsNPE(args.updatedVM, null, args.mentee, args.mentor),
                        assertConstructorThrowsNPE(args.updatedVM, args.configuration, null, args.mentor),
                        assertConstructorThrowsNPE(args.updatedVM, args.configuration, args.mentee, null)));
    }
    
    static Executable assertConstructorThrowsNPE(PersonMatchesViewModel vm, 
            RunConfiguration configuration, Person mentee, Person mentor){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new SingleMatchTask(vm, configuration, mentee, mentor));
    }
    
    static class SingleMatchTaskArgs extends TestArgs {
        final PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
        final RunConfiguration configuration = Mockito.mock(RunConfiguration.class);
        final Person mentee;
        final Person mentor;
        final int expectedCost;
        
        final static Person MENTEE = new PersonBuilder().withFullName("mentee").build();
        final static Person MENTOR = new PersonBuilder().withFullName("mentor").build();
        final static Person PROHIBITED_MENTEE = new PersonBuilder().withFullName("prohibited mentee")
                .withProperty("value", 0).build();
        final static int PROGRESSIVE_COST = 3;
        
        SingleMatchTaskArgs(String testCase, Person mentee, Person mentor, int expectedCost){
            super(testCase);
            this.mentee = mentee;
            this.mentor = mentor;
            @SuppressWarnings("unchecked")
            CriteriaConfiguration<Person, Person> criteriaConfiguration = 
                    Mockito.mock(CriteriaConfiguration.class);
            stubCriteriaConfiguration(criteriaConfiguration);
            Mockito.when(configuration.getCriteriaConfiguration())
                    .thenReturn(criteriaConfiguration);
            this.expectedCost = expectedCost;
        }
        
        SingleMatchTask convert(){
            SingleMatchTask task = new SingleMatchTask(updatedVM, configuration, mentee, mentor);
            return task;
        }
        
        static void stubCriteriaConfiguration(CriteriaConfiguration<Person, Person> result){
            Mockito.when(result.getProgressiveCriteria())
                    .thenReturn(List.of((mentee, mentor) -> PROGRESSIVE_COST));
            Mockito.when(result.getNecessaryCriteria())
                    .thenReturn(List.of((mentee, mentor) -> 
                            ! mentee.equals(SingleMatchTaskArgs.PROHIBITED_MENTEE)));
        }
    }
}
