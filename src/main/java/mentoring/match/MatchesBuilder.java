package mentoring.match;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import assignmentproblem.hungariansolver.HungarianSolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/**
 * Builder used to match mentees and mentors. The only mandatory parameters to build a 
 * {@link Matches} instance are the arguments of the public constructor: the simplest use of this 
 * class is {@code new MatchesBuilder<>(...).build()}. All the other public methods are provided to 
 * tweak the behaviour of the {@link #build()} method and the content of its output.
 * 
 * <p>MatchesBuilder is neither thread-safe, nor designed for instance reuse: for each instance of 
 * {@link Matches} to build, a specific instance of MatchesBuilder should be used.
 * @param <Mentee> class representing an individual mentee
 * @param <Mentor> class representing an individual mentor
 */
public final class MatchesBuilder<Mentee, Mentor> {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    final private List<Mentee> mentees;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    final private List<Mentor> mentors;
    final CostMatrixHandler<Mentee, Mentor> costMatrixHandler;
    /**
     * A value such that all candidate matches with this cost will be considered invalid.
     */
    public static final int PROHIBITIVE_VALUE = Integer.MAX_VALUE;
    private Integer unassignedValue = null;
    private Solver solver = new HungarianSolver(unassignedValue);
    private Mentee defaultMentee;
    private Mentor defaultMentor;
    private boolean hasPlaceholderPersons = false;
    
    /**
     * Instantiates a default MatchesBuilder instance.
     * @param mentees List of the mentees awaiting a mentor
     * @param mentors List of the mentors awaiting a mentee
     * @param progressiveCriteria used to evaluate the cost of assigning each mentor to 
     * each mentee
     */
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
    
    /**
     * Sets the criteria deciding if an assignment can be viable. A pair (mentee, mentor) for which 
     * any necessary criterion is not met is considered not viable.
     * @param necessaryCriteria used to evaluate if it is possible to assign each mentor to 
     * each mentee
     * @return the same builder instance
     */
    public MatchesBuilder<Mentee, Mentor> withNecessaryCriteria(
            Collection<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria){
        costMatrixHandler.withNecessaryCriteria(necessaryCriteria);
        return this;
    }
    
    /**
     * Sets the solver used to find the optimal assignment. If this method is not called before 
     * {@link #build()}, a default solver is used.
     * @param solver used to solve the assignment problem defined by the mentees, the mentors and 
     * the different criteria
     * @param unassignedValue value used by the solver to signal that a row or a column is not 
     * assigned in the optimal solution
     * @return the same builder instance
     */
    public MatchesBuilder<Mentee, Mentor> withSolver(Solver solver, Integer unassignedValue){
        this.solver = solver;
        this.unassignedValue = unassignedValue;
        return this;
    }
    
    /**
     * Defines the default persons to assign to unassigned mentors and mentees. If this method is 
     * not called before {@link #build()}, unassigned mentors and mentees are not included in the 
     * resulting {@link Matches} instance. If it is called, unassigned mentors and mentees are 
     * included in the instance, each assigned to their respective default counterpart.
     * @param defaultMentee to assign to unassigned mentors
     * @param defaultMentor to assign to unassigned mentees
     * @return the same builder instance
     */
    public MatchesBuilder<Mentee, Mentor> withPlaceholderPersons(Mentee defaultMentee, 
            Mentor defaultMentor){
        this.hasPlaceholderPersons = true;
        this.defaultMentee = defaultMentee;
        this.defaultMentor = defaultMentor;
        return this;
    }
    
    /**
     * Finalises the assignment problem instance, solves it and returns the result.
     * @return an optimal assignment between the mentees and the mentors.
     */
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
        List<Match<Mentee, Mentor>> matches = buildMenteeMatchesWithValidOrDefaultMentor(rawResult);
        matches.addAll(buildDefaultMatchesForUnassignedMentors(rawResult));
        return new Matches<>(matches);
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
    
    private List<Match<Mentee, Mentor>> 
        buildMenteeMatchesWithValidOrDefaultMentor(Result rawResult){
        List<Integer> rowAssignments = rawResult.getRowAssignments();
        List<Match<Mentee, Mentor>> result = new ArrayList<>(rowAssignments.size());
        for (int i = 0; i < rowAssignments.size(); i++){
            result.add(buildMatchWithValidOrDefaultMentor(i, rowAssignments.get(i)));
        }
        return result;
    }
    
    private List<Match<Mentee,Mentor>> buildDefaultMatchesForUnassignedMentors(Result rawResult){
        List<Integer> colAssignments = rawResult.getColumnAssignments();
        List<Match<Mentee,Mentor>> result = new ArrayList<>();
        for (int j = 0; j < colAssignments.size(); j++){
            if (!isValidMatch(colAssignments.get(j),j)){
                result.add(buildDefaultMentorMatch(j));
            }
        }
        return result;
    }
    
    private Match<Mentee, Mentor> buildMatchWithValidOrDefaultMentor(int menteeIndex, 
            Integer mentorIndex){
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