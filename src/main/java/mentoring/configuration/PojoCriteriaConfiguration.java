package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mentoring.datastructure.Person;
import mentoring.datastructure.Year;
import mentoring.match.ProgressiveCriterion;
import mentoring.match.NecessaryCriterion;

/**
 * Example criteria configurations for test cases.
 */
public final class PojoCriteriaConfiguration extends CriteriaConfiguration<Person,Person>{
    private static final String LANGUAGES_PROPERTY = "Langue";
    private static final String ENGLISH_PROPERTY = "Anglais";
    private static final String ENGLISH_SPEAKING_PROPERTY = "Anglophone";
    private static final int YEAR_WEIGHT = 10;
    private static final int MENTEE_YEAR = 2020;
    private static final String YEAR_PROPERTY = "Promotion";
    private static final String ACTIVITIES_PROPERTY = "Métiers";
    private static final String MOTIVATION_PROPERTY = "Motivation";
    private static final int MATURITE_MAX_2023 = 5;
    private static final String SECTOR2023 = "Secteur";
    private static final String MATURITY2023 = "Maturité";
    private static final String JOB2023 = "Métier";
    private static final String COMPANY2023 = "Entreprise";
    private static final int MATURITY_WEIGHT = 50;
    private static final Map<String, Integer> MEETING_INDICES = Map.of("oui",0,"sipossible",1,
            "pasnecessairement",2);
    private static final String MEETING2023 = "Echanges";
    private static final int MEETING_WEIGHT = 150;
    private static final int INTEREST_AMPLIFIER2023 = 3;
    /** Configuration used in simple test cases. */
    public final static PojoCriteriaConfiguration CRITERIA_CONFIGURATION = 
            new PojoCriteriaConfiguration("Test criteria configuration", List.of(
                    (mentee, mentor) ->
                            YEAR_WEIGHT * (MENTEE_YEAR
                                    - mentor.getPropertyAs(YEAR_PROPERTY, Integer.class)),
                    (mentee, mentor) -> {
                        Set<String> menteeActivities = mentee
                                .getPropertyAsSetOf(ACTIVITIES_PROPERTY, String.class);
                        Set<String> mentorActivities = mentor
                                .getPropertyAsSetOf(ACTIVITIES_PROPERTY, String.class);
                        return CriteriaToolbox.computeSetDistance(menteeActivities, 
                                mentorActivities);
                    }, (mentee, mentor) -> {
                        Set<String> menteeMotivation =
                                mentee.getPropertyAsSetOf(MOTIVATION_PROPERTY, 
                                        String.class);
                        Set<String> mentorMotivation =
                                mentor.getPropertyAsSetOf(MOTIVATION_PROPERTY, 
                                        String.class);
                        return CriteriaToolbox.computeSetDistance(menteeMotivation, 
                                mentorMotivation);
                    }), List.of((mentee, mentor) -> CriteriaToolbox.logicalNotAOrB(
                            mentee.getPropertyAs(ENGLISH_PROPERTY, Boolean.class),
                            mentor.getPropertyAs(ENGLISH_PROPERTY, Boolean.class))));
    /** Configuration used for real 2021 data. */
    public final static PojoCriteriaConfiguration CRITERIA_CONFIGURATION_REAL_DATA =
            new PojoCriteriaConfiguration("Criteria configuration for real data",
                List.of((mentee, mentor) -> {
                    return YEAR_WEIGHT 
                        * (mentee.getPropertyAs(YEAR_PROPERTY, Integer.class) 
                            - mentor.getPropertyAs(YEAR_PROPERTY, 
                                    Year.class).getNormalizedYear());
                    },
                    (mentee, mentor) -> {
                        Set<String> menteeActivities = 
                                mentee.getPropertyAsSetOf(ACTIVITIES_PROPERTY, 
                                        String.class);
                        Set<String> mentorActivities = 
                                mentor.getPropertyAsSetOf(ACTIVITIES_PROPERTY, 
                                        String.class);
                        return CriteriaToolbox.computeSetDistance(menteeActivities, 
                                mentorActivities);
                }),
                List.of((mentee, mentor) ->
                        (mentee.getPropertyAsSetOf(LANGUAGES_PROPERTY, String.class)
                            .contains("Français")
                            || mentor.getPropertyAs(ENGLISH_SPEAKING_PROPERTY, 
                                    Boolean.class))));
    /** Configuration used for the preprocessed 2022 data set. */
    public final static PojoCriteriaConfiguration CRITERIA_CONFIGURATION_2023_DATA =
            new PojoCriteriaConfiguration("Criteria configuration for 2023 data",
                    List.of(
                            (mentee, mentor) -> CriteriaToolbox.exponentialDistance(
                                    MEETING_INDICES, 
                                    mentee.getPropertyAs(MEETING2023, String.class), 
                                    mentor.getPropertyAs(MEETING2023, String.class), 
                                    MEETING_WEIGHT),
                            (mentee, mentor ) -> 
                                YEAR_WEIGHT * Math.abs(
                                        mentee.getPropertyAs(
                                                YEAR_PROPERTY, Year.class).getNormalizedYear() - 10 
                                        - mentor.getPropertyAs(
                                                YEAR_PROPERTY, Year.class).getNormalizedYear()), 
                            (mentee, mentor) -> MATURITY_WEIGHT * 
                                    Math.abs(mentee.getPropertyAs(MATURITY2023, 
                                            Integer.class)
                                            - mentor.getPropertyAs(MATURITY2023, 
                                                    Integer.class)), 
                            (mentee, mentor) -> INTEREST_AMPLIFIER2023
                                    * CriteriaToolbox.computeWeightedAsymetricMapDistance(
                                    mentee.getPropertyAsMapOf(SECTOR2023, String.class, 
                                            Integer.class), 
                                    mentor.getPropertyAsSetOf(SECTOR2023, String.class),
                                    mentee.getPropertyAs(MATURITY2023, Integer.class)
                                            / MATURITE_MAX_2023), 
                            (mentee, mentor) -> INTEREST_AMPLIFIER2023
                                    * CriteriaToolbox.computeWeightedAsymetricMapDistance(
                                    mentee.getPropertyAsMapOf(JOB2023, String.class, 
                                            Integer.class), 
                                    mentor.getPropertyAsSetOf(JOB2023, String.class),
                                    mentee.getPropertyAs(MATURITY2023, Integer.class)
                                            / MATURITE_MAX_2023), 
                            (mentee, mentor) -> INTEREST_AMPLIFIER2023
                                    * CriteriaToolbox.computeWeightedAsymetricMapDistance(
                                    mentee.getPropertyAsMapOf(COMPANY2023, String.class, 
                                            Integer.class), 
                                    mentor.getPropertyAsSetOf(COMPANY2023, String.class),
                                    mentee.getPropertyAs(MATURITY2023, Integer.class)
                                            / MATURITE_MAX_2023)
                    ),
                    List.of((mentee, mentor) -> {
                        boolean found = false;
                        Set<String> mentorLanguages = mentor.getPropertyAsSetOf(
                                LANGUAGES_PROPERTY, String.class);
                        for (String language : mentee.getPropertyAsSetOf(
                                LANGUAGES_PROPERTY, String.class)){
                            if (mentorLanguages.contains(language)){
                                found = true;
                                break;
                            }
                        }
                        return found;
            }));
    
    @Override
    public List<CriteriaConfiguration<Person, Person>> values(){
        return Collections.unmodifiableList(List.of(CRITERIA_CONFIGURATION,
                    CRITERIA_CONFIGURATION_2023_DATA,CRITERIA_CONFIGURATION_REAL_DATA));
    }
    
    private final Collection<ProgressiveCriterion<Person, Person>> progressiveCriteria;
    private final List<NecessaryCriterion<Person, Person>> necessaryCriteria;
    
    @Override
    public Collection<ProgressiveCriterion<Person, Person>> getProgressiveCriteria(){
        return Collections.unmodifiableCollection(progressiveCriteria);
    }
    
    @Override
    public List<NecessaryCriterion<Person, Person>> getNecessaryCriteria(){
        return Collections.unmodifiableList(necessaryCriteria);
    }
    
    public PojoCriteriaConfiguration(String configurationName,
            Collection<ProgressiveCriterion<Person, Person>> progressiveCriteria,
            List<NecessaryCriterion<Person, Person>> necessaryCriteria){
        super(configurationName);
        this.progressiveCriteria = progressiveCriteria;
        this.necessaryCriteria = necessaryCriteria;
    }
}
