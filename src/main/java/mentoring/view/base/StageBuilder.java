package mentoring.view.base;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Class used to build {@link Stage} objects. A single builder can build several unrelated objects:
 * the internal state is reinitialised after each creation. The minimal use of this class relies 
 * only on its no-argument constructor and {@link #build(javafx.scene.Parent) }: all other methods
 * help tweak the built instance but are optional.
 * This class is not thread-safe.
 */
public class StageBuilder {
    private Stage stage = new Stage();
    /**
     * Finalise the creation of a new {@link Stage} instance. After this, the builder instance 
     * is reinitialised.
     * @param root the root of the new instance
     * @return the newly created instance
     */
    public Stage build(Parent root){
        Scene scene = new Scene(root);
        //TODO extract main CSS as global parameter
        scene.getStylesheets().add(getClass().getResource("/mentoring/styles.css").toExternalForm());
        stage.setScene(scene);
        Stage result = stage;
        stage = new Stage();
        return result;
    }
    
    /**
     * Add a title to the stage under construction.
     * @param title for the stage
     * @return this builder instance (Fluent API)
     */
    public StageBuilder withTitle(String title){
        stage.setTitle(title);
        return this;
    }
    
    /**
     * Define the modality for the stage under construction.
     * @param modality for the stage
     * @param parentWindow modality is defined relatively to this stage
     * @return this builder instance (Fluent API)
     */
    public StageBuilder withModality(Modality modality, Window parentWindow){
        stage.initModality(modality);
        stage.initOwner(parentWindow);
        return this;
    }
}
