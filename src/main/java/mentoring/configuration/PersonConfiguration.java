package mentoring.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PersonConfiguration {
    public final Set<String> booleanProperties;
    public final Set<String> integerProperties;
    public final Set<String> multipleStringProperties;
    public final String separator;
    public final String nameFormat;
    public List<String> nameProperties;
    public final Collection<String> allProperties;
    
    public PersonConfiguration(Set<String> booleanProperties, Set<String> integerProperties, 
            Set<String> multipleStringProperties, String separator, String nameFormat,
            List<String> nameProperties){
        List<String> tmpAllProperties = new ArrayList<>();
        this.booleanProperties = Collections.unmodifiableSet(booleanProperties);
        tmpAllProperties.addAll(booleanProperties);
        this.integerProperties = Collections.unmodifiableSet(integerProperties);
        tmpAllProperties.addAll(integerProperties);
        this.multipleStringProperties = Collections.unmodifiableSet(multipleStringProperties);
        tmpAllProperties.addAll(multipleStringProperties);
        this.separator = separator;
        this.nameFormat = nameFormat;
        this.nameProperties = Collections.unmodifiableList(nameProperties);
        tmpAllProperties.addAll(nameProperties);
        allProperties = Collections.unmodifiableCollection(tmpAllProperties);
    }
    
    public final static PersonConfiguration MENTEE_CONFIGURATION = 
            new PersonConfiguration(Set.of("Anglais"), 
            Set.of("Promotion"), Set.of("Activités et métiers","Motivation"), ";", 
            "%s %s (X%s)", List.of("Prénom","Nom","Promotion"));
    
    public final static PersonConfiguration MENTOR_CONFIGURATION = 
            new PersonConfiguration(Set.of("Anglais"), 
            Set.of("Promotion"), Set.of("Activités et métiers","Motivation"), ";", 
            "%s %s (X%s)", List.of("Prénom","Nom","Promotion"));
}