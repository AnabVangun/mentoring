package mentoring.match;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Matches<Mentee, Mentor> implements Iterable<Match<Mentee, Mentor>>{
    private final Map<Mentee, Match<Mentee, Mentor>> menteeMatches = new HashMap<>();
    private final Map<Mentor, Match<Mentee, Mentor>> mentorMatches = new HashMap<>();
    private final Iterable<Match<Mentee, Mentor>> iterable;
    
    public Match<Mentee, Mentor> getMentorMatch(Mentor mentor){
        return mentorMatches.get(mentor);
    }
    public Match<Mentee, Mentor> getMenteeMatch(Mentee mentee){
        return menteeMatches.get(mentee);
    }
    
    public boolean isMentee(Mentee mentee){
        return menteeMatches.containsKey(mentee);
    }
    
    public boolean isMentor(Mentor mentor){
        return mentorMatches.containsKey(mentor);
    }
    
    Matches(Iterable<Match<Mentee, Mentor>> matches){
        for (Match<Mentee, Mentor> match:matches){
            mentorMatches.put(match.getMentor(), match);
            menteeMatches.put(match.getMentee(), match);
        }
        this.iterable = matches;
    }

    @Override
    public Iterator<Match<Mentee, Mentor>> iterator() {
        return iterable.iterator();
    }
}
