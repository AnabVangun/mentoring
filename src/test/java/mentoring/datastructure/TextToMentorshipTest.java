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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArguments;
import test.tools.TestFrameWork;

public class TextToMentorshipTest implements TestFrameWork<Person.Mentorship, TextToMentorshipArgument> {

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
    Stream<DynamicTest> checkValidInput(){
        return test("check valid input", v -> {
            try {
                assertEquals(v.expected, v.convertWithException());
            } catch (CsvDataTypeMismatchException | CsvConstraintViolationException ex) {
                Assertions.fail("failed to parse " + v.toString() + " as valid input");
            }
        });
    }
    
    @TestFactory
    Stream<DynamicTest> checkInvalidNonNullInput(){
        Stream<TextToMentorshipArgument> args = Stream.of(
                new TextToMentorshipArgument("foo", null),
                new TextToMentorshipArgument("", null)
        );
        return test(args, "check invalid non-null input", v -> assertThrows(CsvConstraintViolationException.class,
                () -> v.convertWithException()));
    }
    
    @Test
    @DisplayName("mentoring.datastructure.TextToMentorshipTest.check null input")
    void checkNullInput(){
        assertThrows(CsvDataTypeMismatchException.class, () -> new TextToMentorshipArgument(null, null).convertWithException());
    }
    
}

class TextToMentorshipArgument implements TestArguments<Person.Mentorship>{
    final String string;
    final Person.Mentorship expected;
    final TextToMentorship converter;
    TextToMentorshipArgument(String string, Person.Mentorship expected){
        this.string = string;
        this.expected = expected;
        this.converter = new TextToMentorship();
    }

    @Override
    public Person.Mentorship convert(){
        throw new UnsupportedOperationException("Not supported");
    }
    public Person.Mentorship convertWithException() throws CsvDataTypeMismatchException, CsvConstraintViolationException{
        return converter.convert(string);
    }
    
    @Override
    public String toString(){
        return this.string;
    }
    
}