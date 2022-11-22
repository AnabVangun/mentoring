package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;

/**
 * Example person configurations for test cases.
 */
public enum PojoPersonConfiguration implements PersonConfiguration{
    /** Configuration used in simple test cases. */
    TEST_CONFIGURATION(Set.of(new PropertyName<>("Anglais", PropertyType.BOOLEAN), 
            new PropertyName<>("Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyName<>("M�tiers","Activit�s et m�tiers", PropertyType.STRING), 
                    new SetPropertyName<>("Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Pr�nom","Nom","Promotion")),
    /** Configuration used for real mentee data in the 2021 data set. */
    MENTEE_CONFIGURATION_REAL_DATA(Set.of(
            new PropertyName<>("Promotion","Promotion, cycle", PropertyType.INTEGER),
            new PropertyName<>("Maturit�","maturit�", PropertyType.INTEGER),
            new PropertyName<>("Email","E-mail", PropertyType.STRING)),
            Set.of(new SetPropertyName<>("M�tiers","r�sum� m�tier secteur", PropertyType.STRING), 
                    new SetPropertyName<>("Langue","Option : langue pr�f�rentielle", PropertyType.STRING)),
            ",", "%s %s (X%s)", List.of("Pr�nom", "NOM", "Promotion, cycle")),
    /** Configuration used for real mentor data in the 2021 data set. */
    MENTOR_CONFIGURATION_REAL_DATA(Set.of(
            new PropertyName<>("Anglophone","anglophone", PropertyType.BOOLEAN),
            new PropertyName<>("Promotion","Promotion (X09, ...)", PropertyType.STRING),
            new PropertyName<>("Email","Adresse email", PropertyType.STRING)),
            Set.of(new SetPropertyName<>("M�tiers","R�sum� m�tier secteur", PropertyType.STRING)),
            ",", "%s %s (%s)", List.of("Pr�nom", "Nom", "Promotion (X09, ...)")),
    /**Configuration used for real mentee data in the preprocessed 2022 data set. */
    MENTEE_CONFIGURATION_2023_DATA(
            Set.of(new PropertyName<>("Email", "email", PropertyType.STRING),
                    new PropertyName<>("Promotion", "Promotion et cycle (par exemple X20)", PropertyType.YEAR),
                    new PropertyName<>("Maturit�", 
                            "Quel est le degr� de maturit� de ton projet acad�mique et professionnel actuel ?", 
                            PropertyType.INTEGER),
                    new PropertyName<>("Echanges", 
                            "Tiens-tu absolument � pouvoir rencontrer ton mentor en face � face en r�gion parisienne ?",
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            Set.of(
                    new IndexedPropertyName<>("Secteur", 
                            "secteur",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyName<>("M�tier", 
                            "metier",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyName<>("Entreprise", 
                            "entreprise",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SetPropertyName<>("Langue", 
                            "Dans quelle(s) langue(s) souhaites-tu �changer ?", 
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            ";", "%s %s (%s)", List.of("Pr�nom", "Nom", "Promotion et cycle (par exemple X20)")),
    /**Configuration used for real mentor data in the preprocessed 2022 data set. */
    MENTOR_CONFIGURATION_2023_DATA(
            Set.of(new PropertyName<>("Email", "email", PropertyType.STRING),
                    new PropertyName<>("Promotion", "Promotion et cycle (par exemple X11)", PropertyType.YEAR),
                    new PropertyName<>("Maturit�", 
                            "Tu pr�f�res mentorer un �tudiant dont le projet professionnel est :", 
                            PropertyType.INTEGER),
                    new PropertyName<>("Echanges", 
                            "Tiens-tu absolument � pouvoir rencontrer ton mentor� en face � face en r�gion parisienne ?",
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            Set.of(
                    new IndexedPropertyName<>("Secteur", 
                            "secteur",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyName<>("M�tier", 
                            "metier",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyName<>("Entreprise", 
                            "entreprise",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SetPropertyName<>("Langue", 
                            "Dans quelle(s) langue(s) acceptes-tu d'�changer ?", 
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            ";", "%s %s (%s)", List.of("Pr�nom", "Nom", "Promotion et cycle (par exemple X11)"));
    
    private final Set<PropertyName<?>> properties;
    private final Set<? extends MultiplePropertyName<?,?>> multipleProperties;
    private final String separator;
    private final String nameFormat;
    private final List<String> nameProperties;
    private final Collection<String> allPropertiesHeaderNames;
    
    private PojoPersonConfiguration(Set<PropertyName<?>> properties, 
            Set<? extends MultiplePropertyName<?,?>> multipleProperties, 
            String separator, String nameFormat,
            List<String> nameProperties){
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
    public Set<PropertyName<?>> getPropertiesNames() {
        return properties;
    }

    @Override
    public Set<? extends MultiplePropertyName<?,?>> getMultiplePropertiesNames() {
        return multipleProperties;
    }

    @Override
    public String getSeparator() {
        return separator;
    }

    @Override
    public String getNameFormat() {
        return nameFormat;
    }

    @Override
    public List<String> getNamePropertiesHeaderNames() {
        return nameProperties;
    }

    @Override
    public Collection<String> getAllPropertiesHeaderNames() {
        return allPropertiesHeaderNames;
    }
}