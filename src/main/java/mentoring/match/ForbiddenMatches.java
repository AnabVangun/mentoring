package mentoring.match;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Structure used to keep track of matches that are forbidden.
 * 
 * <p>ForbiddenMatches is thread-safe.
 * @param <Mentee> class representing an individual mentee
 * @param <Mentor> class representing an individual mentor
 */
public final class ForbiddenMatches<Mentee, Mentor> {
    private final Map<Mentee, Set<Mentor>> menteesToMentors = new HashMap<>();
    private final Function<Mentee, Set<Mentor>> setBuilder = m -> new HashSet<>();
    private final List<Action<Mentee, Mentor>> actions = new LinkedList<>();
    
    /**
     * Forbid a match between a mentee and a mentor.
     * @param mentee that MUST NOT be matched with the mentor
     * @param mentor that MUST NOT be matched with the mentee
     * @return {@code true} if the match has been forbidden
     */
    public synchronized boolean forbidMatch(Mentee mentee, Mentor mentor){
        Set<Mentor> forbiddenMentors = menteesToMentors.computeIfAbsent(mentee, setBuilder);
        boolean result = forbiddenMentors.add(mentor);
        if (result){
            actions.add(new ForbidAction<>(mentee, mentor));
        }
        return result;
    }
    
    /**
     * Allow a match between a mentee and a mentor.
     * @param mentee that MAY be matched with the mentor
     * @param mentor that MAY be matched with the mentee
     * @return {@code true} if the match has been allowed
     */
    public synchronized boolean allowMatch(Mentee mentee, Mentor mentor){
        boolean result = false;
        if (menteesToMentors.containsKey(mentee)){
            Set<Mentor> forbiddenMentors = menteesToMentors.get(mentee);
            result = forbiddenMentors.remove(mentor);
            if (result && forbiddenMentors.isEmpty()){
                menteesToMentors.remove(mentee);
            }
            if (result){
                actions.add(new AllowAction<>(mentee, mentor));
            }
        }
        return result;
    }
    
    /**
     * Removes all forbidden matches.
     */
    public synchronized void clear(){
        for(Map.Entry<Mentee, Set<Mentor>> entry : menteesToMentors.entrySet()){
            Mentee mentee = entry.getKey();
            for (Mentor mentor : entry.getValue()){
                actions.add(new AllowAction<>(mentee, mentor));
            }
        }
        menteesToMentors.clear();
    }
    
    /**
     * Applies all the forbidden matches to the input cost matrix.
     * @param costMatrix that must take the forbidden matches into account
     * @param menteeIndexGetter function returning the index in the cost matrix of each mentee 
     *      involved in a forbidden match
     * @param mentorIndexGetter function returning the index in the cost matrix of each mentor
     *      involved in a forbidden match
     */
    synchronized void apply(CostMatrixHandler<Mentee, Mentor> costMatrix, 
            ToIntFunction<Mentee> menteeIndexGetter,
            ToIntFunction<Mentor> mentorIndexGetter){
        costMatrix.clearSpecificallyForbiddenMatches();
        for(Map.Entry<Mentee, Set<Mentor>> entry : menteesToMentors.entrySet()){
            int menteeIndex = menteeIndexGetter.applyAsInt(entry.getKey());
            for (Mentor mentor : entry.getValue()){
                costMatrix.forbidMatch(menteeIndex, mentorIndexGetter.applyAsInt(mentor));
            }
        }
        actions.clear();
    }
    
    /**
     * Applies the last forbidden matches to the input cost matrix. This method should only be used
     * to update a single {@link CostMatrixHandler} at a time, and that object should first be 
     * initialised with 
     * {@link #apply(mentoring.match.CostMatrixHandler, java.util.function.ToIntFunction, java.util.function.ToIntFunction) }
     * @param costMatrix that must take the forbidden matches into account
     * @param menteeIndexGetter function returning the index in the cost matrix of each mentee 
     *      involved in a forbidden match
     * @param mentorIndexGetter function returning the index in the cost matrix of each mentor
     *      involved in a forbidden match
     */
    synchronized void applyFromLastState(CostMatrixHandler<Mentee, Mentor> costMatrix, 
            ToIntFunction<Mentee> menteeIndexGetter, ToIntFunction<Mentor> mentorIndexGetter){
        for (Action<Mentee, Mentor> action : actions){
            action.apply(costMatrix, menteeIndexGetter, mentorIndexGetter);
        }
        actions.clear();
    }
    
    private static abstract class Action<Mentee, Mentor> {
        final Mentee mentee;
        final Mentor mentor;
        
        Action(Mentee mentee, Mentor mentor){
            this.mentee = mentee;
            this.mentor = mentor;
        }
        
        abstract void apply(CostMatrixHandler<Mentee, Mentor> costMatrix,
                ToIntFunction<Mentee> menteeIndexGetter, ToIntFunction<Mentor> mentorIndexGetter);
    }
    
    private static class ForbidAction<Mentee, Mentor> extends Action<Mentee, Mentor> {
        ForbidAction(Mentee mentee, Mentor mentor){
            super(mentee, mentor);
        }
        
        @Override
        void apply(CostMatrixHandler<Mentee, Mentor> costMatrix,
                ToIntFunction<Mentee> menteeIndexGetter, ToIntFunction<Mentor> mentorIndexGetter){
            costMatrix.forbidMatch(menteeIndexGetter.applyAsInt(mentee),
                    mentorIndexGetter.applyAsInt(mentor));
        }
    }
    
    private static class AllowAction<Mentee, Mentor> extends Action<Mentee, Mentor> {
        AllowAction(Mentee mentee, Mentor mentor){
            super(mentee, mentor);
        }
        
        @Override
        void apply(CostMatrixHandler<Mentee, Mentor> costMatrix,
                ToIntFunction<Mentee> menteeIndexGetter, ToIntFunction<Mentor> mentorIndexGetter){
            costMatrix.allowMatch(menteeIndexGetter.applyAsInt(mentee),
                    mentorIndexGetter.applyAsInt(mentor));
        }
    }
}
