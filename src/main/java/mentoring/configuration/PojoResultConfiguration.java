package mentoring.configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import mentoring.datastructure.Person;
import mentoring.match.Match;

/**
 * Example result-writing configurations for test cases.
 */
public enum PojoResultConfiguration{
    /** Writes the names and the total cost of a pair. */
    NAMES_AND_SCORE("names and score", List.of("Mentor�", "Mentor", "Co�t"),
            match -> Map.of("Mentor�", match.getMentee().getFullName(),
                "Mentor", match.getMentor().getFullName(), 
                "Co�t", match.getCost())),
    /** Writes the names, e-mail addresses and the total cost of a pair. */
    NAMES_EMAILS_AND_SCORE("names, e-mails and scores", 
            List.of("Mentor�", "email mentor�", "Mentor", "email mentor", "Co�t"),
            match -> Map.of("Mentor�", match.getMentee().getFullName(),
                "email mentor�", match.getMentee().getPropertyAs("Email", Object.class),
                "Mentor", match.getMentor().getFullName(),
                "email mentor", match.getMentor().getPropertyAs("Email", Object.class),
                "Co�t", match.getCost())),
    NAMES_EMAILS_DUPLICATE_AND_SCORE("names, e-mails with duplicate column and score", 
            List.of("Mentor", "email mentor", "Mentor�", "email mentor�", "email mentor", 
                    "Co�t"),
            match -> Map.of("Mentor", match.getMentor().getFullName(),
                "email mentor", match.getMentor().getPropertyAs("Email", Object.class),
                "Mentor�", match.getMentee().getFullName(),
                "email mentor�", match.getMentee().getPropertyAs("Email", Object.class),
                "Co�t", match.getCost()));
    
    private final ResultConfiguration<Person, Person> configuration;
    
    private PojoResultConfiguration(String name, List<String> header, 
        Function<Match<Person, Person>, Map<String, Object>> lineFormater){
        configuration = ResultConfiguration.createForMapLine(name, header, lineFormater);
    }
    
    public ResultConfiguration<Person, Person> getConfiguration(){
        return configuration;
    }
}
