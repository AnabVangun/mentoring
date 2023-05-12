package mentoring.viewmodel.datastructure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javax.inject.Inject;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
import mentoring.match.Matches;

/**
 * Viewmodel responsible for representing a {@link Matches} object.
 * 
 * @param <Mentee> type of the first element of a {@link Match}.
 * @param <Mentor> type of the second element of a {@link Match}.
 */
public class MatchesViewModel<Mentee, Mentor> {
    //FIXME: this potentially smells, TableColumn is more a View thing than a ViewModel...
    public final ObservableList<TableColumn<MatchViewModel<Mentee, Mentor>, String>> headerContent;
    public final ObservableList<MatchViewModel<Mentee, Mentor>> items;
    
    @Inject
    MatchesViewModel(ResultConfiguration<Mentee, Mentor> configuration,
            Matches<Mentee, Mentor> matches){
        headerContent = FXCollections.unmodifiableObservableList(prepareHeader(configuration));
        items = FXCollections.unmodifiableObservableList(prepareItems(configuration, matches));
    }
    
    private static <Mentee, Mentor> 
            ObservableList<TableColumn<MatchViewModel<Mentee, Mentor>, String>>
            prepareHeader(ResultConfiguration<Mentee, Mentor> configuration) {
        ObservableList<TableColumn<MatchViewModel<Mentee, Mentor>, String>> result 
                = FXCollections.observableArrayList();
        for (String headerItem : configuration.getResultHeader()){
            result.add(new TableColumn<>(headerItem));
        }
        return result;
    }
            
    private static <Mentee, Mentor> ObservableList<MatchViewModel<Mentee, Mentor>> prepareItems(
            ResultConfiguration<Mentee, Mentor> configuration, Matches<Mentee, Mentor> matches) {
        ObservableList<MatchViewModel<Mentee, Mentor>> result 
                = FXCollections.observableArrayList();
        for (Match<Mentee, Mentor> match : matches){
            result.add(new MatchViewModel<>(configuration, match));
        }
        return result;
    }
}
