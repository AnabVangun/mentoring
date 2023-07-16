package mentoring.viewmodel.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonType;
import mentoring.viewmodel.tasks.MultipleMatchTaskTest.MultipleMatchTaskArgs;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestFramework;

class MultipleMatchTaskTest implements TestFramework<MultipleMatchTaskArgs>{

    @Override
    public Stream<MultipleMatchTaskArgs> argumentsSupplier() {
        return Stream.of(new MultipleMatchTaskArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_updateViewModel(){
        //TODO refactor test: move stuff to MultipleMatchTaskArgs
        return test("call() updates the input view model", args -> {
            PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
            RunConfiguration config = RunConfiguration.TEST;
            List<Person> mentees = null;
            List<Person> mentors = null;
            try{
                mentees = new PersonGetter(null, config, PersonType.MENTEE)
                    .call();
                mentors = new PersonGetter(null, config, PersonType.MENTOR)
                    .call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            MultipleMatchTask task = new MultipleMatchTask(updatedVM, config, mentees, mentors);
            try {
                task.call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            task.succeeded();
            @SuppressWarnings("unchecked")
            ArgumentCaptor<Matches<Person, Person>> captor = ArgumentCaptor.forClass(Matches.class);
            Mockito.verify(updatedVM).update(
                    Mockito.eq(PojoResultConfiguration.NAMES_AND_SCORE.getConfiguration()), 
                    captor.capture());
            PersonBuilder builder = new PersonBuilder();
            Iterator<Match<Person,Person>> expectedIterator = new MatchesTest.MatchesArgs<>(
                    List.of(
                            Pair.of(builder.withFullName("Marceau Moussa (X2020)").build(), 
                                    builder.withFullName("Gaspard Marion (X2000)").build()),
                            Pair.of(builder.withFullName("Leon Arthur (X2020)").build(),
                                    builder.withFullName("Sandro Keelian (X1975)").build()),
                            Pair.of(builder.withFullName("Rafael Pablo (X2020)").build(),
                                    builder.withFullName("Lhya Elias (X1999)").build()),
                            Pair.of(builder.withFullName("Laula Anthonin (X2020)").build(),
                                    builder.withFullName("Hyacine Maddi (X2000)").build()),
                            Pair.of(builder.withFullName("Paul Pierre (X2020)").build(),
                                    builder.withFullName("Elsa Margaux (X1999)").build()))).convert()
                    .iterator();
            for (Match<Person, Person> actualMatch : captor.getValue()){
                Match<Person, Person> expectedMatch = expectedIterator.next();
                Assertions.assertAll(
                        () -> Assertions.assertEquals(expectedMatch.getMentee().getFullName(), 
                                actualMatch.getMentee().getFullName()),
                        () -> Assertions.assertEquals(expectedMatch.getMentor().getFullName(),
                                actualMatch.getMentor().getFullName()));
            }
        });
    }
    
    @TestFactory
    Stream<DynamicNode> makeMultipleMatches_excludeManualMatches(){
        //TODO refactor test: move stuff to MultipleMatchTaskArgs
        return test("call() updates the input view model excluding the manual matches", args -> {
            PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
            RunConfiguration config = RunConfiguration.TEST;
            List<Person> mentees = null;
            List<Person> mentors = null;
            try{
                mentees = new PersonGetter(null, config, PersonType.MENTEE)
                    .call();
                mentors = new PersonGetter(null, config, PersonType.MENTOR)
                    .call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            PersonMatchViewModel firstManualMatchVM = Mockito.mock(PersonMatchViewModel.class);
            //TODO: this should be grossly simplified
            Mockito.when(firstManualMatchVM.getData()).thenReturn(new MatchesTest.MatchesArgs<>(
                    List.of(Pair.of(mentees.get(0), mentors.get(1)))).convert().iterator().next());
            PersonMatchViewModel secondManualMatchVM = Mockito.mock(PersonMatchViewModel.class);
            Mockito.when(secondManualMatchVM.getData()).thenReturn(new MatchesTest.MatchesArgs<>(
                    List.of(Pair.of(mentees.get(1), mentors.get(0)))).convert().iterator().next());
            Mockito.when(updatedVM.getTransferredItems()).thenReturn(List.of(firstManualMatchVM, 
                    secondManualMatchVM));
            MultipleMatchTask task = new MultipleMatchTask(updatedVM, config, mentees, mentors);
            try {
                task.call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            task.succeeded();
            @SuppressWarnings("unchecked")
            ArgumentCaptor<Matches<Person, Person>> captor = ArgumentCaptor.forClass(Matches.class);
            Mockito.verify(updatedVM).update(
                    Mockito.eq(PojoResultConfiguration.NAMES_AND_SCORE.getConfiguration()), 
                    captor.capture());
            PersonBuilder builder = new PersonBuilder();
            //FIXME: Sandro and Lhya should be switched if Anglais was correctly read but it is false for each Person despite the input file. Investigate!
            Iterator<Match<Person,Person>> expectedIterator = new MatchesTest.MatchesArgs<>(
                    List.of(
                            Pair.of(builder.withFullName("Rafael Pablo (X2020)").build(),
                                    builder.withFullName("Sandro Keelian (X1975)").build()),
                            Pair.of(builder.withFullName("Laula Anthonin (X2020)").build(),
                                    builder.withFullName("Lhya Elias (X1999)").build()),
                            Pair.of(builder.withFullName("Paul Pierre (X2020)").build(),
                                    builder.withFullName("Elsa Margaux (X1999)").build()))).convert()
                    .iterator();
            Matches<Person, Person> actualMatches = captor.getValue();
            for (Match<Person, Person> actualMatch : actualMatches){
                Match<Person, Person> expectedMatch = expectedIterator.next();
                Assertions.assertAll(
                        () -> Assertions.assertEquals(expectedMatch.getMentee().getFullName(), 
                                actualMatch.getMentee().getFullName()),
                        () -> Assertions.assertEquals(expectedMatch.getMentor().getFullName(),
                                actualMatch.getMentor().getFullName()));
            }
        });
    }
    
    static record MultipleMatchTaskArgs(String testCase){
        
        @Override 
        public String toString(){
            return testCase;
        }
    }
}
