package mentoring.datastructure;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
    TODO improve String handling
        1. For column headers in CSV files, put special header names in separate file.
*/
/**
 * This bean represents the answers of a person to the registration form.
 */
public class Person {
    static final int YEAR_WEIGHT = 10;
    static final int REFERENCE_YEAR = 2020;
    static final int MOTIVATION_WEIGHT = 30;
    static final int DOMAIN_WEIGHT = 10;
    static final int MAX_DISTANCE = (Domain.values().length * DOMAIN_WEIGHT 
        + Motivation.values().length*MOTIVATION_WEIGHT) < 0 ? Integer.MAX_VALUE : 
        (Domain.values().length * DOMAIN_WEIGHT + Motivation.values().length*MOTIVATION_WEIGHT);
    
    @CsvBindByName(column="Prénom", required=true)
    private String firstName;
    @CsvBindByName(column="Nom", required=true)
    private String lastName;
    @CsvBindByName(column="Horodateur")
    @CsvDate("yyyy/MM/dd h:m:s a z")
    private Date timestamp;
    @CsvBindAndSplitByName(column="Activités et métiers", splitOn=";", elementType=Domain.class)
    private Set<Domain> domains;
    @CsvBindAndSplitByName(column="Motivation", splitOn=";", elementType=Motivation.class)
    private Set<Motivation> motivations;
    @CsvBindByName(column="Anglais", required=true)
    private boolean english;
    
    public String getFirstName(){
        return this.firstName;
    }
    @Override
    public String toString(){
        return getFirstName() + " " + getLastName();
    }
    
    public String getLastName(){
        return this.lastName;
    }
    public Date getTimestamp(){
        return this.timestamp;
    }
    
    public boolean speaksEnglish(){
        return this.english;
    }
    
    public Set<Domain> getDomains(){
        return Collections.unmodifiableSet(domains);
    }
    
    public Set<Motivation> getMotivations(){
        return Collections.unmodifiableSet(motivations);
    }
    
    /**
     * Computes the distance between two {@link Person} objects.
     * 
     * This distance is positive.
     * @param other to which the distance must be computed.
     * @return the computed distance.
     */
    public int computeDistance(Mentor other){
        if (this.speaksEnglish() && ! other.speaksEnglish()){
            return Integer.MAX_VALUE;
        }
        int result = (REFERENCE_YEAR - other.getYear())*YEAR_WEIGHT + MAX_DISTANCE;
        for (Domain d: this.domains){
            if (other.getDomains().contains(d)){
                result -= DOMAIN_WEIGHT;
            }
        }
        for (Motivation m:this.motivations){
            if (other.getMotivations().contains(m)){
                result -= MOTIVATION_WEIGHT;
            }
        }
        return (result < 0 ? Integer.MAX_VALUE : result);
    }
    
    /**
     * Assign mentors to mentees.
     * 
     * Solve the assignment problem represented by the list of mentors and mentees: compute the cost
     * matrix between mentors and mentees and return the optimal assignment so that either 
     * each mentor as a mentee, or each mentee has a mentor, depending on the minority group.
     * @param mentors List of mentors ready do be assigned.
     * @param mentees List of mentees waiting for a mentor.
     * @param solver Solver to use to compute an optimal solution from a positive rectangular cost
     * matrix.
     * @return A mapping between mentees, used as keys, and mentors, used as values. 
     */
    public static Map<Person, Person> assign(List<Mentor> mentors, List<Person> mentees, 
            Solver solver){
        //TODO test this function
        /**
         * To test this function, refactoring may be necessary because there are four distinct 
         * steps mixed up: separate mentors and mentees, compute cost matrix, solve cost matrix,
         * transform cost matrix solution into person mapping.
         */
        Map<Person, Person> result = new HashMap<>();
        if (mentees.isEmpty() || mentors.isEmpty()){
            return result;
        }
        int[][] costMatrix = new int[mentees.size()][mentors.size()];
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++){
                costMatrix[i][j] = mentees.get(i).computeDistance(mentors.get(j));
            }
        }
        Result assignments = solver.solve(costMatrix);
        for(int i = 0; i < assignments.getRowAssignments().size(); i++){
            if (assignments.getRowAssignments().get(i) != assignments.unassigned){
                result.put(mentees.get(i), mentors.get(assignments.getRowAssignments().get(i)));
            }
        }
        return result;
    }
}
