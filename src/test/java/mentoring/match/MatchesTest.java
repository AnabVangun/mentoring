package mentoring.match;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

public class MatchesTest implements TestFramework<MatchesArgs>{

    @Override
    public Stream<MatchesArgs> argumentsSupplier() {
        return Stream.of(new MatchesArgs(
            new Match("Mentor1","Mentee1",1),
            new Match("Mentor2","Mentee2",2),
            new Match("Mentor3",2,3)
        ));
    }
    
    @TestFactory
    public Stream<DynamicNode> getMentorMatchTest(){
        return test("getMentorMatch()", args -> {
            Matches matches = new Matches(args.matches);
           for(Match match:args.matches){
               Assertions.assertEquals(match, matches.getMentorMatch(match.getMentor()));
           } 
        });
    }
    
    @TestFactory
    public Stream<DynamicNode> getMenteeMatchTest(){
        return test("getMenteeMatch()", args -> {
            Matches matches = new Matches(args.matches);
           for(Match match:args.matches){
               Assertions.assertEquals(match, matches.getMenteeMatch(match.getMentee()));
           } 
        });
    }
}

class MatchesArgs{
    final List<Match<String, String>> matches;
    
    MatchesArgs(Match<String, String>... matches){
        this.matches = Arrays.asList(matches);
    }
}