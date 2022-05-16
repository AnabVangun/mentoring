package mentoring.match;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import assignmentproblem.hungariansolver.HungarianSolver;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MatchesBuilder<Mentee, Mentor> {
    //TODO add tests
    final private List<Mentee> mentees;
    final private List<Mentor> mentors;
    final private Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria;
    private List<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria;
   /** Cell [i][j] is the cost of associating mentee i with mentor j. */
    private int[][] costMatrix;
    public static final int PROHIBITIVE_VALUE = Integer.MAX_VALUE;
    private Integer unassignedValue = null;
    private Solver solver = new HungarianSolver(unassignedValue);
    private Mentee defaultMentee;
    private Mentor defaultMentor;
    private boolean hasPlaceholderPersons = false;
    
    public MatchesBuilder(List<Mentee> mentees, List<Mentor> mentors,
            Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria){
        this.mentors = mentors;
        this.mentees = mentees;
        this.progressiveCriteria = progressiveCriteria;
    }
    
    public MatchesBuilder<Mentee, Mentor> withNecessaryCriteria(
            List<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria){
        this.necessaryCriteria = necessaryCriteria;
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
        buildCostMatrix();
        Result rawResult = solver.solve(costMatrix);
        return formatResult(rawResult);
    }
    
    private void buildCostMatrix(){
        costMatrix = new int[mentees.size()][mentors.size()];
        for(int i = 0; i < mentees.size(); i++){
            for (int j = 0; j < mentors.size(); j++){
                costMatrix[i][j] = computeCost(mentees.get(i), mentors.get(j));
            }
        }
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
    
    private int computeCost(Mentee mentee, Mentor mentor){
        if (checkNecessaryCriteria(mentee, mentor)){
            return computeProgressiveCriteriaCost(mentee, mentor);
        } else {
            return PROHIBITIVE_VALUE;
        }
    }
    
    private boolean isValidMatch(Integer menteeIndex, Integer mentorIndex){
        return (menteeIndex != unassignedValue 
                && mentorIndex != unassignedValue 
                && costMatrix[menteeIndex][mentorIndex] < PROHIBITIVE_VALUE);
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
                costMatrix[menteeIndex][mentorIndex]);
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
    
    private boolean checkNecessaryCriteria(Mentee mentee, Mentor mentor){
        for (NecessaryCriterion<Mentee, Mentor> criterion : necessaryCriteria){
            if (!criterion.test(mentee, mentor)){
                return false;
            }
        }
        return true;
    }
    
    private int computeProgressiveCriteriaCost(Mentee mentee, Mentor mentor){
        int result = 0;
        for (ProgressiveCriterion<Mentee, Mentor> criterion : progressiveCriteria){
            int tmp = criterion.applyAsInt(mentee, mentor);
            if (tmp < 0){
                throw new IllegalStateException("Score of criterion " + criterion + " for " +
                    mentee + " and " + mentor + " is " + tmp + ", below 0.");
            }
            result += tmp;
            if (result < 0){
                throw new IllegalStateException("Score for " + mentee + " and " + mentor + 
                    " overflows, reached " + result + " after criterion " + criterion);
            }
        }
        return result;
    }   
}