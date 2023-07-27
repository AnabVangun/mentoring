package mentoring.viewmodel.tasks;

import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Stream;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.PojoRunConfiguration;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonType;
import mentoring.viewmodel.tasks.SingleMatchTaskTest.SingleMatchTaskArgs;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import test.tools.TestFramework;

class SingleMatchTaskTest implements TestFramework<SingleMatchTaskArgs>{

    @Override
    public Stream<SingleMatchTaskArgs> argumentsSupplier() {
        return Stream.of(new SingleMatchTaskArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> makeSingleMatche_updateViewModel(){
        //TODO refactor test: move stuff to MatchMakerArgs
        return test("call() updates the input view model", args -> {
            PersonMatchesViewModel updatedVM = Mockito.mock(PersonMatchesViewModel.class);
            RunConfiguration config = PojoRunConfiguration.TEST;
            Person mentee = null;
            Person mentor = null;
            try{
                mentee = new PersonGetter(Mockito.mock(PersonListViewModel.class), 
                        config, PersonType.MENTEE, 
                        input -> new FileReader(input, Charset.forName("utf-8")))
                    .call().get(0);
                mentor = new PersonGetter(Mockito.mock(PersonListViewModel.class), 
                        config, PersonType.MENTOR, 
                        input -> new FileReader(input, Charset.forName("utf-8")))
                    .call().get(0);
            } catch (Exception e){
                Assertions.fail(e);
            }
            SingleMatchTask task = new SingleMatchTask(updatedVM, config, mentee, mentor);
            try {
                task.call();
            } catch (Exception e){
                Assertions.fail(e);
            }
            task.succeeded();
            @SuppressWarnings("unchecked")
            ArgumentCaptor<Match<Person, Person>> captor = ArgumentCaptor.forClass(Match.class);
            Mockito.verify(updatedVM).add(captor.capture());
            PersonBuilder builder = new PersonBuilder();
            //TODO simplify Match creation
            Match<Person,Person> expectedMatch = new MatchesTest.MatchesArgs<>(
                    List.of(
                            Pair.of(builder.withFullName("Marceau Moussa (X2020)").build(), 
                                    builder.withFullName("Gaspard Marion (X2000)").build()))
                            ).convert().iterator().next();
            Match<Person, Person> actualMatch = captor.getValue();
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedMatch.getMentee().getFullName(), 
                            actualMatch.getMentee().getFullName()),
                    () -> Assertions.assertEquals(expectedMatch.getMentor().getFullName(),
                            actualMatch.getMentor().getFullName()));
        });
    }
    
    static record SingleMatchTaskArgs(String testCase) {
        
        @Override 
        public String toString(){
            return testCase;
        }
    }
}
