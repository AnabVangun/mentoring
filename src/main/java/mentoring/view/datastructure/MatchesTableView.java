package mentoring.view.datastructure;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javax.inject.Inject;
import mentoring.viewmodel.datastructure.MatchViewModel;
import mentoring.viewmodel.datastructure.MatchesViewModel;

/**
 * View responsible for displaying a table of {@link MatchViewModel} object.
 * 
 * @param <Mentee> type of the first element of a {@link Match}.
 * @param <Mentor> type of the second element of a {@link Match}.
 */
public class MatchesTableView<Mentee, Mentor> implements Initializable {
    
    @FXML
    private TableView<MatchViewModel<Mentee, Mentor>> table;
    
    private final MatchesViewModel<Mentee, Mentor> vm;
    
    @Inject
    MatchesTableView(MatchesViewModel<Mentee, Mentor> vm){
        this.vm = vm;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        table.getColumns().addAll(vm.headerContent);
        table.setItems(vm.items);
    }
    
}
