package mentoring.datastructure;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import com.opencsv.CSVReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a list of answers to the registration form.
 * 
 * It handles the resolution of the underlying assignment problem.
 */
public class PersonList extends ArrayList<Person> {
    //TODO check OpenCSV documentation: this class should contain an arrayList and be initialised with a CSV file, rather than extend ArrayList
    
    public enum ErrorCode{
        NAME_CONFLICT;
    }
    
    public Map<Person, Person> assign(Solver solver){
        PersonList mentors = new PersonList();
        PersonList mentees = new PersonList();
        for (Person p: this){
            if (p.isMentor()){
                mentors.add(p);
            } else {
                mentees.add(p);
            }
        }
        int[][] costMatrix = new int[mentees.size()][mentors.size()];
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++){
                costMatrix[i][j] = mentees.get(i).computeDistance(mentors.get(j));
            }
        }
        Result assignments = solver.solve(costMatrix);
        Map<Person, Person> result = new HashMap<>();
        for(int i = 0 ; i < assignments.getRowAssignments().size(); i++){
            if (assignments.getRowAssignments().get(i) != assignments.unassigned){
                result.put(mentees.get(i), mentors.get(assignments.getRowAssignments().get(i)));
            }
        }
        return result;
    }
    
    public static EnumSet<ErrorCode> checkHeaderValidity(CSVReader reader){
        throw new UnsupportedOperationException("not implemented yet");
    }
}
