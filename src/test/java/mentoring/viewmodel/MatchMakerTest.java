package mentoring.viewmodel;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.MatchMakerTest.MatchMakerArgs;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestFramework;

class MatchMakerTest implements TestFramework<MatchMakerArgs>{

    @Override
    public Stream<MatchMakerArgs> argumentsSupplier() {
        return Stream.of(new MatchMakerArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> makeMatches_updateViewModel(){
        return test("call() updates the input view model", args -> {
            PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
            MatchMaker task = new MatchMaker(updatedVM);
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
    
    static record MatchMakerArgs(String testCase){
        
        @Override 
        public String toString(){
            return testCase;
        }
    }
}
