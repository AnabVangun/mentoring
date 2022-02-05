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

    //TODO define expected column names in this class as final constants
    //TODO use this variables in Person
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
            csvStrings.addAll(Arrays.asList(Arrays.stream(strings).
                    map(v -> StringUtils.deleteWhitespace(v.toLowerCase())).toArray(String[]::new)));
        }
        /**
         * Parse the CSV field corresponding to the mentorship status.
         * @param string CSV field to parse.
         * @return the corresponding Mentorship if the string could be parsed.
         * @throws IllegalArgumentException if the string does not correspond to
         * a known Mentorship.
         */
        static Mentorship parseCsvField(String string){
            String lower = StringUtils.deleteWhitespace(string.toLowerCase());
            for (Mentorship candidate: Mentorship.values()){
                if (candidate.csvStrings.contains(lower)){
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Failed to parse " + string + " as a mentorship status");
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
        return (isMentor()?"Parrain":"Filleul") + " " + firstName + " " + lastName + "(" + year + ")";
    }
    //TODO Jdoc
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
        //TODO improve set management: action on union of sets
        for (String key:answers.keySet()){
            if (answers.get(key).size() != 1){
                throw new IllegalStateException("Person " + toString() + " has multiple values for key " + key + ": " + answers.get(key).toString());
            }
            Integer answer = answers.get(key).iterator().next();
            if (other.answers.containsKey(key)){
                if (other.answers.get(key).size() != 1){//TODO factor this and previous similar check in a private method
                    throw new IllegalStateException("Person " + toString() + " has multiple values for key " + key + ": " + other.answers.get(key).toString());
                }
                //TODO improve handling of null values
                Integer otherAnswer = other.answers.get(key).iterator().next();
                if (otherAnswer == null){
                    otherAnswer = DEFAULT_ANSWER;
                }
                //TODO use method computing score between two answers to update result
                if (answer == null){
                    answer = DEFAULT_ANSWER;
                }
                result += Math.pow(answer-otherAnswer, 2);
            }
            else if (answer != null){
                //TODO use method computing score between an answer and a missing answer to update result
                result += Math.pow(answer-DEFAULT_ANSWER, 2);
            }
        }
        for (String key:other.answers.keySet()){
            if (!answers.containsKey(key)){
                if (other.answers.get(key).size() != 1){
                    //TODO call method to perform this check or, better, perform it once and for all
                    throw new IllegalStateException("Person " + toString() + " has multiple values for key " + key + ": " + other.answers.get(key).toString());
                }
                Integer otherAnswer = other.answers.get(key).iterator().next();
                //TODO improve handling of null
                if (otherAnswer != null){
                //TODO use method computing score between an answer and a missing answer to update result
                    result += Math.pow(otherAnswer-DEFAULT_ANSWER, 2);
                }
            }
        }
        return result;
    }
    
    //TODO document and test
    public static Map<Person, Person> assign(List<Person> list, Solver solver){
        PersonList mentors = new PersonList();
        PersonList mentees = new PersonList();
        for (Person p: list){
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
        //TODO handle edge cases: no mentors, no mentees
        Result assignments = solver.solve(costMatrix);
        Map<Person, Person> result = new HashMap<>();
        for(int i = 0; i < assignments.getRowAssignments().size(); i++){
            if (assignments.getRowAssignments().get(i) != assignments.unassigned){
                result.put(mentees.get(i), mentors.get(assignments.getRowAssignments().get(i)));
            }
        }
        return result;
    }
}
