package mentoring.datastructure;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
/**
 * This custom converter handles the string defining if a person is a mentor or 
 * a mentee.
 */
public class TextToMentorship extends AbstractBeanField<String, Boolean>{
    private static final Map<String, Boolean> booleanTranslation = Map.of(
        "unparrain", true, 
        "unfilleul", false);
    
    @Override
    protected Boolean convert(String string) throws CsvDataTypeMismatchException, 
        CsvConstraintViolationException {
        if (string == null){
            throw new CsvDataTypeMismatchException("Could not parse null as a mentorship");
        }
        String simplifiedString = StringUtils.deleteWhitespace(string.toLowerCase());
        if (booleanTranslation.containsKey(simplifiedString)){
            return booleanTranslation.get(simplifiedString);
        } else {
            throw new CsvConstraintViolationException("Could not parse " + string 
                + " as a mentorship");
        }
    }
    
}
