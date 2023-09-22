package mentoring.viewmodel.match;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
import mentoring.viewmodel.datastructure.DataViewModel;

/**
 * ViewModel responsible for representing a {@link Match} object.
 * 
 * @param <Mentee> type of the first element of a {@link Match}.
 * @param <Mentor> type of the second element of a {@link Match}.
 */
public class MatchViewModel<Mentee, Mentor> {
    private final ObservableMap<String, String> match;
    private final Match<Mentee, Mentor> data;
    
    protected MatchViewModel(ResultConfiguration<Mentee, Mentor> configuration, 
            Match<Mentee, Mentor> match){
        ObservableMap<String, String> modifiableMatch = 
                FXCollections.observableMap(configuration.getResultMap(match));
        this.match = FXCollections.unmodifiableObservableMap(modifiableMatch);
        data = match;
    }
    
    public ObservableMap<String, String> observableMatch(){
        return match;
    }
    
    public Match<Mentee, Mentor> getData(){
        return data;
    }
    
    /**
     * Tests whether the encapsulated match contains a given mentee.
     * @param mentee to check
     * @return true if the match contains the mentee
     */
    public boolean containsMentee(DataViewModel<Mentee> mentee){
        return data.getMentee().equals(mentee.getData());
    }
    
    /**
     * Tests whether the encapsulated match contains a given mentor.
     * @param mentor to check
     * @return true if the match contains the mentor
     */
    public boolean containsMentor(DataViewModel<Mentee> mentor){
        return data.getMentor().equals(mentor.getData());
    }
}
