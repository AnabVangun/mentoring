package mentoring.configuration;

import java.util.List;
import java.util.Set;
import mentoring.datastructure.IndexedPropertyDescription;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyDescription;
import mentoring.datastructure.SimplePropertyDescription;

/**
 * Example person configurations for test cases.
 */
public enum PojoPersonConfiguration{
    /** Configuration used in simple test cases. */
    TEST_CONFIGURATION(new PersonConfiguration("Test configuration",
            Set.of(new SimplePropertyDescription<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new SimplePropertyDescription<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyDescription<>("M�tiers","Activit�s et m�tiers", PropertyType.STRING), 
                    new SetPropertyDescription<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Pr�nom","Nom","Promotion"))),
    TEST_CONFIGURATION_2(new PersonConfiguration("Test configuration 2",
            Set.of(new SimplePropertyDescription<>("Name", "Nom", PropertyType.STRING),
                    new SimplePropertyDescription<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new SimplePropertyDescription<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyDescription<>("M�tiers","Activit�s et m�tiers", PropertyType.STRING), 
                    new SetPropertyDescription<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Pr�nom","Nom","Promotion"))),
    /**Configuration used for real mentee data in the preprocessed 2023 data set. */
    MENTEE_CONFIGURATION_2024_DATA(new PersonConfiguration("Mentee configuration for 2024 data",
            Set.of(new SimplePropertyDescription<>("Email", "email", PropertyType.STRING),
                    new SimplePropertyDescription<>("Portable", "Portable", PropertyType.STRING),
                    new SimplePropertyDescription<>("Promotion", "Promotion et cycle (normalement X22)", 
                            PropertyType.YEAR),
                    new SimplePropertyDescription<>("Maturit�", 
                            "Quel est le degr� de maturit� de ton projet acad�mique et professionnel actuel ?", 
                            PropertyType.INTEGER),
                    new SimplePropertyDescription<>("Explication de maturit�", "Peux-tu expliquer ta r�ponse ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Echanges", 
                            "Tiens-tu absolument � pouvoir rencontrer ton mentor en face � face en r�gion parisienne ?",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SimplePropertyDescription<>("Autres secteurs ?",
                            "Si tu as r�pondu \"Autres\", quel secteur d'activit� aurais-tu vouloir voir appara�tre ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Autres m�tiers ?",
                            "Si tu as r�pondu \"Autre\", quel m�tier aurais-tu vouloir voir appara�tre ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Attentes",
                            "Qu�attends-tu de cette relation mentorale et de ton mentor ?",
                            PropertyType.STRING)),
            Set.of(new IndexedPropertyDescription<>("Secteur", 
                            "secteur",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyDescription<>("M�tier", 
                            "metier",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyDescription<>("Entreprise", 
                            "entreprise",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SetPropertyDescription<>("Langue", 
                            "Dans quelle(s) langue(s) souhaites-tu �changer ?", 
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            ";", "%s %s (%s)", List.of("Pr�nom", "Nom", "Promotion et cycle (normalement X22)"))),
    /**Configuration used for real mentor data in the preprocessed 2023 data set. */
    MENTOR_CONFIGURATION_2024_DATA(new PersonConfiguration("Mentor configuration for 2024 data",
            Set.of(new SimplePropertyDescription<>("Email", "email", PropertyType.STRING),
                    new SimplePropertyDescription<>("Portable", "Portable", PropertyType.STRING),
                    new SimplePropertyDescription<>("Motivation", "Pourquoi souhaites-tu �tre mentor ?", 
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Promotion", "Promotion et cycle (par exemple X11)", 
                            PropertyType.YEAR),
                    new SimplePropertyDescription<>("Maturit�", 
                            "Tu pr�f�res mentorer un �tudiant dont le projet professionnel est :", 
                            PropertyType.INTEGER),
                    new SimplePropertyDescription<>("Autres secteurs ?",
                            "Si tu as r�pondu \"Autres\", quel secteur d'activit� aurais-tu vouloir voir appara�tre ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Autres m�tiers ?",
                            "Si tu as r�pondu \"Autre\", quel m�tier aurais-tu vouloir voir appara�tre ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Echanges", 
                            "Tiens-tu absolument � pouvoir rencontrer ton mentor� en face � face en r�gion parisienne ?",
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            Set.of(new IndexedPropertyDescription<>("Secteur", 
                            "secteur",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyDescription<>("M�tier", 
                            "metier",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyDescription<>("Entreprise", 
                            "entreprise",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SetPropertyDescription<>("Langue", 
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