package mentoring.viewmodel.match;

import java.util.List;
import java.util.Map;
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
            Map<String, String> expectedResult = 
                    Map.of("first", "first value", "second", "second value");
            Match<String, String> match = null;
            /* TODO once ResultConfiguration allow creation with Map, use expectedResult back
            ResultConfiguration<String, String> configuration = ResultConfiguration.create("name",
                    List.of("first", "second"), line -> expectedResult);
            */
            ResultConfiguration<String, String> configuration = ResultConfiguration.create("name",
                    List.of("first", "second"), line -> new String[]{"first value", "second value"});
            MatchViewModel<String, String> viewModel = new MatchViewModel<>(configuration, match);
            Assertions.assertEquals(expectedResult, viewModel.observableMatch());
        });
    }
    
    static record MatchViewModelTestArgs(String testCase) {
        @Override
        public String toString(){
            return this.testCase;
        }
    }
}
