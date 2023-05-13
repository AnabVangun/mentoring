package mentoring.viewmodel;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.MainViewModelTest.MainViewModelArgs;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestFramework;

class MainViewModelTest implements TestFramework<MainViewModelArgs>{

    @Override
    public Stream<MainViewModelArgs> argumentsSupplier() {
        return Stream.of(new MainViewModelArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> makeMatches_updateStatus(){
        //TODO refactor and simplify tests
        return test("makeMatches() updates the view model properties", args -> {
            ConcurrencyHandler executor = new ConcurrencyHandler();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
            MainViewModel vm = new MainViewModel(executor);
            vm.status.addListener(listener);
            Future<?> taskStatus = vm.makeMatches(updatedVM);
            try{
                taskStatus.get(500, TimeUnit.MILLISECONDS);
            } catch (ExecutionException|InterruptedException|TimeoutException e){
                Assertions.fail(e);
            }
            Mockito.verify(listener, Mockito.atLeast(2)).invalidated(Mockito.any());
            Assertions.assertTrue(taskStatus.isDone());
            Assertions.assertEquals("""
                                    "Mentoré","Mentor","Coût"
                                    "Marceau Moussa (X2020)","Gaspard Marion (X2000)","383"
                                    "Leon Arthur (X2020)","Sandro Keelian (X1975)","700"
                                    "Rafael Pablo (X2020)","Lhya Elias (X1999)","410"
                                    "Laula Anthonin (X2020)","Hyacine Maddi (X2000)","800"
                                    "Paul Pierre (X2020)","Elsa Margaux (X1999)","210"
                                    """,
                    vm.status.get());
            executor.shutdown(0);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> makeMatches_updateViewModel(){
        return test("makeMatches() updates the input view model", args -> {
            ConcurrencyHandler executor = new ConcurrencyHandler();
            PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
            MainViewModel vm = new MainViewModel(executor);
            Future<?> taskStatus = vm.makeMatches(updatedVM);
            try{
                taskStatus.get(500, TimeUnit.MILLISECONDS);
            } catch (ExecutionException|InterruptedException|TimeoutException e){
                Assertions.fail(e);
            }
            Assertions.assertTrue(taskStatus.isDone());
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
            executor.shutdown(0);
        });
    }
    
    static record MainViewModelArgs(String testCase){
        
        @Override 
        public String toString(){
            return testCase;
        }
    }
}
