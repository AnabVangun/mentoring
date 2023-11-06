package mentoring.match;

import assignmentproblem.Solver;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;
import test.tools.TestArgs;
import test.tools.TestFramework;

final class MatchesBuilderTest implements TestFramework<MatchesBuilderTest.MatchesBuilderArgs>{

    @Override
    public Stream<MatchesBuilderArgs> argumentsSupplier() {
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
                                mentee.equals(mentor) ? prohibitiveCost : standardCost)),
                new PublicMatchesBuilderArgs("non square matrix", expectedMatches, 
                        List.of(0,1,12), List.of(0,1),
                        List.of((mentee, mentor) -> 
                                (mentee.equals(mentor) || mentee.equals(12)) 
                                        ? prohibitiveCost : standardCost)));
        return test(testCase, "build() with default settings works", args -> {
            assertMatchesEquals(args.expectedMatches, args.convert().build());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> defaultMatchesBuilderWorksWithPartialBuild(){
        int prohibitiveCost = 2000;
        int standardCost = 5;
        Matches<Integer,Integer> expectedMatch = new Matches<>(List.of(
                new Match<>(0, 1, standardCost)));
        Stream<PublicPartialMatchesBuilderArgs> testCase = Stream.of(
                new PublicPartialMatchesBuilderArgs("one valid match and an unassigned mentor", 
                        expectedMatch, 
                        List.of(0, 1), List.of(0, 1), 
                        List.of((mentee, mentor) -> 
                                mentee.equals(mentor) ? prohibitiveCost : standardCost),
                        List.of(0), List.of(1,0),
                        null),
                new PublicPartialMatchesBuilderArgs("one valid match and an unassigned mentee",
                        expectedMatch,
                        List.of(0, 1), List.of(0, 1), 
                        List.of((mentee, mentor) -> 
                                mentee.equals(mentor) ? prohibitiveCost : standardCost),
                        List.of(0,1), List.of(1),
                        null));
        return test(testCase, "build() with default settings works", args -> {
            assertMatchesEquals(args.expectedMatches, 
                    args.convert().build(args.partialMentees, args.partialMentors));
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
    Stream<DynamicNode> defaultMatchesBuilderWithNecessaryCriteriaWorksWithPartialBuild(){
        Matches<Integer,Integer> expectedMatch = new Matches<>(List.of(new Match<>(2,5,10)));
        Stream<PublicPartialMatchesBuilderArgs> testCase = Stream.of(
                new PublicPartialMatchesBuilderArgs("one valid match and an unassigned mentor", 
                        expectedMatch, 
                        List.of(2, 4, 6), List.of(5, 10, 15), 
                        List.of((mentee, mentor) -> mentee*mentor),
                        List.of(2, 6), List.of(10,5),
                        null),
                new PublicPartialMatchesBuilderArgs("one valid match and an unassigned mentee",
                        expectedMatch,
                        List.of(2, 4), List.of(5, 10, 15, 20),
                        List.of((mentee, mentor) -> mentee*mentor), 
                        List.of(4,2), List.of(5, 20, 15),
                        null));
        return test(testCase, "build() with necessary criterion works", args -> {
            MatchesBuilder<Integer,Integer> builder = args.convert();
            builder.withNecessaryCriteria(List.of((mentee, mentor) -> (mentee == 2 && mentor == 5)));
            assertMatchesEquals(args.expectedMatches, 
                    builder.build(args.partialMentees, args.partialMentors));
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
    Stream<DynamicNode> defaultMatchesBuilderWithSolverWorksWithPartialBuild(){
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
            assertMatchesEquals(args.expectedMatches, builder.build(List.of(0,1), List.of(0,1)));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> forbidMatch_forwardToHandler(){
        @SuppressWarnings("unchecked")
        CostMatrixHandler<Integer,Integer> handler = Mockito.mock(CostMatrixHandler.class);
        Stream<HandlerMatchesBuilderArgs> testCase = Stream.of(
                new HandlerMatchesBuilderArgs("minimal test case", null, 
                        List.of(3,2), List.of(5,6), handler));
        return test(testCase, "forbidMatch() forwards the call to the handler", args -> {
            MatchesBuilder<Integer, Integer> builder = args.convert();
            builder.forbidMatch(3, 6);
            Mockito.verify(handler).forbidMatch(0, 1);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> allowMatch_forwardToHandler(){
        @SuppressWarnings("unchecked")
        CostMatrixHandler<Integer,Integer> handler = Mockito.mock(CostMatrixHandler.class);
        Stream<HandlerMatchesBuilderArgs> testCase = Stream.of(
                new HandlerMatchesBuilderArgs("minimal test case", null, 
                        List.of(3,2), List.of(5,6), handler));
        return test(testCase, "allowMatch() forwards the call to the handler", args -> {
            MatchesBuilder<Integer, Integer> builder = args.convert();
            builder.allowMatch(2, 5);
            Mockito.verify(handler).allowMatch(1, 0);
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
    
    @TestFactory
    Stream<DynamicNode> defaultMatchesBuilderWithPlaceholderWorksWithPartialBuild(){
        Integer defaultMentee = 5;
        Integer defaultMentor = -2;
        Integer unassigned = -1;
        Matches<Integer,Integer> validMatchWithAMenteePlaceholder = new Matches<>(List.of(
                new Match<>(2,5,10),
                new Match<>(defaultMentee,10,MatchesBuilder.PROHIBITIVE_VALUE)));
        Matches<Integer, Integer> validMatchWithAMentorPlaceholder = new Matches<>(List.of(
                new Match<>(2,5,10),
                new Match<>(4,defaultMentor,MatchesBuilder.PROHIBITIVE_VALUE)));
        Stream<PublicPartialMatchesBuilderArgs> testCase = Stream.of(
                new PublicPartialMatchesBuilderArgs("one valid match and a mentee placeholder", 
                        validMatchWithAMenteePlaceholder, 
                        List.of(2, 4), List.of(5, 10), 
                        List.of((mentee, mentor) -> mentee*mentor),
                        List.of(2), List.of(10,5),
                        new DummySolver(List.of(1), List.of(unassigned, 0))),
                new PublicPartialMatchesBuilderArgs("one valid match and a mentor placeholder",
                        validMatchWithAMentorPlaceholder,
                        List.of(2, 4), List.of(5, 10),
                        List.of((mentee, mentor) -> mentee*mentor), 
                        List.of(4,2), List.of(5),
                        new DummySolver(List.of(unassigned, 0), List.of(1))));
        return test(testCase, "build() with custom solver works", args -> {
            MatchesBuilder<Integer,Integer> builder = args.convert();
            builder.withSolver(args.solver, unassigned);
            builder.withPlaceholderPersons(defaultMentee, defaultMentor);
            Matches<Integer, Integer> actual = builder.build(args.partialMentees, args.partialMentors);
            assertMatchesEquals(args.expectedMatches, actual);
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
    
    @TestFactory
    Stream<DynamicNode> buildSingleMatch_validInput(){
        int prohibitiveCost = 2000;
        int standardCost = 5;
        Stream<PublicMatchesBuilderArgs> testCase = Stream.of(
                new PublicMatchesBuilderArgs("minimal test case", null, 
                        List.of(0,1,2), List.of(0,1,2), 
                        List.of((mentee, mentor) -> 
                                mentee.equals(mentor) ? prohibitiveCost : standardCost)
                ));
        return test(testCase, "buildSingleMatch() returns the expected match", args -> {
            MatchesBuilder<Integer,Integer> builder = args.convert();
            builder.withNecessaryCriteria(List.of((mentee, mentor) -> mentee.equals(mentor)));
            Assertions.assertAll(
                    () -> Assertions.assertEquals(new Match<>(0, 0, prohibitiveCost), 
                            builder.buildSingleMatch(0, 0)),
                    () -> Assertions.assertEquals(new Match<>(1, 2, MatchesBuilder.PROHIBITIVE_VALUE),
                            builder.buildSingleMatch(1,2)));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> buildSingleMatch_invalidInput(){
        Stream<PublicMatchesBuilderArgs> testCase = Stream.of(
                new PublicMatchesBuilderArgs("minimal test case", null, 
                        List.of(0,1), List.of(0,1), 
                        List.of((mentee, mentor) -> 
                                mentee.equals(mentor) ? 1 : 1)
                ));
        Class<IllegalArgumentException> expectedException = IllegalArgumentException.class;
        return test(testCase, "buildSingleMatch() fails on invalid input", args -> {
            MatchesBuilder<Integer,Integer> builder = args.convert();
            Assertions.assertAll(
                    () -> Assertions.assertThrows(expectedException, 
                            () -> builder.buildSingleMatch(0, 1235), 
                            "failed to fail on invalid mentor"),
                    () -> Assertions.assertThrows(expectedException, 
                            () -> builder.buildSingleMatch(6291, 1), 
                            "failed to fail on invalid mentee"),
                    () -> Assertions.assertThrows(expectedException, 
                            () -> builder.buildSingleMatch(987654, 1235), 
                            "failed to fail on invalid mentor and mentee"));
        });
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
    
    static class PublicPartialMatchesBuilderArgs extends PublicMatchesBuilderArgs{
        final List<Integer> partialMentees;
        final List<Integer> partialMentors;
        final Solver solver;
        
        PublicPartialMatchesBuilderArgs(String testCase, Matches<Integer, Integer> expectedMatches,
                List<Integer> mentees, List<Integer> mentors,
                Collection<ProgressiveCriterion<Integer, Integer>> progressiveCriteria, 
                List<Integer> menteeIndices, List<Integer> mentorIndices,
                Solver solver){
            super(testCase, expectedMatches, mentees, mentors, progressiveCriteria);
            this.partialMentees = menteeIndices;
            this.partialMentors = mentorIndices;
            this.solver = solver;
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
