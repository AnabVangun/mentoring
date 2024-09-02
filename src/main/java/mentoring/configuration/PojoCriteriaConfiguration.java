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
    private static final int YEAR_WEIGHT = 1;
    private static final int MENTEE_YEAR = 2020;
    private static final String YEAR_PROPERTY = "Promotion";
    private static final String ACTIVITIES_PROPERTY = "Métiers";
    private static final String MOTIVATION_PROPERTY = "Motivation";
    private static final String SECTOR2024 = "Secteur";
    private static final String MATURITY2024 = "Maturité";
    private static final String JOB2024 = "Métier";
    private static final String COMPANY2024 = "Entreprise";
    private static final int MATURITY_WEIGHT = 10;
    private static final Map<String, Integer> MEETING_INDICES = Map.of("oui",0,"sipossible",1,
            "pasnecessairement",2);
    private static final String MEETING2024 = "Echanges";
    private static final int MEETING_WEIGHT = 10;
    private static final int INTEREST_AMPLIFIER2024 = 3;
    private static final int SECTOR_MENTEE_WEIGHT = 150;
    private static final int SECTOR_MENTOR_WEIGHT = 9;
    private static final int JOB_MENTEE_WEIGHT = 50;
    private static final int JOB_MENTOR_WEIGHT = 3;
    private static final int COMPANY_MENTEE_WEIGHT = 1;
    private static final int COMPANY_MENTOR_WEIGHT = 0;
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
    /** Configuration used for the preprocessed 2024 data set. */
    public final static PojoCriteriaConfiguration CRITERIA_CONFIGURATION_2024_DATA =
            new PojoCriteriaConfiguration("Criteria configuration for 2024 data",
                    List.of(
                            (mentee, mentor) -> CriteriaToolbox.exponentialDistance(
                                    MEETING_INDICES, 
                                    mentee.getPropertyAs(MEETING2024, String.class), 
                                    mentor.getPropertyAs(MEETING2024, String.class), 
                                    MEETING_WEIGHT),
                            (mentee, mentor ) -> 
                                YEAR_WEIGHT * Math.abs(
                                        mentee.getPropertyAs(
                                                YEAR_PROPERTY, Year.class).getNormalizedYear() - 10 
                                        - mentor.getPropertyAs(
                                                YEAR_PROPERTY, Year.class).getNormalizedYear()), 
                            (mentee, mentor) -> MATURITY_WEIGHT * 
                                    Math.abs(mentee.getPropertyAs(MATURITY2024, 
                                            Integer.class)
                                            - mentor.getPropertyAs(MATURITY2024, 
                                                    Integer.class)), 
                            (mentee, mentor) -> INTEREST_AMPLIFIER2024
                                    * CriteriaToolbox.computePreferenceMapSimilarityScore(
                                            mentee.getPropertyAsMapOf(SECTOR2024, String.class,
                                                    Integer.class),
                                            mentor.getPropertyAsMapOf(SECTOR2024, String.class,
                                                    Integer.class),
                                            SECTOR_MENTEE_WEIGHT, SECTOR_MENTOR_WEIGHT,
                                            3*(SECTOR_MENTEE_WEIGHT+SECTOR_MENTOR_WEIGHT)), 
                            (mentee, mentor) -> INTEREST_AMPLIFIER2024
                                    * CriteriaToolbox.computePreferenceMapSimilarityScore(
                                            mentee.getPropertyAsMapOf(JOB2024, String.class, 
                                                    Integer.class), 
                                            mentor.getPropertyAsMapOf(JOB2024, String.class,
                                                    Integer.class),
                                            JOB_MENTEE_WEIGHT, JOB_MENTOR_WEIGHT,
                                            3*(JOB_MENTEE_WEIGHT+JOB_MENTOR_WEIGHT)), 
                            (mentee, mentor) -> INTEREST_AMPLIFIER2024
                                    * CriteriaToolbox.computePreferenceMapSimilarityScore(
                                            mentee.getPropertyAsMapOf(COMPANY2024, String.class, 
                                                    Integer.class), 
                                            mentor.getPropertyAsMapOf(COMPANY2024, String.class,
                                                    Integer.class),
                                            COMPANY_MENTEE_WEIGHT, COMPANY_MENTOR_WEIGHT,
                                            3*(COMPANY_MENTEE_WEIGHT+COMPANY_MENTOR_WEIGHT))
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
