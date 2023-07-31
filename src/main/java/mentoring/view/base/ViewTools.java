package mentoring.view.base;

import java.util.List;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

/**
 * Set of static functions used to manage views.
 */
public class ViewTools {
    private ViewTools(){/*no-op*/}

    //TODO test and document
    public static FileChooser configureFileChooser(String title, 
            List<FileChooser.ExtensionFilter> filters) {
        Objects.requireNonNull(title);
        FileChooser result = new FileChooser();
        //TODO internationalize strings
        result.setTitle(title);
        result.getExtensionFilters().addAll(filters);
        return result;
    }

    //TODO test and document
    public static void configureButton(Button button, String buttonCaption, 
            EventHandler<ActionEvent> action) {
        Objects.requireNonNull(buttonCaption);
        Objects.requireNonNull(action);
        button.setText(buttonCaption);
        button.setOnAction(action);
    }
}
