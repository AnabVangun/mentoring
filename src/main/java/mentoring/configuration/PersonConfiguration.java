package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import org.apache.commons.lang3.StringUtils;
/**
 * The definition of a person's properties.
 * 
 * <p>A {@code PersonConfiguration} instance describes at the same time how to parse a CSV-file 
 * database to extract {@link Person} instances and said extracted {@link Person} instances.
 */
public final class PersonConfiguration {
    /*
    TODO: consider implementing equals and hashCode for PropertyName and MultiplePropertyName to 
    leverage using a set rather than an arbitrary collection.
    Beware: MultiplePropertyName contains a Function, which is difficult to equate.
    Once done, go leverage them to simplify ExtendedPersonConfigurationTest
    */
    
    private final String configurationName;
    private final Set<PropertyName<?>> properties;
    private final Set<MultiplePropertyName<?,?>> multipleProperties;
    private final String separator;
    private final String nameFormat;
    private final List<String> nameProperties;
    private final Collection<String> allPropertiesHeaderNames;
    
    public PersonConfiguration(String configurationName,
                Set<PropertyName<?>> properties, 
                Set<MultiplePropertyName<?,?>> multipleProperties, 
                String separator, String nameFormat,
                List<String> nameProperties){
            this.configurationName = configurationName;
            Set<String> tmpAllProperties = new HashSet<>();
            this.properties = properties;
            this.properties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
            this.multipleProperties = multipleProperties;
            this.multipleProperties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
            this.separator = separator;
            this.nameFormat = nameFormat;
            this.nameProperties = nameProperties;
            tmpAllProperties.addAll(nameProperties);
            allPropertiesHeaderNames = Collections.unmodifiableCollection(tmpAllProperties);
    }
    
    @Override
    public String toString(){
        return configurationName;
    }
    
    /**
     * Returns the {@link PropertyName} instances describing single-valued properties.
     * @return the identification of single-valued properties for this configuration
     */
    public Set<PropertyName<?>> getPropertiesNames(){
        return properties;
    }
    
    /**
     * Returns the {@link PropertyName} instances describing multi-valued properties. Multi-valued
     * properties are properties associated with collections of values.
     * @return the identification of multi-valued properties for this configuration
     */
    public Set<MultiplePropertyName<?,?>> getMultiplePropertiesNames(){
        return multipleProperties;
    }
    
    /**
     * Returns the separator used in the CSV file to separate values inside multi-valued properties.
     * @return the separator for String.split() to get the individual parts of multi-valued 
     * properties
     */
    public String getSeparator(){
        return separator;
    }
    
    /**
     * Returns the format of the full name of a person.
     * This can be used in a call to {@link String#format(java.lang.String, java.lang.Object...) }
     * along with the values associated with the columns indicated in 
     * {@link #getNamePropertiesHeaderNames() }.
     * @return a String object usable as the first argument of {@code String.format()}.
     */
    public String getNameFormat(){
        return nameFormat;
    }
    
    /**
     * Returns the names of the columns containing parts of the full name of a person.
     * @return 
     * @see #getNameFormat() 
     */
    public List<String> getNamePropertiesHeaderNames(){
        return nameProperties;
    }
    
    /**
     * Returns the names of all the columns expected in a CSV file.
     * @return the names of all the columns expected by this configuration in a CSV file.
     */
    public Collection<String> getAllPropertiesHeaderNames(){
        return allPropertiesHeaderNames;
    }
    
    /**
     * Verify if its input is a valid definition for computing Person names.
     * @param nameFormat String with placeholders defining how names will be computed
     * @param nameProperties List of the property header names that will be used to replace the 
     * placeholders
     * @return true if the input can be used to compute names, false otherwise
     */
    public static boolean isValidNameDefinition(String nameFormat, List<String> nameProperties){
        int count = StringUtils.countMatches(nameFormat.toLowerCase(), "%s");
        return nameProperties.size() == count;
    }
}
