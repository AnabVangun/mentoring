package mentoring.datastructure;

import AssignmentProblem.Solver;
import com.opencsv.CSVReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a list of answers to the registration form.
 * 
 * It handles the resolution of the underlying assignment problem.
 * @author AnabVangun
 */
public class PersonList extends ArrayList<Person> {
    //TODO check OpenCSV documentation: this class should contain an arrayList and be initialised with a CSV file, rather than extend ArrayList
    
    public enum ErrorCode{
        NAME_CONFLICT;
    }
    
    public Map<Person, Person> assign(Solver.SolverType solverType){
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
        Solver solver;
        if (solverType == null){
            solver = Solver.solve(costMatrix);
        } else {
            solver = Solver.solve(costMatrix, solverType);
        }
        Map<Person, Person> result = new HashMap<>();
        for(int[] pair: solver.getAssignments()){
            result.put(mentees.get(pair[0]), mentors.get(pair[1]));
        }
        return result;
    }
    
    public static EnumSet<ErrorCode> checkHeaderValidity(CSVReader reader){
        throw new UnsupportedOperationException("not implemented yet");
    }
}
