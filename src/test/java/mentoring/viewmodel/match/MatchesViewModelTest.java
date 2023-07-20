package mentoring.viewmodel.match;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.base.ObservableViewModelTest;
import mentoring.viewmodel.base.ObservableViewModelArgs;
import mentoring.viewmodel.match.MatchesViewModelTest.MatchesViewModelTestArgs;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class MatchesViewModelTest extends ObservableViewModelTest<
        MatchesViewModel<String, String, MatchViewModel<String, String>>, MatchesViewModelTestArgs>{
    
    @Override
    public Stream<MatchesViewModelTestArgs> argumentsSupplier(){
        return Stream.of(new MatchesViewModelTestArgs("unique test case", 
                List.of("first", "second"), 
                List.of(Pair.of("first mentee", "first mentor"),
                            Pair.of("second mentee", "second mentor")),
                List.of(Map.of("first", "first mentee", "second", "first mentor"), 
                        Map.of("first", "second mentee", "second", "second mentor"))));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor(){
        return test("constructor initiales a not-ready-yet object", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = args.convert();
            Assertions.assertFalse(viewModel.isValid());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_ready(){
        return test("update() marks the object ready", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            Assertions.assertTrue(viewModel.isValid());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_header(){
        return test("update() properly sets the header", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            Assertions.assertEquals(args.expectedHeader, viewModel.getHeaders());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_header_repeated(){
        return test("repeated calls to update() properly set the header", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            List<String> expectedHeader = List.of("unique");
            ResultConfiguration<String, String> newConf = ResultConfiguration.create("name", 
                    expectedHeader, match -> new String[]{match.getMentee()});
            viewModel.update(newConf, 
                    new MatchesTest.MatchesArgs<>(List.of(Pair.of("foo", "bar"))).convert());
            Assertions.assertEquals(expectedHeader, viewModel.getHeaders());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_content(){
        return test("update() properly sets the content", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            List<Map<String, String>> actualContent = viewModel.getContent().stream()
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
            List<Map<String, String>> expectedContent = List.of(Map.of("unique", "foo"));
            ResultConfiguration<String, String> newConf = ResultConfiguration.create("name", 
                    List.of("unique"), match -> new String[]{match.getMentee()});
            viewModel.update(newConf, 
                    new MatchesTest.MatchesArgs<>(List.of(Pair.of("foo", "bar"))).convert());
            assertContentAsExpected(expectedContent, viewModel.getContent());
        });
    }
    
    private static void assertContentAsExpected(List<Map<String, String>> expectedContent,
            List<MatchViewModel<String, String>> actualContent){
        List<Map<String, String>> formattedContent = actualContent.stream()
                    .map(matchVM -> matchVM.observableMatch())
                    .collect(Collectors.toList());
            Assertions.assertEquals(expectedContent, formattedContent);
    }
    
    @TestFactory
    Stream<DynamicNode> update_invalidatedEvent(){
        return test("update() fires an invalidated event to all registered listeners", 
                args -> assertInvalidatedEventFired(args, vm -> args.invalidate(vm)));
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> addManualItem_NPE(){
        return test("addManualItem() throws an NPE when adding a null object", args -> {
           MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                   args.convert();
           Assertions.assertThrows(NullPointerException.class, () -> viewModel.addManualItem(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> addManualItem_addToTransferred(){
        return test("addManualItem() adds the item to the transferred items", args -> {
            /*FIXME: this test only verifies that items already present in the batch
            items can be added to the manual ones. It should mostly verify that :
            1. a match between two persons not in the batch items works;
            2. a match between two persons in the batch items (but not necessarily together) works.
            */
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndUpdate();
            List<Match<String, String>> expectedContent = 
                    List.of(viewModel.getContent().get(1).getData(), 
                            viewModel.getContent().get(0).getData());
            viewModel.addManualItem(viewModel.getContent().get(1).getData());
            viewModel.addManualItem(viewModel.getContent().get(0).getData());
            Assertions.assertEquals(expectedContent, 
                    viewModel.getTransferredItems().stream().map(e -> e.getData()).toList());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> addManualItem_invalidatedEvent(){
        return test("addManualItem() fires an invalidated event to all registered listeners", 
                args -> assertInvalidatedEventFired(args, vm -> args.invalidate(vm), 
                        vm -> vm.addManualItem(vm.getContent().get(0).getData())));
    }
    
    @TestFactory
    Stream<DynamicNode> update_keepAddedItemsWhenNotChangingConfiguration(){
        return test("update() does not modify the added items when the configuration is unchanged", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convert();
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            viewModel.update(configuration, 
                    new MatchesTest.MatchesArgs<>(List.of(
                            Pair.of("first foo", "first bar"),
                            Pair.of("second foo", "second bar"))).convert());
            viewModel.addManualItem(viewModel.getContent().get(0).getData());
            Map<String, String> expectedContent = 
                    Map.copyOf(viewModel.getTransferredItems().get(0).observableMatch());
            viewModel.update(configuration, 
                    new MatchesTest.MatchesArgs<>(List.of(Pair.of("foo", "bar"))).convert());
            Assertions.assertAll(
                    () -> Assertions.assertEquals(1, viewModel.getTransferredItems().size()),
                    () -> Assertions.assertEquals(expectedContent, 
                            viewModel.getTransferredItems().get(0).observableMatch())
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_changeAddedItemsWhenChangingConfiguration(){
        return test("update() does updates the representation of the added items when the configuration is changed", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convert();
            viewModel.update(args.getResultConfiguration(), 
                    new MatchesTest.MatchesArgs<>(List.of(Pair.of("foo", "bar"))).convert());
            List<String> expectedHeader = List.of("unique");
            ResultConfiguration<String, String> configuration = ResultConfiguration.create("name", 
                    expectedHeader, match -> new String[]{match.getMentee()});
            List<Map<String, String>> expectedContent = List.of(Map.of("unique", "foo"));
           viewModel.addManualItem(viewModel.getContent().get(0).getData());
            viewModel.update(configuration, 
                    new MatchesTest.MatchesArgs<>(List.of(Pair.of("foo", "bar"))).convert());
            assertContentAsExpected(expectedContent, viewModel.getTransferredItems());
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> removeManualItem_NPE(){
        return test("removeManualItem() throws an NPE when removing a null object", args -> {
           MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                   args.convert();
           Assertions.assertThrows(NullPointerException.class, () -> viewModel.removeManualItem(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeManualItem_removeFromTransferred(){
        return test("removeManualItem() removed the item from the transferred items", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndUpdate();
            List<Match<String, String>> expectedContent = 
                    List.of(viewModel.getContent().get(1).getData());
            viewModel.addManualItem(viewModel.getContent().get(1).getData());
            viewModel.addManualItem(viewModel.getContent().get(0).getData());
            Assertions.assertAll(
                    () -> Assertions.assertTrue(
                            viewModel.removeManualItem(viewModel.getTransferredItems().get(1))),
                    () -> Assertions.assertEquals(expectedContent,
                            viewModel.getTransferredItems().stream().map(e -> e.getData()).toList())
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeManualItem_invalidatedEvent(){
        return test("removeManualItem() fires an invalidated event to all registered listeners", 
                args -> assertInvalidatedEventFired(args, vm -> {
                    args.invalidate(vm);
                    vm.addManualItem(vm.getContent().get(0).getData());
                }, vm -> vm.removeManualItem(vm.getTransferredItems().get(0))));
    }
    
    @TestFactory
    Stream<DynamicNode> removeManualItem_noInvalidatedEventOnNoOp(){
        return test("removeManualItem() does not fire an invalidated event when not removing", 
                args -> assertNoInvalidatedEventFired(args, vm -> args.invalidate(vm), 
                        vm -> vm.removeManualItem(vm.getContent().get(0))));
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_NPE(){
        return test("writeMatches() throws an NPE on null input", args -> {
            //TODO refactor make 3 different tests for the 3 test cases
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndUpdate();
            Class<? extends Exception> expectedException = NullPointerException.class;
            Assertions.assertAll(
                    () -> Assertions.assertThrows(expectedException, 
                            () -> viewModel.writeMatches(null, args.getResultConfiguration())),
                    () -> Assertions.assertThrows(expectedException,
                            () -> viewModel.writeMatches(System.out, null)),
                    () -> Assertions.assertThrows(expectedException,
                            () -> viewModel.writeMatches(null, null)));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_expectedResult_onlyAutomated(){
        return test("writeMatches() writes the expected result with only automated matches", args -> {
            OutputStream os = new ByteArrayOutputStream();
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndUpdate();
            viewModel.getContent();
            try {
                viewModel.writeMatches(os, configuration);
            } catch (IOException e){
                Assertions.fail(e);
            }
            String actualResult = os.toString();
            String expectedResult = """
                    "first","second"
                    "first mentee","first mentor"
                    "second mentee","second mentor"
                    """;
            Assertions.assertEquals(expectedResult, actualResult);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_onlyManual(){
        return test("writeMatches() writes the expected result with only manual matches", args -> {
            OutputStream os = new ByteArrayOutputStream();
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convert();
            viewModel.update(configuration, new Matches<>(new ArrayList<>()));
            Matches<String, String> matches = new MatchesTest.MatchesArgs<>(args.input).convert();
            for(Match<String, String> match : matches){
                viewModel.addManualItem(match);
            }
            viewModel.getTransferredItems();
            try {
                viewModel.writeMatches(os, configuration);
            } catch (IOException e){
                Assertions.fail(e);
            }
            String actualResult = os.toString();
            String expectedResult = """
                    "first","second"
                    "first mentee","first mentor"
                    "second mentee","second mentor"
                    """;
            Assertions.assertEquals(expectedResult, actualResult);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_both(){
        return test("writeMatches() writes the expected result with both manual and automated matches", args -> {
            OutputStream os = new ByteArrayOutputStream();
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndUpdate();
            Matches<String, String> matches = new MatchesTest.MatchesArgs<>(
                    List.of(Pair.of("third mentee", "third mentor"), 
                            Pair.of("fourth mentee", "fourth mentor"))).convert();
            for(Match<String, String> match : matches){
                viewModel.addManualItem(match);
            }
            viewModel.getContent();
            try {
                viewModel.writeMatches(os, configuration);
            } catch (IOException e){
                Assertions.fail(e);
            }
            String actualResult = os.toString();
            String expectedResult = """
                    "first","second"
                    "third mentee","third mentor"
                    "fourth mentee","fourth mentor"
                    "first mentee","first mentor"
                    "second mentee","second mentor"
                    """;
            Assertions.assertEquals(expectedResult, actualResult);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_none(){
        return test("writeMatches() writes the expected result with no matches", args -> {
            OutputStream os = new ByteArrayOutputStream();
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convert();
            viewModel.update(configuration,
                    new MatchesTest.MatchesArgs<>(
                            new ArrayList<Pair<? extends String, ? extends String>>())
                    .convert());
            viewModel.getContent();
            try {
                viewModel.writeMatches(os, configuration);
            } catch (IOException e){
                Assertions.fail(e);
            }
            String actualResult = os.toString();
            String expectedResult = """
                    "first","second"
                    """;
            Assertions.assertEquals(expectedResult, actualResult);
        });
    }
    
    static class MatchesViewModelTestArgs extends ObservableViewModelArgs<
            MatchesViewModel<String, String, MatchViewModel<String, String>>>{
        private final List<String> expectedHeader;
        private final List<Pair<? extends String, ? extends String>> input;
        private final List<Map<String, String>> expectedContent;
        
        MatchesViewModelTestArgs(String testCase, List<String> expectedHeader,
                List<Pair<? extends String, ? extends String>> input, 
                List<Map<String, String>> expectedContent){
            super(testCase);
            this.expectedHeader = expectedHeader;
            this.input = input;
            this.expectedContent = expectedContent;
        }
    
        @Override
        protected MatchesViewModel<String, String, MatchViewModel<String, String>> convert(){
            return new MatchesViewModel<>(MatchViewModel::new);
        }
        
       MatchesViewModel<String, String, MatchViewModel<String, String>> convertAndUpdate(){
            MatchesViewModel<String, String, MatchViewModel<String, String>> vm = convert();
            invalidate(vm);
            return vm;
        }
       
       @Override
       protected void invalidate(
               MatchesViewModel<String, String, MatchViewModel<String, String>> vm){
           vm.update(getResultConfiguration(),
                   new MatchesTest.MatchesArgs<>(input).convert());
       }
       
       ResultConfiguration<String, String> getResultConfiguration(){
           return ResultConfiguration.create("name", expectedHeader,
                   match -> new String[]{match.getMentee(), match.getMentor()});
       }
    }
}
