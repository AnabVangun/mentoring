package mentoring.match;

import assignmentproblem.Solver;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.AssertionFailedError;
import test.tools.TestArgs;
import test.tools.TestFramework;

final class MatchesBuilderTest implements TestFramework<MatchesBuilderTest.MatchesBuilderArgs>{

    @Override
    public Stream<MatchesBuilderArgs> argumentsSupplier() {
        //TODO implement
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @TestFactory
    Stream<DynamicNode> defaultMatchesBuilderWorks(){
        int prohibitiveCost = 2000;
        int standardCost = 5;
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,1,standardCost), new Match<>(1,0,standardCost)));
        Stream<PublicMatchesBuilderArgs> testCase = Stream.of(
                new PublicMatchesBuilderArgs("minimal test case", expectedMatches, 
                        List.of(0,1), List.of(0,1), 
                        List.of((mentee, mentor) -> 
                                mentee.equals(mentor) ? prohibitiveCost : standardCost)
                ));
        return test(testCase, "build() with default settings works", args -> {
            assertMatchesEquals(args.expectedMatches, args.convert().build());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> defaultMatchesBuilderWithNecessaryCriteriaWorks(){
        int prohibitiveCost = 2000;
        int standardCost = 5;
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,0,prohibitiveCost), new Match<>(1,1,prohibitiveCost)));
        Stream<PublicMatchesBuilderArgs> testCase = Stream.of(
                new PublicMatchesBuilderArgs("minimal test case", expectedMatches, 
                        List.of(0,1), List.of(0,1), 
                        List.of((mentee, mentor) -> 
                                mentee.equals(mentor) ? prohibitiveCost : standardCost)
                ));
        return test(testCase, "build() with necessary criterion works", args -> {
            MatchesBuilder<Integer,Integer> builder = args.convert();
            builder.withNecessaryCriteria(List.of((mentee, mentor) -> mentee.equals(mentor)));
            assertMatchesEquals(args.expectedMatches, builder.build());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> defaultMatchesBuilderWithSolverWorks(){
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,0,0), new Match<>(1,1,1)));
        Solver solver = new DummySolver(List.of(0,1), List.of(0,1));
        Stream<PublicMatchesBuilderArgs> testCase = Stream.of(
                new PublicMatchesBuilderArgs("minimal test case with solver", expectedMatches, 
                        List.of(0,1), List.of(0,1), 
                        List.of((mentee, mentor) -> mentee*mentor)
                ));
        return test(testCase, "build() with custom solver works", args -> {
            MatchesBuilder<Integer,Integer> builder = args.convert();
            builder.withSolver(solver, -1);
            assertMatchesEquals(args.expectedMatches, builder.build());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> defaultMatchesBuilderWithPlaceholderWorks(){
        Integer defaultMentee = 5;
        Integer defaultMentor = -2;
        Integer unassigned = 27;
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,defaultMentor,MatchesBuilder.PROHIBITIVE_VALUE), 
                new Match<>(defaultMentee,0,MatchesBuilder.PROHIBITIVE_VALUE)));
        Solver solver = new DummySolver(List.of(unassigned), List.of(unassigned));
        Stream<PublicMatchesBuilderArgs> testCase = Stream.of(
                new PublicMatchesBuilderArgs("minimal test case with placeholder", expectedMatches, 
                        List.of(0), List.of(0), 
                        List.of((mentee, mentor) -> mentee*mentor)
                ));
        return test(testCase, "build() with custom solver works", args -> {
            MatchesBuilder<Integer,Integer> builder = args.convert();
            builder.withSolver(solver, unassigned);
            builder.withPlaceholderPersons(defaultMentee, defaultMentor);
            assertMatchesEquals(args.expectedMatches, builder.build());
        });
    }
    
    static <Mentee, Mentor> void assertMatchesEquals(Matches<Mentee, Mentor> expected, 
            Matches<Mentee, Mentor> actual){
        Map<Mentee, Map<Mentor, Integer>> found = new HashMap<>();
        for (Match<Mentee, Mentor> match : expected){
                if (!found.containsKey(match.getMentee())){
                    found.put(match.getMentee(), new HashMap<>());
                }
                Map<Mentor, Integer> menteeMap = found.get(match.getMentee());
                menteeMap.put(match.getMentor(), menteeMap.getOrDefault(match.getMentor(), 0));
            }
        for (Match<Mentee, Mentor> match: actual){
            if (!found.containsKey(match.getMentee())){
                throw new AssertionFailedError(
                        buildAssertMatchesEqualsErrorMessage(expected, actual, match), 
                        expected, actual);
            }
            Map<Mentor, Integer> menteeMap = found.get(match.getMentee());
            if (!menteeMap.containsKey(match.getMentor())){
                throw new AssertionFailedError(
                        buildAssertMatchesEqualsErrorMessage(expected, actual, match), 
                        expected, actual);
            }
            if(menteeMap.get(match.getMentor()) == 1){
                menteeMap.remove(match.getMentor());
                if (menteeMap.isEmpty()){
                    found.remove(match.getMentee());
                }
            } else {
                menteeMap.put(match.getMentor(), menteeMap.get(match.getMentor()) - 1);
            }
        }
    }
    
    private static <Mentee, Mentor> String buildAssertMatchesEqualsErrorMessage(
            Matches<Mentee, Mentor> expected, Matches<Mentee, Mentor> actual, 
            Match<Mentee, Mentor> source){
        return "Matches " + actual + " differs from expected " + expected + 
                ": could not find enough times match " + source;
    }
    
    static abstract class MatchesBuilderArgs extends TestArgs{
        final Matches<Integer,Integer> expectedMatches;
        final List<Integer> mentees;
        final List<Integer> mentors;
        
        MatchesBuilderArgs(String testCase, Matches<Integer, Integer> expectedMatches,
                List<Integer> mentees, List<Integer> mentors){
            super(testCase);
            this.expectedMatches = expectedMatches;
            this.mentees = mentees;
            this.mentors = mentors;
        }
        
        abstract MatchesBuilder<Integer, Integer> convert();
    }
    
    static class PublicMatchesBuilderArgs extends MatchesBuilderArgs{
        final Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria;
        
        PublicMatchesBuilderArgs(String testCase, Matches<Integer, Integer> expectedMatches,
                List<Integer> mentees, List<Integer> mentors,
                Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria){
            super(testCase, expectedMatches, mentees, mentors);
            this.progressiveCriteria = progressiveCriteria;
        }
        
        @Override
        MatchesBuilder<Integer,Integer> convert(){
            return new MatchesBuilder<>(mentees, mentors, progressiveCriteria);
        }
    }
    
    static class HandlerMatchesBuilderArgs extends MatchesBuilderArgs{
        
        final CostMatrixHandler<Integer, Integer> handler;
        
        HandlerMatchesBuilderArgs(String testCase, Matches<Integer, Integer> expectedMatches,
                List<Integer> mentees, List<Integer> mentors, 
                CostMatrixHandler<Integer, Integer> handler) {
            super(testCase, expectedMatches, mentees, mentors);
            this.handler = handler;
        }
        
        HandlerMatchesBuilderArgs(String testCase, Matches<Integer, Integer> expectedMatches,
                List<Integer> mentees, List<Integer> mentors,
                Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria){
            this(testCase, expectedMatches, mentees, mentors, 
                    new CostMatrixHandler<>(mentees, mentors, progressiveCriteria));
        }
        
        @Override
        MatchesBuilder<Integer, Integer> convert(){
            return new MatchesBuilder<>(mentees, mentors, handler);
        }
    }
}
