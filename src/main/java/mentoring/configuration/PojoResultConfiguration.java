package mentoring.configuration;

import java.util.function.Function;
import mentoring.datastructure.Person;
import mentoring.match.Match;

//TODO document this class
//TODO test this class
public enum PojoResultConfiguration implements ResultConfiguration<Person, Person> {
    NAMES_AND_SCORE(new String[]{"Mentoré", "Mentor", "Coût"},
        match -> new String[]{match.getMentee().getFullName(), 
            match.getMentor().getFullName(), Integer.toString(match.getCost())}),
    NAMES_EMAILS_AND_SCORE(new String[]{
        "Mentoré", "email mentoré", "Mentor", "email mentor", "Coût"},
        match -> new String[]{match.getMentee().getFullName(),
            match.getMentee().getPropertyAs("Email", String.class),
            match.getMentor().getFullName(),
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
