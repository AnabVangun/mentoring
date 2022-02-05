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
        return Stream.of(new TextToMentorshipArgument("un parrain  ", true),
        new TextToMentorshipArgument("un filleul", false),
        new TextToMentorshipArgument("UN   FILLEUL", false),
        new TextToMentorshipArgument("uN fIlLeUl", false),
        new TextToMentorshipArgument(" UN PARRAIN", true),
        new TextToMentorshipArgument("Un Parrain ", true));
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
    final Boolean expected;
    final TextToMentorship converter;
    TextToMentorshipArgument(String string, Boolean expected){
        this.string = string;
        this.expected = expected;
        this.converter = new TextToMentorship();
    }
    public Boolean convert() throws CsvDataTypeMismatchException, 
        CsvConstraintViolationException{
        return converter.convert(string);
    }
    
    @Override
    public String toString(){
        return this.string;
    }
}