package mentoring.configuration;

import java.util.List;
import java.util.function.Function;
import mentoring.datastructure.Person;
import mentoring.match.Match;

/**
 * Example result-writing configurations for test cases.
 */
public enum PojoResultConfiguration{
    /** Writes the names and the total cost of a pair. */
    NAMES_AND_SCORE("names and score", List.of("Mentoré", "Mentor", "Coût"),
            match -> new String[]{match.getMentee().getFullName(),
                match.getMentor().getFullName(), Integer.toString(match.getCost())}),
    /** Writes the names, e-mail addresses and the total cost of a pair. */
    NAMES_EMAILS_AND_SCORE("names, e-mails and scores", 
            List.of("Mentoré", "email mentoré", "Mentor", "email mentor", "Coût"),
            match -> new String[]{match.getMentee().getFullName(),
                match.getMentee().getPropertyAs("Email", String.class),
                match.getMentor().getFullName(),
                match.getMentor().getPropertyAs("Email", String.class),
                Integer.toString(match.getCost())}),
    NAMES_EMAILS_DUPLICATE_AND_SCORE("names, e-mails with duplicate column and score", 
            List.of("Mentor", "email mentor", "Mentoré", "email mentoré", "email mentor bis", 
                    "Coût"),
            match -> new String[]{match.getMentor().getFullName(),
                match.getMentor().getPropertyAs("Email", String.class),
                match.getMentee().getFullName(),
                match.getMentee().getPropertyAs("Email", String.class),
                match.getMentor().getPropertyAs("Email", String.class),
                Integer.toString(match.getCost())});
    
    private final ResultConfiguration<Person, Person> configuration;
    
    private PojoResultConfiguration(String name, List<String> header, 
        Function<Match<Person, Person>, String[]> lineFormater){
        configuration = ResultConfiguration.createForArrayLine(name, header, lineFormater);
    }
    
    public ResultConfiguration<Person, Person> getConfiguration(){
        return configuration;
    }
}
