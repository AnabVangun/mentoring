package mentoring.configuration;

import java.util.function.Function;
import mentoring.datastructure.Person;
import mentoring.match.Match;

/**
 * Example result-writing configurations for test cases.
 */
public enum PojoResultConfiguration implements ResultConfiguration<Person, Person> {
    /** Writes the names and the total cost of a pair. */
    NAMES_AND_SCORE(new String[]{"Mentoré", "Mentor", "Coût"},
            match -> new String[]{match.getMentee().getFullName(),
                match.getMentor().getFullName(), Integer.toString(match.getCost())}),
    /** Writes the names, e-mail addresses and the total cost of a pair. */
    NAMES_EMAILS_AND_SCORE(new String[]{
        "Mentoré", "email mentoré", "Mentor", "email mentor", "Coût"},
            match -> new String[]{match.getMentee().getFullName(),
                match.getMentee().getPropertyAs("Email", String.class),
                match.getMentor().getFullName(),
                match.getMentor().getPropertyAs("Email", String.class),
                Integer.toString(match.getCost())}),
    NAMES_EMAILS_DUPLICATE_AND_SCORE(new String[]{
        "Mentor", "email mentor", "Mentoré", "email mentoré", "email mentor bis", "Coût"},
            match -> new String[]{match.getMentor().getFullName(),
                match.getMentor().getPropertyAs("Email", String.class),
                match.getMentee().getFullName(),
                match.getMentee().getPropertyAs("Email", String.class),
                match.getMentor().getPropertyAs("Email", String.class),
                Integer.toString(match.getCost())});
    
    private final String[] header;
    private final Function<Match<Person, Person>, String[]> lineFormater;
    
    private PojoResultConfiguration(String[] header, 
        Function<Match<Person, Person>, String[]> lineFormater){
        this.header = header;
        this.lineFormater = lineFormater;
    }
    @Override
    public String[] getResultHeader() {
        return header.clone();
    }

    @Override
    public String[] getResultLine(Match<Person, Person> match) {
        return lineFormater.apply(match);
    }
    
}
