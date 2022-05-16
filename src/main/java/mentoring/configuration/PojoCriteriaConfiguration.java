package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.Person;
import mentoring.match.ProgressiveCriterion;
import mentoring.match.NecessaryCriterion;

public enum PojoCriteriaConfiguration implements CriteriaConfiguration<Person,Person>{
    CRITERIA_CONFIGURATION(List.of(
        (mentee, mentor) ->
            Constants.YEAR_WEIGHT * 
                (Constants.MENTEE_YEAR - mentor.getIntegerProperty(Constants.MENTOR_YEAR_HEADER)),
        (mentee, mentor) -> {
            Set<String> menteeActivities = mentee.getMultipleStringProperty(Constants.MENTEE_ACTIVITIES_HEADER);
            Set<String> mentorActivities = mentor.getMultipleStringProperty(Constants.MENTOR_ACTIVITIES_HEADER);
            return CriteriaToolbox.computeSetProximity(menteeActivities, mentorActivities);
        },
        (mentee, mentor) -> {
            Set<String> menteeMotivation = mentee.getMultipleStringProperty(Constants.MENTEE_MOTIVATION_HEADER);
            Set<String> mentorMotivation = mentor.getMultipleStringProperty(Constants.MENTOR_MOTIVATION_HEADER);
            return CriteriaToolbox.computeSetProximity(menteeMotivation, mentorMotivation);
        }), 
    List.of((mentee, mentor) -> CriteriaToolbox.logicalNotAOrB(
            mentee.getBooleanProperty(Constants.MENTEE_ENGLISH_HEADER), 
            mentor.getBooleanProperty(Constants.MENTOR_ENGLISH_HEADER)))),
    CRITERIA_CONFIGURATION_REAL_DATA(            
        List.of((mentee, mentor) -> {
            //age criteria
            return Constants.YEAR_WEIGHT 
                * (mentee.getIntegerProperty("Promotion") 
                    - CriteriaToolbox.getYear(mentor.getStringProperty("Promotion")));
            },
            (mentee, mentor) -> {
                Set<String> menteeActivities = mentee.getMultipleStringProperty("Métiers");
                Set<String> mentorActivities = mentor.getMultipleStringProperty("Métiers");
                return CriteriaToolbox.computeSetProximity(menteeActivities, mentorActivities);
        }),
        List.of((mentee, mentor) -> {
            return (mentee.getMultipleStringProperty("Langue")
                    .contains("Français")
                    || mentor.getBooleanProperty("Anglophone"));
        })
    );
    
    private static class Constants {
        private static final String MENTEE_ENGLISH_HEADER = "Anglais";
        private static final String MENTOR_ENGLISH_HEADER = "Anglais";
        private static final int YEAR_WEIGHT = 10;
        private static final int MENTEE_YEAR = 2020;
        private static final String MENTOR_YEAR_HEADER = "Promotion";
        private static final String MENTEE_ACTIVITIES_HEADER = "Métiers";
        private static final String MENTOR_ACTIVITIES_HEADER = "Métiers";
        private static final String MENTEE_MOTIVATION_HEADER = "Motivation";
        private static final String MENTOR_MOTIVATION_HEADER = "Motivation";
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
    
    private PojoCriteriaConfiguration(
        Collection<ProgressiveCriterion<Person, Person>> progressiveCriteria,
        List<NecessaryCriterion<Person, Person>> necessaryCriteria){
        this.progressiveCriteria = progressiveCriteria;
        this.necessaryCriteria = necessaryCriteria;
    }
}
