package mentoring.datastructure;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;

/*
    TODO improve String handling
        1. For column headers in CSV files, put special header names in separate file.
        2. For Mentorship, include a nice String representation.
*/
/**
 * This bean represents the answers of a person to the registration form.
 * 
 * It is somewhat coupled to the format of the CSV output of the registration 
 * answers. It handles the addition of new interest columns and the reordering 
 * of columns. Adding new identification columns or changing the scale on which
 * interests are rated will need modifications of this class.
 * 
 */
public class Person {
    static final int DEFAULT_ANSWER = 3;
    static enum Mentorship{
        MENTOR("un parrain"),
        MENTEE("un filleul");
        
        private final Set<String> csvStrings = new HashSet<>();
        /**
         * Associates a set of strings to a value.
         * All strings are reduced to lower case and stripped of their white 
         * space characters before comparing with the values of a CSV File.
         * @param strings 
         */
        private Mentorship(String... strings){
            csvStrings.addAll(Arrays.asList(Arrays.stream(strings)
                .map(v -> StringUtils.deleteWhitespace(v.toLowerCase())).toArray(String[]::new)));
        }
        /**
         * Parse the CSV field corresponding to the mentorship status.
         * @param string CSV field to parse.
         * @return the corresponding Mentorship if the string could be parsed.
         * @throws IllegalArgumentException if the string does not correspond to
         * a known Mentorship.
         */
        static Mentorship parseCsvField(String string) throws IllegalArgumentException{
            String lower = StringUtils.deleteWhitespace(string.toLowerCase());
            for (Mentorship candidate: Mentorship.values()){
                if (candidate.csvStrings.contains(lower)){
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Failed to parse " + string 
                + " as a mentorship status");
        }
    }
    @CsvBindByName(column="Prénom", required=true)
    private String firstName;
    @CsvBindByName(column="Nom", required=true)
    private String lastName;
    @CsvBindByName(column="Promotion", required=true)
    private String year;
    @CsvCustomBindByName(column="Je suis :", required=true, converter = TextToMentorship.class)
    private Mentorship isMentor;
    @CsvBindByName(column="Horodateur")
    @CsvDate("yyyy/MM/dd h:m:s a z")
    private Date timestamp;
    @CsvBindAndJoinByName(column=".*", elementType=Integer.class)
    public MultiValuedMap<String,Integer> answers;
    
    public String getFirstName(){
        return this.firstName;
    }
    
    public String getLastName(){
        return this.lastName;
    }
    
    public String getYear(){
        return this.year;
    }
    
    public boolean isMentor(){
        return this.isMentor.equals(Mentorship.MENTOR);
    }
    
    public Date getTimestamp(){
        return this.timestamp;
    }
    
    @Override
    public String toString(){
        return (isMentor()?"Parrain":"Filleul") + " " + firstName + " " + lastName + "(" + year 
                + ")";
    }
    /**
     * Computes the distance between two {@link Person} objects.
     * 
     * This distance is positive, symmetric, and equal to zero between an object
     * and itself.
     * @param other to which the distance must be computed.
     * @return the computed distance.
     */
    public int computeDistance(Person other){
        if (equals(other)){
            return 0;
        }
        int result = 0;
        Set<String> keySet = new HashSet<>(answers.keySet());
        keySet.addAll(other.answers.keySet());
        Integer answer;
        Integer otherAnswer;
        for (String key:keySet){
            //TODO make this check once and for all, ideally when parsing CSV file
            //This can probably be done via OpenCsv check capability
            if (answers.containsKey(key) && answers.get(key).size() != 1){
                throw new IllegalStateException("Person " + toString() + " has multiple values for key " + key + ": " + answers.get(key).toString());
            }
            if (other.answers.containsKey(key) && other.answers.get(key).size() != 1){
                throw new IllegalStateException("Person " + toString() + " has multiple values for key " + key + ": " + other.answers.get(key).toString());
            }
            answer = answers.containsKey(key) ? answers.get(key).iterator().next() : null;
            otherAnswer = other.answers.containsKey(key) ? other.answers.get(key).iterator().next() : null;
            result += computeAnswerDistance(answer, otherAnswer);
        }
        return result;
    }
    /**
     * Compute the distance between two persons for a single question.
     * @param first Answer of the first person to the question, may be null.
     * @param second Answer of the second person to the question, may be null.
     * @return The distance between the two answers. The number will be positive, it may be zero.
     */
    static int computeAnswerDistance(Integer first, Integer second){
        //TODO test
        return (int) Math.pow((first == null ? DEFAULT_ANSWER : first) 
            - (second == null ? DEFAULT_ANSWER : second), 2);
    }
    
    /**
     * Assign mentors to mentees.
     * 
     * Solve the assignment problem represented by the list of mentors and mentees: compute the cost
     * matrix between mentors and mentees and return the optimal assignment so that either 
     * each mentor as a mentee, or each mentee has a mentor, depending on the minority group.
     * @param list List of mentors and mentees, all mixed up.
     * @param solver Solver to use to compute an optimal solution from a positive rectangular cost
     * matrix.
     * @return A mapping between mentees, used as keys, and mentors, used as values. 
     */
    public static Map<Person, Person> assign(List<Person> list, Solver solver){
        //TODO test this function
        /**
         * To test this function, refactoring may be necessary because there are four distinct 
         * steps mixed up: separate mentors and mentees, compute cost matrix, solve cost matrix,
         * transform cost matrix solution into person mapping.
         */
        PersonList mentors = new PersonList();
        PersonList mentees = new PersonList();
        for (Person p: list){
            if (p.isMentor()){
                mentors.add(p);
            } else {
                mentees.add(p);
            }
        }
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
