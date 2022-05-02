package mentoring.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PersonConfiguration {
    public final Set<String> booleanProperties;
    public final Set<String> integerProperties;
    public final Set<String> stringProperties;
    public final Set<String> multipleStringProperties;
    public final String separator;
    public final String nameFormat;
    public List<String> nameProperties;
    public final Collection<String> allProperties;
    
    public PersonConfiguration(Set<String> booleanProperties, Set<String> integerProperties, 
            Set<String> stringProperties,
            Set<String> multipleStringProperties, String separator, String nameFormat,
            List<String> nameProperties){
        List<String> tmpAllProperties = new ArrayList<>();
        this.booleanProperties = Collections.unmodifiableSet(booleanProperties);
        tmpAllProperties.addAll(booleanProperties);
        this.integerProperties = Collections.unmodifiableSet(integerProperties);
        tmpAllProperties.addAll(integerProperties);
        this.stringProperties = Collections.unmodifiableSet(stringProperties);
        tmpAllProperties.addAll(stringProperties);
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
            Set.of("Promotion"), Set.of(), Set.of("Activités et métiers","Motivation"), ";", 
            "%s %s (X%s)", List.of("Prénom","Nom","Promotion"));
    
    public final static PersonConfiguration MENTOR_CONFIGURATION = 
            new PersonConfiguration(Set.of("Anglais"), 
            Set.of("Promotion"), Set.of(), Set.of("Activités et métiers","Motivation"), ";", 
            "%s %s (X%s)", List.of("Prénom","Nom","Promotion"));
    
    public final static PersonConfiguration MENTEE_CONFIGURATION_REAL_DATA =
            new PersonConfiguration(Set.of(),
            Set.of("Promotion, cycle", "maturité"), Set.of(),
            Set.of("résumé métier secteur", "Option : langue préférentielle"), ",", 
            "%s %s (X%s)", List.of("Prénom", "NOM", "Promotion, cycle"));
    
    public final static PersonConfiguration MENTOR_CONFIGURATION_REAL_DATA =
            new PersonConfiguration(Set.of("anglophone"), Set.of(), 
            Set.of("Promotion (X09, ...)"), Set.of("Résumé métier secteur"), ",",
            "%s %s (%s)", List.of("Prénom", "Nom", "Promotion (X09, ...)"));
}