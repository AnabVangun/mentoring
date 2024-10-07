package mentoring.match;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
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
    
    private enum ForbiddenCode {SPECIFICALLY_FORBIDDEN, FORBIDDEN_BY_CRITERIA}
    private final List<List<Set<ForbiddenCode>>> forbiddenMatches;
    
    CostMatrixHandler(List<Mentee> mentees, List<Mentor> mentors,
            Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria){
        this.mentors = mentors;
        menteeIndices = IntStream.range(0, mentees.size()).boxed().collect(Collectors.toList());
        this.mentees = mentees;
        mentorIndices = IntStream.range(0, mentors.size()).boxed().collect(Collectors.toList());
        this.progressiveCriteria = progressiveCriteria;
        costMatrix = new int[mentees.size()][mentors.size()];
        buildCostMatrix();
        forbiddenMatches = buildForbiddenCodeMatrix(mentees.size(), mentors.size());
    }
    
    private static List<List<Set<ForbiddenCode>>> buildForbiddenCodeMatrix(int nRows, 
            int nColumns){
        List<List<Set<ForbiddenCode>>> result = new ArrayList<>(nRows);
        for(int i = 0; i < nRows; i++){
            List<Set<ForbiddenCode>> line = new ArrayList<>(nColumns);
            result.add(line);
            for (int j = 0; j < nColumns; j++){
                line.add(EnumSet.noneOf(ForbiddenCode.class));
            }
        }
        return result;
    }
    
    CostMatrixHandler<Mentee, Mentor> withNecessaryCriteria(
            Collection<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria){
        this.necessaryCriteria = necessaryCriteria;
        for (int i : menteeIndices){
            for (int j : mentorIndices){
                boolean allowed = checkNecessaryCriteria(mentees.get(i), mentors.get(j));
                Set<ForbiddenCode> status = forbiddenMatches.get(i).get(j);
                setOrRemoveForbiddenByCriteriaCode(allowed, status);
            }
        }
        return this;
    }
    
    private static void setOrRemoveForbiddenByCriteriaCode(boolean allowedByCriteria, 
        Set<ForbiddenCode> status){
            if(allowedByCriteria && status.contains(ForbiddenCode.FORBIDDEN_BY_CRITERIA)){
                status.remove(ForbiddenCode.FORBIDDEN_BY_CRITERIA);
            } else if (!allowedByCriteria && !status.contains(ForbiddenCode.FORBIDDEN_BY_CRITERIA)){
                status.add(ForbiddenCode.FORBIDDEN_BY_CRITERIA);
            }
    }
    
    boolean forbidMatch(int menteeIndex, int mentorIndex){
        return forbiddenMatches.get(menteeIndex).get(mentorIndex)
                .add(ForbiddenCode.SPECIFICALLY_FORBIDDEN);
    }
    
    boolean allowMatch(int menteeIndex, int mentorIndex){
        return forbiddenMatches.get(menteeIndex).get(mentorIndex)
                .remove(ForbiddenCode.SPECIFICALLY_FORBIDDEN);
    }
    
    private CostMatrixHandler<Mentee, Mentor> buildCostMatrix(){
        for (int i : menteeIndices){
            for (int j : mentorIndices){
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
        return forbiddenMatches.get(menteeIndex).get(mentorIndex).isEmpty();
    }
    
    int getMatchScore(int menteeIndex, int mentorIndex){
        return costMatrix[menteeIndex][mentorIndex];
    }
    
    void clearSpecificallyForbiddenMatches(){
        for(int i: menteeIndices){
            for (int j: mentorIndices){
                forbiddenMatches.get(i).get(j).remove(ForbiddenCode.SPECIFICALLY_FORBIDDEN);
            }
        }
    }
}
