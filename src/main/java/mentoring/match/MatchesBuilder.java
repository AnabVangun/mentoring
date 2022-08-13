package mentoring.match;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import assignmentproblem.hungariansolver.HungarianSolver;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class MatchesBuilder<Mentee, Mentor> {
    final private List<Mentee> mentees;
    final private List<Mentor> mentors;
    final CostMatrixHandler<Mentee, Mentor> costMatrixHandler;
    public static final int PROHIBITIVE_VALUE = Integer.MAX_VALUE;
    private Integer unassignedValue = null;
    private Solver solver = new HungarianSolver(unassignedValue);
    private Mentee defaultMentee;
    private Mentor defaultMentor;
    private boolean hasPlaceholderPersons = false;
    
    public MatchesBuilder(List<Mentee> mentees, List<Mentor> mentors,
            Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria){
        this(mentees, mentors, new CostMatrixHandler<>(mentees, mentors, progressiveCriteria));
    }
    
    MatchesBuilder(List<Mentee> mentees, List<Mentor> mentors, 
            CostMatrixHandler<Mentee, Mentor> handler){
        this.mentees = mentees;
        this.mentors = mentors;
        this.costMatrixHandler = handler;
    }
    
    public MatchesBuilder<Mentee, Mentor> withNecessaryCriteria(
            List<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria){
        costMatrixHandler.withNecessaryCriteria(necessaryCriteria);
        return this;
    }
    
    public MatchesBuilder<Mentee, Mentor> withSolver(Solver solver, Integer unassignedValue){
        this.solver = solver;
        this.unassignedValue = unassignedValue;
        return this;
    }
    
    public MatchesBuilder<Mentee, Mentor> withPlaceholderPersons(Mentee defaultMentee, 
            Mentor defaultMentor){
        this.hasPlaceholderPersons = true;
        this.defaultMentee = defaultMentee;
        this.defaultMentor = defaultMentor;
        return this;
    }
    
    public Matches<Mentee, Mentor> build(){
        costMatrixHandler.buildCostMatrix();
        Result rawResult = costMatrixHandler.solveCostMatrix(solver);
        return formatResult(rawResult);
    }
    
    private Matches<Mentee, Mentor> formatResult(Result rawResult){
        if (this.hasPlaceholderPersons){
            return formatMatchesWithPlaceholders(rawResult);
        } else {
            return filterAndFormatValidMatches(rawResult);
        }
    }
    
    private Matches<Mentee, Mentor> formatMatchesWithPlaceholders(Result rawResult){
        Stream<Match<Mentee, Mentor>> concatenation = Stream.concat(
            buildMenteeMatchesWithValidOrDefaultMentor(rawResult), 
            buildDefaultMatchesForUnassignedMentors(rawResult));
        return new Matches<>(concatenation.collect(Collectors.toList()));
    }
    
    private Matches<Mentee, Mentor> filterAndFormatValidMatches(Result rawResult){
        List<Integer> rowAssignments = rawResult.getRowAssignments();
        return new Matches<>(IntStream.range(0, rowAssignments.size())
            .filter(i -> isValidMatch(i, rowAssignments.get(i)))
            .mapToObj(i -> buildMatch(i, rowAssignments.get(i)))
            .collect(Collectors.toList())
        );
    }
    
    private boolean isValidMatch(Integer menteeIndex, Integer mentorIndex){
        return (menteeIndex != unassignedValue 
                && mentorIndex != unassignedValue 
                && costMatrixHandler.isMatchScoreNotProhibitive(menteeIndex, mentorIndex));
    }
    
    private Stream<Match<Mentee, Mentor>> 
        buildMenteeMatchesWithValidOrDefaultMentor(Result rawResult){
        List<Integer> rowAssignments = rawResult.getRowAssignments();
        return IntStream.range(0, rowAssignments.size())
            .mapToObj(i -> buildMatchWithValidOrDefaultMentor(i, rowAssignments.get(i)));
    }
    
    private Stream<Match<Mentee,Mentor>> buildDefaultMatchesForUnassignedMentors(Result rawResult){
        List<Integer> colAssignments = rawResult.getColumnAssignments();
        return IntStream.range(0, colAssignments.size())
            .filter(j -> ! isValidMatch(colAssignments.get(j), j))
            .mapToObj(this::buildDefaultMentorMatch);
    }
    
    private Match<Mentee, Mentor> buildMatchWithValidOrDefaultMentor(int menteeIndex, int mentorIndex){
        if (isValidMatch(menteeIndex, mentorIndex)){
            return buildMatch(menteeIndex, mentorIndex);
        } else {
            return buildDefaultMenteeMatch(menteeIndex);
        }
    }
    
    private Match<Mentee, Mentor> buildMatch(int menteeIndex, int mentorIndex){
        return buildMatch(mentees.get(menteeIndex),
                mentors.get(mentorIndex),
                costMatrixHandler.getMatchScore(menteeIndex, mentorIndex));
    }
    
    private Match<Mentee, Mentor> buildDefaultMenteeMatch(int menteeIndex){
        return buildMatch(mentees.get(menteeIndex), defaultMentor, PROHIBITIVE_VALUE);
    }
    
    private Match<Mentee, Mentor> buildDefaultMentorMatch(int mentorIndex){
        return buildMatch(defaultMentee, mentors.get(mentorIndex), PROHIBITIVE_VALUE);
    }
    
    private Match<Mentee, Mentor> buildMatch(Mentee mentee, Mentor mentor, int cost){
        return new Match<>(mentee, mentor, cost);
    }
}