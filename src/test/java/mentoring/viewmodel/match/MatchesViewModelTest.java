package mentoring.viewmodel.match;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.match.MatchesViewModelTest.MatchesViewModelTestArgs;
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
    Stream<DynamicNode> constructor(){
        return test("constructor initiales a not-ready-yet object", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = args.convert();
            Assertions.assertFalse(viewModel.readyProperty().get());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_ready(){
        return test("update() marks the object ready", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            Assertions.assertTrue(viewModel.readyProperty().get());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_header(){
        return test("update() properly sets the header", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            List<String> actualHeaders = viewModel.headerContentProperty().stream()
                    .map(tableColumn -> tableColumn.getText()).collect(Collectors.toList());
            Assertions.assertEquals(args.expectedHeader, actualHeaders);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_header_repeated(){
        return test("repeated calls to update() properly set the header", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            List<String> expectedHeader = List.of("unique");
            ResultConfiguration<String, String> newConf = new ResultConfiguration<>("name", 
                    expectedHeader, match -> new String[]{match.getMentee()});
            viewModel.update(newConf, 
                    new MatchesTest.MatchesArgs<>(List.of(Pair.of("foo", "bar"))).convert());
            List<String> actualHeaders = viewModel.headerContentProperty().stream()
                    .map(tableColumn -> tableColumn.getText()).collect(Collectors.toList());
            Assertions.assertEquals(expectedHeader, actualHeaders);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_content(){
        return test("update() properly sets the content", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            List<List<String>> actualContent = viewModel.itemsProperty().stream()
                    .map(matchVM -> matchVM.observableMatch())
                    .collect(Collectors.toList());
            Assertions.assertEquals(args.expectedContent, actualContent);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_content_repeated(){
        return test("repeated calls to update() properly set the content", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            List<List<String>> expectedContent = List.of(List.of("foo"));
            ResultConfiguration<String, String> newConf = new ResultConfiguration<>("name", 
                    List.of("unique"), match -> new String[]{match.getMentee()});
            viewModel.update(newConf, 
                    new MatchesTest.MatchesArgs<>(List.of(Pair.of("foo", "bar"))).convert());
            List<List<String>> actualContent = viewModel.itemsProperty().stream()
                    .map(matchVM -> matchVM.observableMatch())
                    .collect(Collectors.toList());
            Assertions.assertEquals(expectedContent, actualContent);
        });
    }
    
    static record MatchesViewModelTestArgs(String testCase, List<String> expectedHeader,
            List<Pair<? extends String, ? extends String>> input, List<List<String>> expectedContent) {
        
        @Override
        public String toString(){
            return testCase;
        }
        
        MatchesViewModel<String, String, MatchViewModel<String, String>> convert(){
            return new MatchesViewModel<>(MatchViewModel::new);
        }
        
       MatchesViewModel<String, String, MatchViewModel<String, String>> convertAndUpdate(){
            MatchesViewModel<String, String, MatchViewModel<String, String>> vm = convert();
            vm.update(new ResultConfiguration<>("name", expectedHeader,
                            match -> new String[]{match.getMentee(), match.getMentor()}),
                    new MatchesTest.MatchesArgs<>(input).convert());
            return vm;
        }
    }
}
