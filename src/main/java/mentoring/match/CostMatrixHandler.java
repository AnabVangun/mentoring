package mentoring.match;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import java.util.Collection;
import java.util.List;

final class CostMatrixHandler<Mentee, Mentor> {
    final private List<Mentee> mentees;
    final private List<Mentor> mentors;
    final private Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria;
    private Collection<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria = List.of();
    private int[][] costMatrix;
    
    CostMatrixHandler(List<Mentee> mentees, List<Mentor> mentors,
            Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria){
        this.mentors = mentors;
        this.mentees = mentees;
        this.progressiveCriteria = progressiveCriteria;
    }
    
    CostMatrixHandler<Mentee, Mentor> withNecessaryCriteria(
            Collection<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria){
        this.necessaryCriteria = necessaryCriteria;
        return this;
    }
    
    CostMatrixHandler buildCostMatrix(){
        /** Cell [i][j] is the cost of associating mentee i with mentor j. */
        costMatrix = new int[mentees.size()][mentors.size()];
        for(int i = 0; i < mentees.size(); i++){
            for (int j = 0; j < mentors.size(); j++){
                costMatrix[i][j] = computeCost(mentees.get(i), mentors.get(j));
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
        return solver.solve(costMatrix);
    }
    
    boolean isMatchScoreNotProhibitive(int menteeIndex, int mentorIndex){
        return costMatrix[menteeIndex][mentorIndex] < MatchesBuilder.PROHIBITIVE_VALUE;
    }
    
    int getMatchScore(int menteeIndex, int mentorIndex){
        return costMatrix[menteeIndex][mentorIndex];
    }
}
