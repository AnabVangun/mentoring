/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mentoring.datastructure;

import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

public class TextToMentorshipTest implements TestFramework<TextToMentorshipArgument> {

    @Override
    public Stream<TextToMentorshipArgument> argumentsSupplier() {
        return Stream.of(new TextToMentorshipArgument("un parrain  ", Person.Mentorship.MENTOR),
        new TextToMentorshipArgument("un filleul", Person.Mentorship.MENTEE),
        new TextToMentorshipArgument("UN   FILLEUL", Person.Mentorship.MENTEE),
        new TextToMentorshipArgument("uN fIlLeUl", Person.Mentorship.MENTEE),
        new TextToMentorshipArgument(" UN PARRAIN", Person.Mentorship.MENTOR),
        new TextToMentorshipArgument("Un Parrain ", Person.Mentorship.MENTOR));
    }
    
    @TestFactory
    Stream<DynamicNode> checkValidInput(){
        return test("check valid input", v -> {
            try {
                assertEquals(v.expected, v.convert());
            } catch (CsvDataTypeMismatchException | CsvConstraintViolationException ex) {
                Assertions.fail("failed to parse " + v.toString() + " as valid input");
            }
        });
    }
    
    @TestFactory
    Stream<DynamicNode> checkInvalidNonNullInput(){
        Stream<TextToMentorshipArgument> args = Stream.of(
                new TextToMentorshipArgument("foo", null),
                new TextToMentorshipArgument("", null)
        );
        return test(args, "check invalid non-null input", v -> assertThrows(
                CsvConstraintViolationException.class,
                () -> v.convert()));
    }
    
    @Test
    @DisplayName("mentoring.datastructure.TextToMentorshipTest.check null input")
    void checkNullInput(){
        assertThrows(CsvDataTypeMismatchException.class, 
            () -> new TextToMentorshipArgument(null, null).convert());
    }
    
}

class TextToMentorshipArgument{
    final String string;
    final Person.Mentorship expected;
    final TextToMentorship converter;
    TextToMentorshipArgument(String string, Person.Mentorship expected){
        this.string = string;
        this.expected = expected;
        this.converter = new TextToMentorship();
    }
    public Person.Mentorship convert() throws CsvDataTypeMismatchException, 
        CsvConstraintViolationException{
        return converter.convert(string);
    }
    
    @Override
    public String toString(){
        return this.string;
    }
}