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
            Set.of("Promotion"), Set.of(), Set.of("Activit�s et m�tiers","Motivation"), ";", 
            "%s %s (X%s)", List.of("Pr�nom","Nom","Promotion"));
    
    public final static PersonConfiguration MENTOR_CONFIGURATION = 
            new PersonConfiguration(Set.of("Anglais"), 
            Set.of("Promotion"), Set.of(), Set.of("Activit�s et m�tiers","Motivation"), ";", 
            "%s %s (X%s)", List.of("Pr�nom","Nom","Promotion"));
    
    public final static PersonConfiguration MENTEE_CONFIGURATION_REAL_DATA =
            new PersonConfiguration(Set.of(),
            Set.of("Promotion, cycle", "maturit�"), Set.of(),
            Set.of("r�sum� m�tier secteur", "Option : langue pr�f�rentielle"), ",", 
            "%s %s (X%s)", List.of("Pr�nom", "NOM", "Promotion, cycle"));
    
    public final static PersonConfiguration MENTOR_CONFIGURATION_REAL_DATA =
            new PersonConfiguration(Set.of("anglophone"), Set.of(), 
            Set.of("Promotion (X09, ...)"), Set.of("R�sum� m�tier secteur"), ",",
            "%s %s (%s)", List.of("Pr�nom", "Nom", "Promotion (X09, ...)"));
}