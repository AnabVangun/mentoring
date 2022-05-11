package mentoring.configuration;

import mentoring.match.Match;

public interface ResultConfiguration<Mentee, Mentor> {
    
    String[] getResultHeader();
    
    String[] getResultLine(Match<Mentee, Mentor> match);
}
