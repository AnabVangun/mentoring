package mentoring.datastructure;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import mentoring.datastructure.Person.Mentorship;
/**
 * This custom converter handles the string defining if a person is a mentor or 
 * a mentee.
 * @author AnabVangun
 */
public class TextToMentorship extends AbstractBeanField<String, Mentorship>{
    @Override
    protected Mentorship convert(String string) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try{
            return Mentorship.parseCsvField(string);
        } catch (IllegalArgumentException e){
            throw new CsvConstraintViolationException("Could not parse " + string + " as a mentorship");
        } catch (NullPointerException e){
            throw new CsvDataTypeMismatchException("Could not parse null as a mentorship");
        }
    }
    
}
