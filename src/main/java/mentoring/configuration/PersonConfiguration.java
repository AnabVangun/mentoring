package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.MultiplePropertyDescription;
import mentoring.datastructure.SimplePropertyDescription;
import org.apache.commons.lang3.StringUtils;
/**
 * The definition of a person's properties.
 * 
 * <p>A {@code PersonConfiguration} instance describes at the same time how to parse a CSV-file 
 * database to extract {@link Person} instances and said extracted {@link Person} instances.
 */
public final class PersonConfiguration extends Configuration<PersonConfiguration> {
    
    private final Set<SimplePropertyDescription<?>> properties;
    private final Set<MultiplePropertyDescription<?,?>> multipleProperties;
    private final String separator;
    private final String nameFormat;
    private final List<String> nameProperties;
    private final Collection<String> allPropertiesHeaderNames;
    private final Set<String> allPropertiesNames = new HashSet<>();
    
    /**
     * Create a PersonConfiguration instance.
     * @param configurationName name of the configuration to create
     * @param properties properties of the configuration: {@link Person} objects abiding by that 
     * configuration will have these properties.
     * @param multipleProperties similar to {@code properties} but for multi-valued properties.
     * @param separator separator used inside multi-valued properties in CSV files.
     * @param nameFormat the format of the full name of a person.
     * @param namePropertiesHeaderNames the names of the columns containing parts of the full name 
     * of a person.
     */
    public PersonConfiguration(String configurationName,
                Set<SimplePropertyDescription<?>> properties, 
                Set<MultiplePropertyDescription<?,?>> multipleProperties, 
                String separator, String nameFormat,
                List<String> namePropertiesHeaderNames){
            super(configurationName);
            if(! isValidNameDefinition(nameFormat, namePropertiesHeaderNames)){
                throw new IllegalArgumentException("""
                        Tried to build a PersonConfiguration object with invalid name definition: 
                        %s as and %s""".formatted(nameFormat, namePropertiesHeaderNames));
            }
            Set<String> tmpAllProperties = new HashSet<>();
            this.properties = properties;
            this.properties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
            this.properties.forEach(p -> allPropertiesNames.add(p.getName()));
            this.multipleProperties = multipleProperties;
            this.multipleProperties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
            this.multipleProperties.forEach(p -> allPropertiesNames.add(p.getName()));
            this.separator = separator;
            this.nameFormat = nameFormat;
            this.nameProperties = namePropertiesHeaderNames;
            tmpAllProperties.addAll(namePropertiesHeaderNames);
            allPropertiesHeaderNames = Collections.unmodifiableCollection(tmpAllProperties);
    }
    
    /**
     * Returns the {@link SimplePropertyDescription} instances describing single-valued properties.
     * @return the identification of single-valued properties for this configuration
     */
    public Set<SimplePropertyDescription<?>> getSimplePropertiesNames(){
        return properties;
    }
    
    /**
     * Returns the {@link SimplePropertyDescription} instances describing multi-valued properties. Multi-valued
     * properties are properties associated with collections of values.
     * @return the identification of multi-valued properties for this configuration
     */
    public Set<MultiplePropertyDescription<?,?>> getMultiplePropertiesNames(){
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
     * @return the ordered list of the column used to fill the placeholders in 
     * {@link #getNameFormat() }.
     * @see #getNameFormat() 
     */
    public List<String> getNamePropertiesHeaderNames(){
        return nameProperties;
    }
    
    /**
     * Checks if this configuration contains a property with the given name.
     * @param name to verify
     * @return true if a property exists with the input name in this configuration
     */
    public boolean containsPropertyName(String name){
        return allPropertiesNames.contains(name);
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
