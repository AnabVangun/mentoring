package mentoring;

import mentoring.datastructure.Person;
import assignmentproblem.hungariansolver.HungarianSolver;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import mentoring.datastructure.Mentor;

/**
 * Proof of concept of the mentoring application.
 */
public class Main {
    /**
     * Parse an example file representing a mentoring problem and print the resulting assignment.
     * 
     * @param args the command line arguments, ignored for now.
     */
    public static void main(String[] args) {
        System.out.println("Build and solve cost matrix");
        try{
            List<Person> mentees = new CsvToBeanBuilder<Person>(
                    new FileReader("resources\\main\\Filleul_Trivial.csv",
                        Charset.forName("utf-8")))
                    .withType(Person.class).withOrderedResults(false)
                    .build().parse();
            List<Mentor> mentors = new CsvToBeanBuilder<Mentor>(
                    new FileReader("resources\\main\\Mentor_Trivial.csv",
                        Charset.forName("utf-8")))
                    .withType(Mentor.class).withOrderedResults(false)
                    .build().parse();
            Map<Person, Person> assignments = Person.assign(mentors, mentees, new HungarianSolver(null));
            for (Map.Entry<Person, Person> mapping : assignments.entrySet()){
                System.out.println("Mentee '" + mapping.getKey() +
                        "' with mentor '" + mapping.getValue() +"'");
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
