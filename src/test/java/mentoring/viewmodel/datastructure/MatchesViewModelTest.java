package mentoring.viewmodel.datastructure;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.datastructure.MatchesViewModelTest.MatchesViewModelTestArgs;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

class MatchesViewModelTest implements TestFramework<MatchesViewModelTestArgs>{
    
    @Override
    public Stream<MatchesViewModelTestArgs> argumentsSupplier(){
        return Stream.of(new MatchesViewModelTestArgs("unique test case", 
                List.of("first", "second"), 
                List.of(Pair.of("first mentee", "first mentor"),
                            Pair.of("second mentee", "second mentor")),
                List.of(List.of("first mentee", "first mentor"), 
                        List.of("second mentee", "second mentor"))));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_header(){
        return test("constructor properly initialises the header", args -> {
            MatchesViewModel<String, String> viewModel = args.convert();
            List<String> actualHeaders = viewModel.headerContent.stream()
                    .map(tableColumn -> tableColumn.getText()).collect(Collectors.toList());
            Assertions.assertEquals(args.expectedHeader, actualHeaders);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_content(){
        return test("constructor properly initialises the content", args -> {
            MatchesViewModel<String, String> viewModel = args.convert();
            List<List<String>> actualContent = viewModel.items.stream()
                    .map(matchVM -> matchVM.line)
                    .collect(Collectors.toList());
            Assertions.assertEquals(args.expectedContent, actualContent);
        });
    }
    
    static record MatchesViewModelTestArgs(String testCase, List<String> expectedHeader,
            List<Pair<? extends String, ? extends String>> input, List<List<String>> expectedContent) {
        
        @Override
        public String toString(){
            return testCase;
        }
        
        MatchesViewModel<String, String> convert(){
            return new MatchesViewModel<>(
                    new ResultConfiguration<>("name", expectedHeader,
                            match -> new String[]{match.getMentee(), match.getMentor()}),
                    new MatchesTest.MatchesArgs<>(input).convert());
        }
    }
}
