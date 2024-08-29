package mentoring.match;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class CostMatrixHandler<Mentee, Mentor> {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<Mentee> mentees;
    private final List<Integer> menteeIndices;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<Mentor> mentors;
    private final List<Integer> mentorIndices;
    private final Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria;
    private Collection<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria = List.of();
    /** Cell [i][j] is the cost of associating mentee i with mentor j. */
    private final int[][] costMatrix;
    private final boolean[][] allowedMatch;
    
    CostMatrixHandler(List<Mentee> mentees, List<Mentor> mentors,
            Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria){
        this.mentors = mentors;
        menteeIndices = IntStream.range(0, mentees.size()).boxed().collect(Collectors.toList());
        this.mentees = mentees;
        mentorIndices = IntStream.range(0, mentors.size()).boxed().collect(Collectors.toList());
        this.progressiveCriteria = progressiveCriteria;
        costMatrix = new int[mentees.size()][mentors.size()];
        buildCostMatrix();
        allowedMatch = buildAllTrueMatrix(mentees.size(), mentors.size());
    }
    
    private boolean[][] buildAllTrueMatrix(int nRows, int nColumns){
        boolean[][] result = new boolean[nRows][];
        for (int i = 0; i < nRows ; i++){
            boolean[] allTrue = new boolean[nColumns];
            Arrays.fill(allTrue, true);
            result[i] = allTrue;
        }
        return result;
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
    
    boolean forbidMatch(int menteeIndex, int mentorIndex){
        if (isMatchAllowed(menteeIndex, mentorIndex)){
            allowedMatch[menteeIndex][mentorIndex] = false;
            return true;
        } else {
            return false;
        }
    }
    
    boolean allowMatch(int menteeIndex, int mentorIndex){
        if (!isMatchAllowed(menteeIndex, mentorIndex)){
            allowedMatch[menteeIndex][mentorIndex] = true;
            return true;
        } else {
            return false;
        }
    }
    
    private CostMatrixHandler<Mentee, Mentor> buildCostMatrix(){
        for(int i = 0; i < mentees.size(); i++){
            for (int j = 0; j < mentors.size(); j++){
                costMatrix[i][j] = computeProgressiveCriteriaCost(mentees.get(i), mentors.get(j));
            }
        }
        return this;
    }
    
    private int computeCost(int menteeIndex, int mentorIndex){
        if (isMatchAllowed(menteeIndex, mentorIndex)){
            return computeProgressiveCriteriaCost(mentees.get(menteeIndex), 
                    mentors.get(mentorIndex));
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
        return solvePartialCostMatrix(solver, menteeIndices, mentorIndices);
    }
    
    Result solvePartialCostMatrix(Solver solver, List<Integer> menteeIndices,
            List<Integer> mentorIndices){
        int[][] actualCostMatrix = computeActualCostMatrix(menteeIndices, mentorIndices);
        return solver.solve(actualCostMatrix);
    }
    
    private int[][] computeActualCostMatrix(List<Integer> menteeIndices, 
            List<Integer> mentorIndices){
        int[][] result = new int[menteeIndices.size()][mentorIndices.size()];
        for (int i = 0; i < result.length; i++){
            for (int j = 0; j < result[i].length; j++){
                result[i][j] = computeCost(menteeIndices.get(i), mentorIndices.get(j));
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
