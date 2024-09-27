package mentoring.configuration;

import java.util.List;
import java.util.Set;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;
import mentoring.datastructure.SimplePropertyName;

/**
 * Example person configurations for test cases.
 */
public enum PojoPersonConfiguration{
    /** Configuration used in simple test cases. */
    TEST_CONFIGURATION(new PersonConfiguration("Test configuration",
            Set.of(new SimplePropertyName<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new SimplePropertyName<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyName<>("M�tiers","Activit�s et m�tiers", PropertyType.STRING), 
                    new SetPropertyName<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Pr�nom","Nom","Promotion"))),
    TEST_CONFIGURATION_2(new PersonConfiguration("Test configuration 2",
            Set.of(new SimplePropertyName<>("Name", "Nom", PropertyType.STRING),
                    new SimplePropertyName<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new SimplePropertyName<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyName<>("M�tiers","Activit�s et m�tiers", PropertyType.STRING), 
                    new SetPropertyName<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Pr�nom","Nom","Promotion"))),
    /**Configuration used for real mentee data in the preprocessed 2023 data set. */
    MENTEE_CONFIGURATION_2024_DATA(new PersonConfiguration("Mentee configuration for 2024 data",
            Set.of(new SimplePropertyName<>("Email", "email", PropertyType.STRING),
                    new SimplePropertyName<>("Portable", "Portable", PropertyType.STRING),
                    new SimplePropertyName<>("Promotion", "Promotion et cycle (normalement X22)", 
                            PropertyType.YEAR),
                    new SimplePropertyName<>("Maturit�", 
                            "Quel est le degr� de maturit� de ton projet acad�mique et professionnel actuel ?", 
                            PropertyType.INTEGER),
                    new SimplePropertyName<>("Explication de maturit�", "Peux-tu expliquer ta r�ponse ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Echanges", 
                            "Tiens-tu absolument � pouvoir rencontrer ton mentor en face � face en r�gion parisienne ?",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SimplePropertyName<>("Autres secteurs ?",
                            "Si tu as r�pondu \"Autres\", quel secteur d'activit� aurais-tu vouloir voir appara�tre ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Autres m�tiers ?",
                            "Si tu as r�pondu \"Autre\", quel m�tier aurais-tu vouloir voir appara�tre ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Attentes",
                            "Qu�attends-tu de cette relation mentorale et de ton mentor ?",
                            PropertyType.STRING)),
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
            ";", "%s %s (%s)", List.of("Pr�nom", "Nom", "Promotion et cycle (normalement X22)"))),
    /**Configuration used for real mentor data in the preprocessed 2023 data set. */
    MENTOR_CONFIGURATION_2024_DATA(new PersonConfiguration("Mentor configuration for 2024 data",
            Set.of(new SimplePropertyName<>("Email", "email", PropertyType.STRING),
                    new SimplePropertyName<>("Portable", "Portable", PropertyType.STRING),
                    new SimplePropertyName<>("Motivation", "Pourquoi souhaites-tu �tre mentor ?", 
                            PropertyType.STRING),
                    new SimplePropertyName<>("Promotion", "Promotion et cycle (par exemple X11)", 
                            PropertyType.YEAR),
                    new SimplePropertyName<>("Maturit�", 
                            "Tu pr�f�res mentorer un �tudiant dont le projet professionnel est :", 
                            PropertyType.INTEGER),
                    new SimplePropertyName<>("Autres secteurs ?",
                            "Si tu as r�pondu \"Autres\", quel secteur d'activit� aurais-tu vouloir voir appara�tre ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Autres m�tiers ?",
                            "Si tu as r�pondu \"Autre\", quel m�tier aurais-tu vouloir voir appara�tre ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Echanges", 
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