package mentoring.viewmodel.datastructure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public final ObservableList<String> line;
    private final ObservableList<String> modifiableLine;
    
    @Inject
    MatchViewModel(ResultConfiguration<Mentee, Mentor> configuration, Match<Mentee, Mentor> match){
        modifiableLine = FXCollections.observableArrayList(configuration.getResultLine(match));
        line = FXCollections.unmodifiableObservableList(modifiableLine);
    }
    
}
