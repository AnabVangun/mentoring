package mentoring.viewmodel.match;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import mentoring.viewmodel.datastructure.DataViewModel;
import mentoring.viewmodel.match.MatchViewModelTest.MatchViewModelArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class MatchViewModelTest implements TestFramework<MatchViewModelArgs>{
    
    @Override
    public Stream<MatchViewModelArgs> argumentsSupplier(){
        return Stream.of(new MatchViewModelArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_expectedObservable(){
        return test("constructor properly initialises the observable property", args -> {
            MatchViewModel<String, String> viewModel = args.convert();
            Assertions.assertEquals(args.expectedResult, viewModel.observableMatch());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_expectedMatch(){
        return test("constructor properly initialises the match", args -> {
            MatchViewModel<String, String> viewModel = args.convert();
            Assertions.assertEquals(args.match, viewModel.getData());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> containsMentee_True(){
        return test("containsMentee returns true if mentee is present", args -> {
            StringViewModel mentee = new StringViewModel(args.mentee);
            MatchViewModel<String, String> viewModel = args.convert();
            Assertions.assertTrue(viewModel.containsMentee(mentee));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> containsMentee_False(){
        return test("containsMentee returns false if mentee is absent", args -> {
            StringViewModel other = new StringViewModel(args.other);
            MatchViewModel<String, String> viewModel = args.convert();
            Assertions.assertFalse(viewModel.containsMentee(other));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> containsMentor_True(){
        return test("containsMentor returns true if mentor is present", args -> {
            StringViewModel mentor = new StringViewModel(args.mentor);
            MatchViewModel<String, String> viewModel = args.convert();
            Assertions.assertTrue(viewModel.containsMentor(mentor));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> containsMentor_False(){
        return test("containsMentor returns false if mentor is absent", args -> {
            StringViewModel other = new StringViewModel(args.other);
            MatchViewModel<String, String> viewModel = args.convert();
            Assertions.assertFalse(viewModel.containsMentee(other));
        });
    }
    
    static class MatchViewModelArgs extends TestArgs {
        final String mentee = "mentee";
        final String mentor = "mentor";
        final String other = "other";
        final Match<String, String> match = new MatchTest.MatchArgs("", "mentee", "mentor", 12)
                .convertAs(String.class, String.class);
        final Map<String, String> expectedResult = 
                Map.of("first", "first value", "second", "second value");
        final ResultConfiguration<String, String> configuration =
                ResultConfiguration.createForMapLine("name", List.of("first", "second"),
                        line -> expectedResult);
        
        MatchViewModelArgs(String testCase){
            super(testCase);
        }
        
        MatchViewModel<String, String> convert(){
            return new MatchViewModel<>(configuration, match);
        }
    }
    
    static class StringViewModel extends DataViewModel<String> {
        
        StringViewModel(String content){
            super(content, x -> Map.of("foo", "bar").entrySet().iterator());
        }
    }
}
