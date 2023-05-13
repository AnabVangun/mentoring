package mentoring.viewmodel.match;

import java.util.List;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
import mentoring.viewmodel.match.MatchViewModelTest.MatchViewModelTestArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

class MatchViewModelTest implements TestFramework<MatchViewModelTestArgs>{
    
    @Override
    public Stream<MatchViewModelTestArgs> argumentsSupplier(){
        return Stream.of(new MatchViewModelTestArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor(){
        return test("constructor properly initialises the observable property", args -> {
            String[] expectedResult = new String[]{"first value", "second value"};
            Match<String, String> match = null;
            ResultConfiguration<String, String> configuration = new ResultConfiguration<>("name",
                    List.of("first", "second"), line -> expectedResult);
            MatchViewModel<String, String> viewModel = new MatchViewModel<>(configuration, match);
            Assertions.assertArrayEquals(expectedResult, viewModel.observableMatch().toArray());
        });
    }
    
    static record MatchViewModelTestArgs(String testCase) {
        @Override
        public String toString(){
            return this.testCase;
        }
    }
}
