package mentoring.viewmodel.match;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
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
            Assertions.assertEquals(args.expectedHeader, viewModel.getHeaderContent());
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
            Assertions.assertEquals(expectedHeader, viewModel.getHeaderContent());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_content(){
        return test("update() properly sets the content", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            List<Map<String, String>> actualContent = viewModel.getBatchItems().stream()
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
            assertContentAsExpected(expectedContent, viewModel.getBatchItems());
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
        return test("update() fires an invalidated event to all registered listeners", args -> {
            Observable[] notified = new Observable[2];
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convert();
            viewModel.addListener(observable -> notified[0] = observable);
            viewModel.addListener(observable -> notified[1] = observable);
            args.update(viewModel);
            Assertions.assertAll(
                    () -> Assertions.assertSame(viewModel, notified[0]),
                    () -> Assertions.assertSame(viewModel, notified[1])
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> addListener_NPE(){
        return test("addListener() throws an NPE when adding a null object", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convert();
            Assertions.assertThrows(NullPointerException.class, () -> viewModel.addListener(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeListener_nominal(){
        return test("removeListener() removes exactly the input listener", args -> {
            Observable[] notified = new Observable[2];
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convert();
            InvalidationListener removed = observable -> notified[0] = observable;
            viewModel.addListener(removed);
            viewModel.addListener(observable -> notified[1] = observable);
            viewModel.removeListener(removed);
            args.update(viewModel);
            Assertions.assertAll(
                    () -> Assertions.assertNull(notified[0]),
                    () -> Assertions.assertSame(viewModel, notified[1])
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeListener_NPE(){
        return test("removeListener() throws an NPE when removing a null object", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convert();
            Assertions.assertThrows(NullPointerException.class, 
                    () -> viewModel.removeListener(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeListener_notPreviouslyRegistered(){
        return test("removeListener() does not throw exception when removing absent listener", args -> {
            Observable[] notified = new Observable[2];
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convert();
            viewModel.addListener(observable -> notified[0] = observable);
            viewModel.addListener(observable -> notified[1] = observable);
            viewModel.removeListener(observable -> notified[-1] = observable);
            args.update(viewModel);
            Assertions.assertAll(
                    () -> Assertions.assertSame(viewModel, notified[1]),
                    () -> Assertions.assertSame(viewModel, notified[1])
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> addManualItem_NPE(){
        return test("addManualItem() throws an NPE when adding a null object", args -> {
           MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                   args.convert();
           Assertions.assertThrows(NullPointerException.class, () -> viewModel.addManualItem(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> addManualItem_addToTransferred(){
        return test("addManualItem() adds the item to the batch items", args -> {
            /*FIXME: this test only verifies that items already present in the batch
            items can be added to the manual ones. It should mostly verify that :
            1. a match between two persons not in the batch items works;
            2. a match between two persons in the batch items (but not necessarily together) works.
            */
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndUpdate();
            List<Match<String, String>> expectedContent = 
                    List.of(viewModel.getBatchItems().get(1).getData(), 
                            viewModel.getBatchItems().get(0).getData());
            viewModel.addManualItem(viewModel.getBatchItems().get(1).getData());
            viewModel.addManualItem(viewModel.getBatchItems().get(0).getData());
            Assertions.assertEquals(expectedContent, 
                    viewModel.getTransferredItems().stream().map(e -> e.getData()).toList());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> addManualItem_invalidatedEvent(){
        return test("addManualItem() fires an invalidated event to all registered listeners", args -> {
            Observable[] notified = new Observable[2];
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndUpdate();
            viewModel.addListener(observable -> notified[0] = observable);
            viewModel.addListener(observable -> notified[1] = observable);
            viewModel.addManualItem(viewModel.getBatchItems().get(0).getData());
            Assertions.assertAll(
                    () -> Assertions.assertSame(viewModel, notified[0]),
                    () -> Assertions.assertSame(viewModel, notified[1])
            );
        });
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
            viewModel.addManualItem(viewModel.getBatchItems().get(0).getData());
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
           viewModel.addManualItem(viewModel.getBatchItems().get(0).getData());
            viewModel.update(configuration, 
                    new MatchesTest.MatchesArgs<>(List.of(Pair.of("foo", "bar"))).convert());
            assertContentAsExpected(expectedContent, viewModel.getTransferredItems());
        });
    }
    
    static record MatchesViewModelTestArgs(String testCase, List<String> expectedHeader,
            List<Pair<? extends String, ? extends String>> input, 
            List<Map<String, String>> expectedContent) {
        
        @Override
        public String toString(){
            return testCase;
        }
        
        MatchesViewModel<String, String, MatchViewModel<String, String>> convert(){
            return new MatchesViewModel<>(MatchViewModel::new);
        }
        
       MatchesViewModel<String, String, MatchViewModel<String, String>> convertAndUpdate(){
            MatchesViewModel<String, String, MatchViewModel<String, String>> vm = convert();
            update(vm);
            return vm;
        }
       
       void update(
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
