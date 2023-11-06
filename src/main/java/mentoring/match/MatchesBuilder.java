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
 * <p>MatchesBuilder is not thread-safe.
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
     * Forbids matches between the input mentee and mentor.
     * @param mentee that cannot be matched with the mentor
     * @param mentor that cannot be matched with the mentee
     * @return true if the match was allowed and has been forbidden, false if it was already 
     * forbidden
     */
    public boolean forbidMatch(Mentee mentee, Mentor mentor){
        return costMatrixHandler.forbidMatch(getMenteeIndex(mentee), getMentorIndex(mentor));
    }
    
    /**
     * Allows matches between the input mentee and mentor.
     * @param mentee that can be matched with the mentor
     * @param mentor that can be matched with the mentee
     * @return true if the match was forbidden and has been allowed, false if it was already 
     * allowed
     */
    public boolean allowMatch(Mentee mentee, Mentor mentor){
        return costMatrixHandler.allowMatch(getMenteeIndex(mentee), getMentorIndex(mentor));
    }
    
    private int getMenteeIndex(Mentee mentee){
        return getIndex(mentee, mentees, "mentee");
    }
    
    private int getMentorIndex(Mentor mentor){
        return getIndex(mentor, mentors, "mentor");
    }
    
    private int getIndex(Object object, List<?> list, String typeOfObject){
        int result = list.indexOf(object);
        if(result == -1){
            throw new IllegalArgumentException("Unknown %s %s".formatted(typeOfObject, object));
        }
        return result;
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
     * Solves the assignment problem instance and returns the result.
     * @return an optimal assignment between the mentees and the mentors.
     */
    public Matches<Mentee, Mentor> build(){
        Result rawResult = costMatrixHandler.solveCostMatrix(solver);
        return formatResult(rawResult,
                IntStream.range(0, mentees.size()).boxed().collect(Collectors.toList()),
                IntStream.range(0, mentors.size()).boxed().collect(Collectors.toList()));
    }
    
    /**
     * Solves the assignment problem instance and returns the result.
     * @param mentees subset of mentees on which to solve the problem
     * @param mentors subset of mentors on which to solve the problem
     * @return an optimal assignment between the input mentees and mentors.
     * @throws IllegalArgumentException if the mentees and mentors are not subsets of those known
     *      by this MatchesBuilder.
     */
    public Matches<Mentee, Mentor> build(List<Mentee> mentees, List<Mentor> mentors) 
            throws IllegalArgumentException{
        List<Integer> menteeIndices = getIndices(mentees, this.mentees, "mentee");
        List<Integer> mentorIndices = getIndices(mentors, this.mentors, "mentor");
        Result rawResult = costMatrixHandler.solvePartialCostMatrix(solver, menteeIndices, 
                mentorIndices);
        return formatResult(rawResult, menteeIndices, mentorIndices);
    }
    
    private <T> List<Integer> getIndices(List<T> sublist, List<T> superlist, String typeOfObject){
        return sublist.stream().map(element -> getIndex(element, superlist, typeOfObject))
                .collect(Collectors.toList());
    }
    
    private Matches<Mentee, Mentor> formatResult(Result rawResult, 
            List<Integer> menteeIndices, List<Integer> mentorIndices){
        if (this.hasPlaceholderPersons){
            return formatMatchesWithPlaceholders(rawResult, menteeIndices, mentorIndices);
        } else {
            return filterAndFormatValidMatches(rawResult, menteeIndices, mentorIndices);
        }
    }
    
    private Matches<Mentee, Mentor> formatMatchesWithPlaceholders(Result rawResult, 
            List<Integer> menteeIndices, List<Integer> mentorIndices){
        List<Match<Mentee, Mentor>> matches = buildMenteeMatchesWithValidOrDefaultMentor(rawResult,
                menteeIndices, mentorIndices);
        matches.addAll(buildDefaultMatchesForUnassignedMentors(rawResult, 
                menteeIndices, mentorIndices));
        return new Matches<>(matches);
    }
    
    private Matches<Mentee, Mentor> filterAndFormatValidMatches(Result rawResult, 
            List<Integer> menteeIndices, List<Integer> mentorIndices){
        List<Integer> rowAssignments = rawResult.getRowAssignments();
        return new Matches<>(IntStream.range(0, rowAssignments.size())
            .filter(i -> isValidMatch(i,rowAssignments.get(i), menteeIndices, mentorIndices))
            .mapToObj(i -> buildMatch(menteeIndices.get(i), 
                    mentorIndices.get(rowAssignments.get(i))))
            .collect(Collectors.toList())
        );
    }
    
    private boolean isValidMatch(Integer menteeIndex, Integer mentorIndex, 
            List<Integer> menteeIndices, List<Integer> mentorIndices){
        return (menteeIndex != unassignedValue 
                && mentorIndex != unassignedValue 
                && costMatrixHandler.isMatchAllowed(menteeIndices.get(menteeIndex), 
                        mentorIndices.get(mentorIndex)));
    }
    
    private List<Match<Mentee, Mentor>> 
            buildMenteeMatchesWithValidOrDefaultMentor(Result rawResult, 
            List<Integer> menteeIndices, List<Integer> mentorIndices){
        List<Integer> rowAssignments = rawResult.getRowAssignments();
        List<Match<Mentee, Mentor>> result = new ArrayList<>(rowAssignments.size());
        for (int i = 0; i < rowAssignments.size(); i++){
            result.add(buildMatchWithValidOrDefaultMentor(i, rowAssignments.get(i),
                    menteeIndices, mentorIndices));
        }
        return result;
    }
    
    private List<Match<Mentee,Mentor>> buildDefaultMatchesForUnassignedMentors(Result rawResult,
            List<Integer> menteeIndices, List<Integer> mentorIndices){
        List<Integer> colAssignments = rawResult.getColumnAssignments();
        List<Match<Mentee,Mentor>> result = new ArrayList<>();
        for (int j = 0; j < colAssignments.size(); j++){
            if (!isValidMatch(colAssignments.get(j),j, menteeIndices, mentorIndices)){
                result.add(buildDefaultMentorMatch(mentorIndices.get(j)));
            }
        }
        return result;
    }
    
    private Match<Mentee, Mentor> buildMatchWithValidOrDefaultMentor(int menteeIndex, 
            Integer mentorIndex, List<Integer> menteeIndices, List<Integer> mentorIndices){
        if (isValidMatch(menteeIndex, mentorIndex, menteeIndices, mentorIndices)){
            return buildMatch(menteeIndices.get(menteeIndex), mentorIndices.get(mentorIndex));
        } else {
            return buildDefaultMenteeMatch(menteeIndices.get(menteeIndex));
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
    
    /**
     * Builds a single Match object between the mentee and the mentor.
     * @param mentee to include in the match
     * @param mentor to include in the match
     * @return a Match object between the mentee and the mentor with the appropriate cost
     * @throws IllegalArgumentException when the mentee and/or the mentor are unknown
     */
    public Match<Mentee, Mentor> buildSingleMatch(Mentee mentee, Mentor mentor) 
            throws IllegalArgumentException{
        int menteeIndex = mentees.indexOf(mentee);
        int mentorIndex = mentors.indexOf(mentor);
        if (menteeIndex == -1) {
            if (mentorIndex == -1) {
                throw new IllegalArgumentException("Mentee %s and mentor %s are invalid"
                        .formatted(mentee, mentor));
            } else {
                throw new IllegalArgumentException("Mentee %s is invalid".formatted(mentee));
            }
        } else if (mentorIndex == -1) {
            throw new IllegalArgumentException("Mentor %s are invalid".formatted(mentor));
        }
        int cost = costMatrixHandler.isMatchAllowed(menteeIndex, mentorIndex) 
                ? costMatrixHandler.getMatchScore(menteeIndex, mentorIndex)
                : PROHIBITIVE_VALUE;
        return new Match<>(mentee, mentor, cost);
    }
}