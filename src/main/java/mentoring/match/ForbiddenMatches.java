package mentoring.match;

import java.util.HashMap;
import java.util.HashSet;
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
    //TODO consider adding a "applyFromState" method that only applies the most recent updates
    private final Map<Mentee, Set<Mentor>> menteesToMentors = new HashMap<>();
    private final Function<Mentee, Set<Mentor>> setBuilder = m -> new HashSet<>();
    
    /**
     * Forbid a match between a mentee and a mentor.
     * @param mentee that MUST NOT be matched with the mentor
     * @param mentor that MUST NOT be matched with the mentee
     * @return {@code true} if the match has been forbidden
     */
    public synchronized boolean forbidMatch(Mentee mentee, Mentor mentor){
        Set<Mentor> forbiddenMentors = menteesToMentors.computeIfAbsent(mentee, setBuilder);
        return forbiddenMentors.add(mentor);
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
        }
        return result;
    }
    
    /**
     * Applies all the forbidden matches to the input cost matrix.
     * @param costMatrix that must take the forbidden matches into account
     * @param menteeIndexGetter function returning the index in the cost matrix of each mentee 
     *      involved in a forbidden match
     * @param mentorIndexGetter function returning the index in the cost matrix of each mentor
     *      involved in a forbidden match
     */
    synchronized void apply(CostMatrixHandler costMatrix, ToIntFunction<Mentee> menteeIndexGetter,
            ToIntFunction<Mentor> mentorIndexGetter){
        for(Map.Entry<Mentee, Set<Mentor>> entry : menteesToMentors.entrySet()){
            int menteeIndex = menteeIndexGetter.applyAsInt(entry.getKey());
            for (Mentor mentor : entry.getValue()){
                costMatrix.forbidMatch(menteeIndex, mentorIndexGetter.applyAsInt(mentor));
            }
        }
    }
}
