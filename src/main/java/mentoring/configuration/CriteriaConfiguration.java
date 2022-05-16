package mentoring.configuration;

import java.util.Collection;
import java.util.List;
import mentoring.match.NecessaryCriterion;
import mentoring.match.ProgressiveCriterion;

public interface CriteriaConfiguration<Mentee,Mentor> {
    
    Collection<ProgressiveCriterion<Mentee, Mentor>> getProgressiveCriteria();
    
    List<NecessaryCriterion<Mentee, Mentor>> getNecessaryCriteria();
}
