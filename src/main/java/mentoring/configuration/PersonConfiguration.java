package mentoring.configuration;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.PropertyName;
/**
 * The definition of a person's properties.
 * 
 * <p>A {@code PersonConfiguration} instance describes at the same time how to parse a CSV-file 
 * database to extract {@link Person} instances and said extracted {@link Person} instances.
 */
public interface PersonConfiguration {
    /**
     * Returns the {@link PropertyName} instances describing single-valued properties.
     * @return the identification of single-valued properties for this configuration
     */
    Set<PropertyName> getPropertiesNames();
    
    /**
     * Returns the {@link PropertyName} instances describing multi-valued properties. Multi-valued
     * properties are properties associated with collections of values.
     * @return the identification of multi-valued properties for this configuration
     */
    Set<PropertyName> getMultiplePropertiesNames();
    
    /**
     * Returns the separator used in the CSV file to separate values inside multi-valued properties.
     * @return the separator for String.split() to get the individual parts of multi-valued 
     * properties
     */
    String getSeparator();
    
    /**
     * Returns the format of the full name of a person.
     * This can be used in a call to {@link String#format(java.lang.String, java.lang.Object...) }
     * along with the values associated with the columns indicated in 
     * {@link #getNamePropertiesHeaderNames() }.
     * @return a String object usable as the first argument of {@code String.format()}.
     */
    String getNameFormat();
    
    /**
     * Returns the names of the columns containing parts of the full name of a person.
     * @return 
     * @see #getNameFormat() 
     */
    List<String> getNamePropertiesHeaderNames();
    
    /**
     * Returns the names of all the columns expected in a CSV file.
     * @return the names of all the columns expected by this configuration in a CSV file.
     */
    Collection<String> getAllPropertiesHeaderNames();
}
