package mentoring.viewmodel.match;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import mentoring.match.Matches;
import mentoring.match.MatchesTest;
import mentoring.viewmodel.base.ObservableTest;
import mentoring.viewmodel.base.ObservableArgs;
import mentoring.viewmodel.match.MatchesViewModelTest.MatchesViewModelTestArgs;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class MatchesViewModelTest extends ObservableTest<
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
        return test("constructor does not fail", args -> 
                Assertions.assertDoesNotThrow(() -> args.convert()));
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> setConfiguration_NPE(){
        return test("setConfiguration() throws an NPE on null input", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convert();
            Assertions.assertThrows(NullPointerException.class, 
                    () -> viewModel.setConfiguration(null));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> setConfiguration_once(){
        return test("setConfiguration() properly sets the header", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndConfigure();
            Assertions.assertEquals(args.expectedHeader, viewModel.getHeaders());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setConfiguration_repeated(){
        return test("repeated calls to setConfiguration() properly set the header", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndConfigure();
            List<String> expectedHeader = List.of("unique");
            ResultConfiguration<String, String> newConf = ResultConfiguration.createForArrayLine("name", 
                    expectedHeader, match -> new String[]{match.getMentee()});
            viewModel.setConfiguration(newConf);
            Assertions.assertEquals(expectedHeader, viewModel.getHeaders());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setConfiguration_updateContentRepresentation(){
        return test("setConfiguration() properly update the content", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndConfigure();
            List<String> newHeader = List.of("unique");
            ResultConfiguration<String, String> newConf = ResultConfiguration.createForArrayLine("name", 
                    newHeader, match -> new String[]{match.getMentee()});
            Matches<String, String> content = new MatchesTest.MatchesArgs<>(
                    List.of(Pair.of("mentee", "mentor"))).convert();
            viewModel.setAll(content);
            List<Map<String, String>> expectedContent = List.of(Map.of("unique","mentee"));
            viewModel.setConfiguration(newConf);
            assertContentAsExpected(expectedContent, viewModel.getContent());
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> setAll_NPE(){
        return test("setAll() throws an NPE on a null input", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndConfigure();
            Assertions.assertThrows(NullPointerException.class, () -> viewModel.setAll(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setAll_once(){
        return test("setAll() properly sets the content", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndSetContent();
            assertContentAsExpected(args.expectedContent, viewModel.getContent());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setAll_repeated(){
        return test("repeated calls to setAll() properly set the content", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel = 
                    args.convertAndSetContent();
            List<Map<String, String>> expectedContent = 
                    List.of(Map.of("first", "foo", "second", "bar"));
            viewModel.setAll(new MatchesTest.MatchesArgs<>(
                    List.of(Pair.of("foo", "bar"))).convert());
            assertContentAsExpected(expectedContent, viewModel.getContent());
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> setAll_beforeConfiguration(){
        return test("setAll() throws an exception if called before setConfiguration()", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convert();
            Assertions.assertThrows(IllegalStateException.class, () -> viewModel.setAll(args.input));
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
    Stream<DynamicNode> setConfiguration_invalidatedEvent(){
        return test("setConfiguration() fires an invalidated event to all registered listeners",
                args -> assertInvalidatedEventFired(args, 
                        vm -> vm.setConfiguration(args.getResultConfiguration())));
    }
    
    @TestFactory
    Stream<DynamicNode> setConfiguration_noOp_noInvalidatedEvent(){
        return test("setConfiguration() does not fire an invalidated event when no-op", args -> {
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            assertNoInvalidatedEventFired(args,
                    vm -> vm.setConfiguration(configuration),
                    vm -> vm.setConfiguration(configuration));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> setConfiguration_repeated_invalidatedEvent(){
        return test("repeated calls to setConfiguration() fire an invalidated event",
                args -> assertInvalidatedEventFired(args,
                        vm -> vm.setConfiguration(args.getResultConfiguration()),
                        vm -> {
                            List<String> newHeader = List.of("unique");
                            ResultConfiguration<String, String> newConf =
                                    ResultConfiguration.createForArrayLine("name",newHeader, 
                                            match -> new String[]{match.getMentee()});
                            vm.setConfiguration(newConf);
                                }));
    }
    
    @TestFactory
    Stream<DynamicNode> setAll_invalidatedEvent(){
        return test("setAll() fires an invalidated event to all registered listeners", 
                args -> assertInvalidatedEventFired(args, 
                        vm -> vm.setConfiguration(args.getResultConfiguration()),
                        vm -> vm.setAll(args.input)));
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> add_NPE(){
        return test("add() throws an NPE when adding a null object", args -> {
           MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                   args.convertAndConfigure();
           Assertions.assertThrows(NullPointerException.class, () -> viewModel.add(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> add_addToContent(){
        return test("add() adds the match", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndConfigure();
            List<Match<String, String>> expectedContent = 
                    List.of(forgeMatch("first mentee", "first mentor"),
                            forgeMatch("second mentee", "second mentor"));
            viewModel.add(expectedContent.get(0));
            viewModel.add(expectedContent.get(1));
            Assertions.assertEquals(expectedContent, 
                    viewModel.getContent().stream().map(e -> e.getData()).toList());
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> add_beforeConfiguration(){
        return test("add() throws an exception if called before setConfiguration()", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convert();
            Assertions.assertThrows(IllegalStateException.class, 
                    () -> viewModel.add(forgeMatch("first mentee", "first mentor")));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> add_invalidatedEvent(){
        return test("add() fires an invalidated event to all registered listeners", 
                args -> assertInvalidatedEventFired(args, vm -> vm.setConfiguration(args.getResultConfiguration()), 
                        vm -> vm.add(forgeMatch("mentee", "mentor"))));
    }
    
    @TestFactory
    Stream<DynamicNode> remove_removeFromContent(){
        return test("remove() removes the item from the content", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndSetContent();
            List<Match<String, String>> expectedContent = 
                    List.of(viewModel.getContent().get(1).getData());
            Assertions.assertAll(
                    () -> Assertions.assertTrue(
                            viewModel.remove(viewModel.getContent().get(0))),
                    () -> Assertions.assertEquals(expectedContent,
                            viewModel.getContent().stream().map(e -> e.getData()).toList())
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> remove_invalidatedEvent(){
        return test("remove() fires an invalidated event to all registered listeners", 
                args -> assertInvalidatedEventFired(args, 
                        vm -> {
                            vm.setConfiguration(args.getResultConfiguration());
                            vm.add(forgeMatch("mentee", "mentor"));
                        }, 
                        vm -> vm.remove(vm.getContent().get(0))));
    }
    
    @TestFactory
    Stream<DynamicNode> remove_noInvalidatedEventOnNoOp(){
        return test("remove() does not fire an invalidated event when not removing", 
                args -> assertNoInvalidatedEventFired(args, vm -> args.invalidate(vm), 
                        vm -> vm.remove(new MatchViewModel<>(args.getResultConfiguration(), 
                                forgeMatch("absent mentee", "absent mentor")))));
    }
    
    private static Match<String, String> forgeMatch(String mentee, String mentor){
        return new MatchTest.MatchArgs("match", mentee, mentor, 12)
                .convertAs(String.class, String.class);
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_NPE(){
        return test("writeMatches() throws an NPE on null input", args -> {
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndSetContent();
            Class<? extends Exception> expectedException = NullPointerException.class;
            Assertions.assertAll(
                    () -> Assertions.assertThrows(expectedException, 
                            () -> viewModel.writeMatches(null, args.getResultConfiguration(), true)),
                    () -> Assertions.assertThrows(expectedException,
                            () -> viewModel.writeMatches(
                                    new PrintWriter(System.out, false, Charset.forName("utf-8")), 
                                    null, false)),
                    () -> Assertions.assertThrows(expectedException,
                            () -> viewModel.writeMatches(null, null, true)));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_expectedResult_header(){
        return test("writeMatches() writes the expected result with non-empty content", args -> {
            StringWriter writer = new StringWriter();
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndSetContent();
            Matches<String, String> matches = new MatchesTest.MatchesArgs<>(
                    List.of(Pair.of("third mentee", "third mentor"), 
                            Pair.of("fourth mentee", "fourth mentor"))).convert();
            for(Match<String, String> match : matches){
                viewModel.add(match);
            }
            viewModel.getContent();
            try {
                viewModel.writeMatches(writer, configuration, true);
            } catch (IOException e){
                Assertions.fail(e);
            }
            String actualResult = writer.toString();
            String expectedResult = """
                    "first","second"
                    "first mentee","first mentor"
                    "second mentee","second mentor"
                    "third mentee","third mentor"
                    "fourth mentee","fourth mentor"
                    """;
            Assertions.assertEquals(expectedResult, actualResult);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_expectedResult_header_empty(){
        return test("writeMatches() writes the expected result with no matches", args -> {
            StringWriter writer = new StringWriter();
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndConfigure();
            viewModel.setAll(
                    new MatchesTest.MatchesArgs<>(
                            new ArrayList<Pair<? extends String, ? extends String>>())
                    .convert());
            viewModel.getContent();
            try {
                viewModel.writeMatches(writer, configuration, true);
            } catch (IOException e){
                Assertions.fail(e);
            }
            String actualResult = writer.toString();
            String expectedResult = """
                    "first","second"
                    """;
            Assertions.assertEquals(expectedResult, actualResult);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_expectedResult_withoutHeader(){
        return test("writeMatches() writes the expected result with non-empty content and no header", args -> {
            StringWriter os = new StringWriter();
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndSetContent();
            viewModel.getContent();
            try {
                viewModel.writeMatches(os, configuration, false);
            } catch (IOException e){
                Assertions.fail(e);
            }
            String actualResult = os.toString();
            String expectedResult = """
                    "first mentee","first mentor"
                    "second mentee","second mentor"
                    """;
            Assertions.assertEquals(expectedResult, actualResult);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> writeMatches_expectedResult_withoutHeader_empty(){
        return test("writeMatches() writes the expected result with no matches and no header", args -> {
            StringWriter os = new StringWriter();
            ResultConfiguration<String, String> configuration = args.getResultConfiguration();
            MatchesViewModel<String, String, MatchViewModel<String, String>> viewModel =
                    args.convertAndConfigure();
            viewModel.setAll(
                    new MatchesTest.MatchesArgs<>(
                            new ArrayList<Pair<? extends String, ? extends String>>())
                    .convert());
            viewModel.getContent();
            try {
                viewModel.writeMatches(os, configuration, false);
            } catch (IOException e){
                Assertions.fail(e);
            }
            String actualResult = os.toString();
            String expectedResult = "";
            Assertions.assertEquals(expectedResult, actualResult);
        });
    }
    
    static class MatchesViewModelTestArgs extends ObservableArgs<
            MatchesViewModel<String, String, MatchViewModel<String, String>>>{
        private final List<String> expectedHeader;
        private final Matches<String, String> input;
        private final List<Map<String, String>> expectedContent;
        
        MatchesViewModelTestArgs(String testCase, List<String> expectedHeader,
                List<Pair<? extends String, ? extends String>> input, 
                List<Map<String, String>> expectedContent){
            super(testCase);
            this.expectedHeader = expectedHeader;
            this.input = new MatchesTest.MatchesArgs<>(input).convert();
            this.expectedContent = expectedContent;
        }
    
        @Override
        protected MatchesViewModel<String, String, MatchViewModel<String, String>> convert(){
            return new MatchesViewModel<>(MatchViewModel::new);
        }
        
        MatchesViewModel<String, String, MatchViewModel<String, String>> convertAndConfigure(){
            MatchesViewModel<String, String, MatchViewModel<String, String>> vm = convert();
            vm.setConfiguration(getResultConfiguration());
            return vm;
        }
        
       MatchesViewModel<String, String, MatchViewModel<String, String>> convertAndSetContent(){
            MatchesViewModel<String, String, MatchViewModel<String, String>> vm = 
                    convertAndConfigure();
            vm.setAll(input);
            return vm;
        }
       
       @Override
       protected void invalidate(
               MatchesViewModel<String, String, MatchViewModel<String, String>> vm){
           vm.setConfiguration(getResultConfiguration());
           vm.setAll(input);
       }
       
       ResultConfiguration<String, String> getResultConfiguration(){
           return ResultConfiguration.createForArrayLine("name", expectedHeader,
                   match -> new String[]{match.getMentee(), match.getMentor()});
       }
    }
}
