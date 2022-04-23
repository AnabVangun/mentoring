package mentoring.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import mentoring.io.Person;
import mentoring.match.ProgressiveCriterion;
import mentoring.match.NecessaryCriterion;

public class CriteriaConfiguration {
    private Collection<ProgressiveCriterion<Person, Person>> progressiveCriteria;
    private List<NecessaryCriterion<Person, Person>> necessaryCriteria;
    private static final String MENTEE_ENGLISH_HEADER = "Anglais";
    private static final String MENTOR_ENGLISH_HEADER = "Anglais";
    private static final int YEAR_WEIGHT = 10;
    private static final int MENTEE_YEAR = 2020;
    private static final String MENTOR_YEAR_HEADER = "Promotion";
    private static final String MENTEE_ACTIVITIES_HEADER = "Activités et métiers";
    private static final String MENTOR_ACTIVITIES_HEADER = "Activités et métiers";
    private static final String MENTEE_MOTIVATION_HEADER = "Motivation";
    private static final String MENTOR_MOTIVATION_HEADER = "Motivation";
    
    public Collection<ProgressiveCriterion<Person, Person>> getProgressiveCriteria(){
        return Collections.unmodifiableCollection(progressiveCriteria);
    }
    
    public List<NecessaryCriterion<Person, Person>> getNecessaryCriteria(){
        return Collections.unmodifiableList(necessaryCriteria);
    }
    
    public CriteriaConfiguration(){
        this.progressiveCriteria = new ArrayList<>();
        this.necessaryCriteria = new ArrayList<>();
        this.necessaryCriteria.add((mentee, mentor) -> {
            return BaseCriteria.logicalNotAOrB(
                mentee.getBooleanProperty(MENTEE_ENGLISH_HEADER), 
                mentor.getBooleanProperty(MENTOR_ENGLISH_HEADER));
        });
        this.progressiveCriteria.add((mentee, mentor) -> {
            return YEAR_WEIGHT * (MENTEE_YEAR - mentor.getIntegerProperty(MENTOR_YEAR_HEADER));
        });
        this.progressiveCriteria.add((mentee, mentor) -> {
           Set<String> menteeActivities = mentee.getMultipleStringProperty(MENTEE_ACTIVITIES_HEADER);
           Set<String> mentorActivities = mentor.getMultipleStringProperty(MENTOR_ACTIVITIES_HEADER);
           return BaseCriteria.computeSetProximity(menteeActivities, mentorActivities);
        });
        this.progressiveCriteria.add((mentee, mentor) -> {
           Set<String> menteeMotivation = mentee.getMultipleStringProperty(MENTEE_MOTIVATION_HEADER);
           Set<String> mentorMotivation = mentor.getMultipleStringProperty(MENTOR_MOTIVATION_HEADER);
           return BaseCriteria.computeSetProximity(menteeMotivation, mentorMotivation);
        });
    }
    
    
}
