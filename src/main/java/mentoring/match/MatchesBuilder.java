package mentoring.match;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import assignmentproblem.hungariansolver.HungarianSolver;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MatchesBuilder<Mentee, Mentor> {
    //TODO extract CostMatrixBuilder from class: isolate cost matrix and criteria, and solve it
    //TODO add tests
    final private List<Mentee> mentees;
    final private List<Mentor> mentors;
    final private Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria;
    private List<ProhibitiveCriterion<Mentee, Mentor>> prohibitiveCriteria;
   /** Cell [i][j] is the cost of associating mentee i with mentor j. */
    private int[][] costMatrix;
    private static final int PROHIBITIVE_VALUE = Integer.MAX_VALUE;
    private Integer unassignedValue = null;
    private Solver solver = new HungarianSolver(unassignedValue);
    
    public MatchesBuilder(List<Mentee> mentees, List<Mentor> mentors,
            Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria){
        this.mentors = mentors;
        this.mentees = mentees;
        this.progressiveCriteria = progressiveCriteria;
    }
    
    public MatchesBuilder<Mentee, Mentor> withProhibitiveCriteria(
            List<ProhibitiveCriterion<Mentee, Mentor>> prohibitiveCriteria){
        this.prohibitiveCriteria = prohibitiveCriteria;
        return this;
    }
    
    public MatchesBuilder<Mentee, Mentor> withSolver(Solver solver, Integer unassignedValue){
        this.solver = solver;
        this.unassignedValue = unassignedValue;
        return this;
    }
    
    public Matches<Mentee, Mentor> build(){
        buildCostMatrix();
        Result rawResult = solver.solve(costMatrix);
        return filterAndFormatResult(rawResult);
    }
    
    private void buildCostMatrix(){
        costMatrix = new int[mentees.size()][mentors.size()];
        for(int i = 0; i < mentees.size(); i++){
            for (int j = 0; j < mentors.size(); j++){
                costMatrix[i][j] = computeCost(mentees.get(i), mentors.get(j));
            }
        }
    }
    
    private Matches<Mentee, Mentor> filterAndFormatResult(Result rawResult){
        List<Integer> rowAssignments = rawResult.getRowAssignments();
        return new Matches<>(IntStream.range(0, rowAssignments.size())
            .filter(i -> isValidMatch(i, rowAssignments.get(i)))
            .mapToObj(i -> buildMatch(i, rowAssignments.get(i)))
            .collect(Collectors.toList())
        );
    }
    
    private int computeCost(Mentee mentee, Mentor mentor){
        if (checkProhibitiveCriteria(mentee, mentor)){
            return computeProgressiveCriteriaCost(mentee, mentor);
        } else {
            return PROHIBITIVE_VALUE;
        }
    }
    
    private boolean isValidMatch(int menteeIndex, int mentorIndex){
        return (menteeIndex != unassignedValue 
                && mentorIndex != unassignedValue 
                && costMatrix[menteeIndex][mentorIndex] < PROHIBITIVE_VALUE);
    }
    
    private Match<Mentee, Mentor> buildMatch(int menteeIndex, int mentorIndex){
        return new Match<>(mentees.get(menteeIndex),
                mentors.get(mentorIndex),
                costMatrix[menteeIndex][mentorIndex]);
    }
    
    private boolean checkProhibitiveCriteria(Mentee mentee, Mentor mentor){
        for (ProhibitiveCriterion<Mentee, Mentor> criterion : prohibitiveCriteria){
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
