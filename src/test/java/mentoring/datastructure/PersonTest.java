package mentoring.datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

public class PersonTest implements TestFramework<PersonArgumentsPair>{

    @Override
    public Stream<PersonArgumentsPair> argumentsSupplier(){
        String[] simpleQuestions = new String[]{"foo", "bar"};
        Integer[] simpleAnswers = new Integer[]{1, 3};
        String[] firstExtendedQuestions = new String[]{"foo", "bar", "foobar"};
        String[] secondExtendedQuestions = new String[]{"foo", "bar", "barfoo", "foobarfoo"};
        Integer[] firstExtendedAnswers = new Integer[]{4, null, 5};
        Integer[] secondExtendedAnswers = new Integer[]{1,5,2,null};
        
        PersonArguments firstPerson = new PersonArguments("Alice", "Andrew", "1qç2", 
                Person.Mentorship.MENTOR, new Date(),simpleQuestions, simpleAnswers);
        //Second test category: two identical persons except for mentorship
        PersonArguments identicalAnswers = firstPerson.clone();
        //Third test category: two diffeerent persons, one with missing answers
        PersonArguments thirdPerson = new PersonArguments("Bob", "Billy", "qsd&&31", 
                Person.Mentorship.MENTEE, new Date(), firstExtendedQuestions, firstExtendedAnswers);
        //Fourth test category: two different persons both with missing answers
        PersonArguments fourthPerson = new PersonArguments("Charlie", "Cloe", "23",
                Person.Mentorship.MENTEE, new Date(), secondExtendedQuestions, secondExtendedAnswers);
        
        List<PersonArgumentsPair> list = new ArrayList<>();
        for (Person.Mentorship firstMentor : Person.Mentorship.values()){
            firstPerson.isMentor = firstMentor;
            //First test category: a single person
            list.add(new PersonArgumentsPair(firstPerson, firstPerson, "single " + firstMentor));
            for (Person.Mentorship secondMentor : Person.Mentorship.values()){
                identicalAnswers.isMentor = secondMentor;
                list.add(new PersonArgumentsPair(firstPerson, identicalAnswers, 
                        "identical answers for " + firstMentor + " and " + secondMentor));
                list.add(new PersonArgumentsPair(firstPerson, thirdPerson,
                        firstMentor + " missing answers with " + secondMentor));
                list.add(new PersonArgumentsPair(thirdPerson, fourthPerson,
                        firstMentor + " and " + secondMentor + " with different questions"));
            }
        }
        return list.stream();
    }
    @TestFactory
    public Stream<DynamicNode> computeDistance_positive(){
        return test(argumentsSupplier(), "computeDistance (positive result)", 
                args -> {
            try {
                assertTrue(args.left.convert().computeDistance(args.right.convert()) >= 0);
            } catch (IllegalAccessException ex) {
                fail("Something went wrong in converter : " + ex.getLocalizedMessage());
            }
        });
    }
    @TestFactory
    public Stream<DynamicNode> computeDistance_symmetric(){
        return test(argumentsSupplier(), "computeDistance (symmetrical result)", 
                args -> {
                    try {
                        Person first = args.left.convert();
                        Person second = args.right.convert();
                        int firstDistance = first.computeDistance(second);
                        int secondDistance = second.computeDistance(first);
                        Assertions.assertEquals(firstDistance, secondDistance);
                    } catch (IllegalAccessException ex){
                        fail("Something went wrong in converter : " + ex.getLocalizedMessage());
                    }
                        });
    }
}

class PersonArguments{
    final String firstName;
    final String lastName;
    final String year;
    Person.Mentorship isMentor;
    final Date date;
    final MultiValuedMap<String, Integer> answers = new ArrayListValuedHashMap<>();
    
    PersonArguments(String firstName, String lastName, String year, Person.Mentorship mentorship, 
            Date date, String[] questions, Integer[] answers){
        this.firstName = firstName;
        this.lastName = lastName;
        this.year = year;
        this.isMentor = mentorship;
        this.date = date;
        if (questions != null && answers != null){
            if (questions.length != answers.length){
                throw new IllegalArgumentException("Questions and answers do not have the same "
                        + "length: " + questions.length + "!=" + answers.length + " for " + this);
            }
            for (int i = 0; i < questions.length; i++){
                this.answers.put(questions[i], answers[i]);
            }
        } else if (questions != null || answers != null){
            throw new IllegalArgumentException("Questions and answers must either both be null, or "
                + "none of them. Received " 
                + (questions == null? "null" : Arrays.toString(questions))
                + " and " + (answers == null? "null" : Arrays.toString(answers)));
        }
    }
    @Override
    protected PersonArguments clone(){
        PersonArguments result = new PersonArguments(firstName, lastName, year, isMentor, date, 
            null, null);
        result.answers.putAll(answers);
        return result;
    }
    public Person convert() throws IllegalAccessException {
        Person result = new Person();
        FieldUtils.writeField(result, "firstName", firstName, true);
        FieldUtils.writeField(result, "lastName", lastName, true);
        FieldUtils.writeField(result, "year", year, true);
        FieldUtils.writeField(result, "isMentor", isMentor, true);
        FieldUtils.writeField(result, "timestamp", date, true);
        FieldUtils.writeField(result, "answers", answers, true);
        return result;
    }
    
    @Override
    public String toString(){
        return (firstName == null ? "null" : firstName) + "_" +
                (lastName == null ? "null" : lastName) + "_" + 
                (year == null ? "null" : year) + "@" + hashCode();
    }
    
}

class PersonArgumentsPair extends MutablePair<PersonArguments, PersonArguments>{
    final String name;
    PersonArgumentsPair(PersonArguments left, PersonArguments right, String name){
        super(left, right);
        this.name = name;
    }
    
    @Override
    public String toString(){
        return this.name;
    }
}