package mentoring.datastructure;

import assignmentproblem.Result;
import assignmentproblem.Solver;
import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.exceptions.CsvConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.MultiValuedMap;

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
    
    @CsvBindByName(column="Prénom", required=true)
    private String firstName;
    @CsvBindByName(column="Nom", required=true)
    private String lastName;
    @CsvBindByName(column="Promotion", required=true)
    private String year;
    @CsvCustomBindByName(column="Je suis :", required=true, converter = TextToMentorship.class)
    private boolean isMentor;
    @CsvBindByName(column="Horodateur")
    @CsvDate("yyyy/MM/dd h:m:s a z")
    private Date timestamp;
    @CsvBindAndJoinByName(column=".*", elementType=Integer.class)
    private MultiValuedMap<String,Integer> answers;
    
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
        return this.isMentor;
    }
    
    public Date getTimestamp(){
        return this.timestamp;
    }
    
    @Override
    public String toString(){
        return (isMentor ? "Parrain" : "Filleul") + " " + firstName + " " + lastName + "(" + year 
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
            answer = answers.containsKey(key) ? answers.get(key).iterator().next() : null;
            otherAnswer = other.answers.containsKey(key) ? other.answers.get(key).iterator().next() 
                : null;
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
    private int computeAnswerDistance(Integer first, Integer second){
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
    /**
     * Checks that the bean is well-formed, that is that all included questions have exactly one
     * answer.
     * Throws {@link CsvConstraintViolationException} when a bean is not well-formed and remove null
     * and empty answers.
     */
    public static final BeanVerifier<Person> VERIFIER = (Person t) -> {
        synchronized(t){
            List<String> toRemove = new ArrayList<>();
            List<String> withNullAnswers = new ArrayList<>();
            for (String question:t.answers.keySet()){
                /*
                Check if 
                1. has 0 answer or only null answers -> remove question
                2. has more than 1 non-null answers -> throw exception
                3. has null answers -> remove nulls
                */
                int nonNull = 0;
                boolean foundNull = false;
                for (Integer answer:t.answers.get(question)){
                    if (answer != null){
                        nonNull += 1;
                    } else {
                        foundNull = true;
                    }
                }
                if (nonNull > 1){
                    throw new CsvConstraintViolationException("Found several answers to "
                            + "question " + question + ": " + t.answers.get(question));
                } else if (nonNull == 0){
                    toRemove.add(question);
                } else if (foundNull){
                    withNullAnswers.add(question);
                }
            }
            toRemove.forEach(question -> t.answers.remove(question));
            withNullAnswers.forEach(question -> {
                while (t.answers.get(question).remove(null)){}//remove all nulls.
            });
            return true;
        }
    };
}
