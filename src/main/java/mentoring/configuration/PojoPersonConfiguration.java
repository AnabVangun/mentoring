package mentoring.configuration;

import java.util.List;
import java.util.Set;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;

/**
 * Example person configurations for test cases.
 */
public enum PojoPersonConfiguration{
    /** Configuration used in simple test cases. */
    TEST_CONFIGURATION(new PersonConfiguration("Test configuration",
            Set.of(new PropertyName<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new PropertyName<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyName<>("M�tiers","Activit�s et m�tiers", PropertyType.STRING), 
                    new SetPropertyName<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Pr�nom","Nom","Promotion"))),
    @Deprecated
    /** Configuration used for real mentee data in the 2021 data set. */
    MENTEE_CONFIGURATION_REAL_DATA(new PersonConfiguration("Mentee configuration for 2022 data",
            Set.of(new PropertyName<>("Promotion","Promotion, cycle", PropertyType.INTEGER),
                    new PropertyName<>("Maturit�","maturit�", PropertyType.INTEGER),
                    new PropertyName<>("Email","E-mail", PropertyType.STRING)),
            Set.of(new SetPropertyName<>("M�tiers","r�sum� m�tier secteur", PropertyType.STRING), 
                    new SetPropertyName<>("Langue","Option : langue pr�f�rentielle", 
                            PropertyType.STRING)),
            ",", "%s %s (X%s)", List.of("Pr�nom", "NOM", "Promotion, cycle"))),
    @Deprecated
    /** Configuration used for real mentor data in the 2021 data set. */
    MENTOR_CONFIGURATION_REAL_DATA(new PersonConfiguration("Mentor configuration for 2022 data",
            Set.of(new PropertyName<>("Anglophone","anglophone", PropertyType.BOOLEAN),
                    new PropertyName<>("Promotion","Promotion (X09, ...)", PropertyType.STRING),
                    new PropertyName<>("Email","Adresse email", PropertyType.STRING)),
            Set.of(new SetPropertyName<>("M�tiers","R�sum� m�tier secteur", PropertyType.STRING)),
            ",", "%s %s (%s)", List.of("Pr�nom", "Nom", "Promotion (X09, ...)"))),
    /**Configuration used for real mentee data in the preprocessed 2022 data set. */
    MENTEE_CONFIGURATION_2023_DATA(new PersonConfiguration("Mentee configuration for 2023 data",
            Set.of(new PropertyName<>("Email", "email", PropertyType.STRING),
                    new PropertyName<>("Promotion", "Promotion et cycle (par exemple X20)", 
                            PropertyType.YEAR),
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
            ";", "%s %s (%s)", List.of("Pr�nom", "Nom", "Promotion et cycle (par exemple X20)"))),
    /**Configuration used for real mentor data in the preprocessed 2022 data set. */
    MENTOR_CONFIGURATION_2023_DATA(new PersonConfiguration("Mentor configuration for 2023 data",
            Set.of(new PropertyName<>("Email", "email", PropertyType.STRING),
                    new PropertyName<>("Promotion", "Promotion et cycle (par exemple X11)", 
                            PropertyType.YEAR),
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
            ";", "%s %s (%s)", List.of("Pr�nom", "Nom", "Promotion et cycle (par exemple X11)")));
    
    private final PersonConfiguration configuration;
    
    private PojoPersonConfiguration(PersonConfiguration configuration){
        this.configuration = configuration;
    }
    
    public PersonConfiguration getConfiguration(){
        return this.configuration;
    }
}