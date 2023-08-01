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

    /**
     * Create and configure a {@link FileChooser} object.
     * @param title for the chooser window
     * @param filters used to help select the appropriate file types
     * @return a configured object ready to be displayed
     */
    public static FileChooser createFileChooser(String title, 
            List<FileChooser.ExtensionFilter> filters) {
        Objects.requireNonNull(title);
        FileChooser result = new FileChooser();
        result.setTitle(title);
        result.getExtensionFilters().addAll(filters);
        return result;
    }

    /**
     * Configure a {@link Button} object.
     * @param button to configure
     * @param caption to set as the button's label 
     * @param action to perform when the button is fired
     */
    public static void configureButton(Button button, String caption, 
            EventHandler<ActionEvent> action) {
        Objects.requireNonNull(caption);
        Objects.requireNonNull(action);
        button.setText(caption);
        button.setOnAction(action);
    }
}
