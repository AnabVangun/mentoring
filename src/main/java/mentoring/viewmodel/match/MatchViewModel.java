package mentoring.viewmodel.match;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javax.inject.Inject;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;

/**
 * Viewmodel responsible for representing a {@link Match} object.
 * 
 * @param <Mentee> type of the first element of a {@link Match}.
 * @param <Mentor> type of the second element of a {@link Match}.
 */
public class MatchViewModel<Mentee, Mentor> {
    private final ObservableMap<String, String> match;
    private final ObservableMap<String, String> modifiableMatch;
    
    @Inject
    protected MatchViewModel(ResultConfiguration<Mentee, Mentor> configuration, 
            Match<Mentee, Mentor> match){
        modifiableMatch = FXCollections.observableMap(configuration.getResultMap(match));
        this.match = FXCollections.unmodifiableObservableMap(modifiableMatch);
    }
    
    public ObservableMap<String, String> observableMatch(){
        return match;
    }
}
