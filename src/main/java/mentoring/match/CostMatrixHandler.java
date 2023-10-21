package mentoring.match;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

final class CostMatrixHandler<Mentee, Mentor> {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<Mentee> mentees;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<Mentor> mentors;
    private final Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria;
    private Collection<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria = List.of();
    /** Cell [i][j] is the cost of associating mentee i with mentor j. */
    private final int[][] costMatrix;
    private final boolean[][] allowedMatch;
    
    CostMatrixHandler(List<Mentee> mentees, List<Mentor> mentors,
            Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria){
        this.mentors = mentors;
        this.mentees = mentees;
        this.progressiveCriteria = progressiveCriteria;
        costMatrix = new int[mentees.size()][mentors.size()];
        buildCostMatrix();
        allowedMatch = new boolean[mentees.size()][];
        for(int i = 0 ; i < allowedMatch.length ; i++){
            boolean[] allTrue = new boolean[mentors.size()];
            Arrays.fill(allTrue, true);
            allowedMatch[i] = allTrue;
        }
    }
    
    CostMatrixHandler<Mentee, Mentor> withNecessaryCriteria(
            Collection<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria){
        this.necessaryCriteria = necessaryCriteria;
        for (int i = 0; i < mentees.size(); i++){
            for (int j = 0; j < mentors.size(); j++){
                allowedMatch[i][j] = checkNecessaryCriteria(mentees.get(i), mentors.get(j));
            }
        }
        return this;
    }
    
    private CostMatrixHandler<Mentee, Mentor> buildCostMatrix(){
        for(int i = 0; i < mentees.size(); i++){
            for (int j = 0; j < mentors.size(); j++){
                costMatrix[i][j] = computeProgressiveCriteriaCost(mentees.get(i), mentors.get(j));
            }
        }
        return this;
    }
    
    private int computeCost(Mentee mentee, Mentor mentor){
        if (checkNecessaryCriteria(mentee, mentor)){
            return computeProgressiveCriteriaCost(mentee, mentor);
        } else {
            return MatchesBuilder.PROHIBITIVE_VALUE;
        }
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
    
    Result solveCostMatrix(Solver solver){
        int[][] actualCostMatrix = computeActualCostMatrix();
        return solver.solve(actualCostMatrix);
    }
    
    private int[][] computeActualCostMatrix(){
        int[][] result = new int[costMatrix.length][costMatrix[0].length];
        for (int i = 0; i < result.length; i++){
            for (int j = 0; j < result[i].length; j++){
                result[i][j] = computeCost(mentees.get(i), mentors.get(j));
            }
        }
        return result;
    }
    
    boolean isMatchAllowed(int menteeIndex, int mentorIndex){
        return allowedMatch[menteeIndex][mentorIndex];
    }
    
    int getMatchScore(int menteeIndex, int mentorIndex){
        return costMatrix[menteeIndex][mentorIndex];
    }
}
