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
            FileReader reader = new FileReader("resources\\main\\Parrainage_generated.csv",
                    Charset.forName("utf-8"));
            List<Person> beans = new CsvToBeanBuilder<Person>(reader)
                    .withType(Person.class).withOrderedResults(false)
                    .build().parse();
            Map<Person, Person> assignments = Person.assign(beans, new HungarianSolver(null));
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
