package mentoring.configuration;

import mentoring.match.Match;
//TODO document this interface
public interface ResultConfiguration<Mentee, Mentor> {
    
    String[] getResultHeader();
    
    String[] getResultLine(Match<Mentee, Mentor> match);
}
