package mentoring.match;

import java.util.Iterator;

public final class Matches<Mentee, Mentor> implements Iterable<Match<Mentee, Mentor>>{
    private final Iterable<Match<Mentee, Mentor>> iterable;
    
    public Matches(Iterable<Match<Mentee, Mentor>> matches){
        this.iterable = matches;
    }

    @Override
    public Iterator<Match<Mentee, Mentor>> iterator(){
        return iterable.iterator();
    }
}
