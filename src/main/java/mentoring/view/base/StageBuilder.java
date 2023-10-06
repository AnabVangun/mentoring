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
    final String MAIN_CSS = StageBuilder.class.getResource("/mentoring/styles.css").toExternalForm();
    /**
     * Finalise the creation of a new {@link Stage} instance. After this, the builder instance 
     * is reinitialised.
     * @param root the root of the new instance
     * @return the newly created instance
     */
    public Stage build(Parent root){
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MAIN_CSS);
        stage.setScene(scene);
        Stage result = stage;
        stage = new Stage();
        return result;
    }
    
    /**
     * Initialise this StageBuilder with a given Stage. Optional, this method MUST be called before
     * any other in the process of building a stage.
     * @param stage to build upon
     * @return this builder instance (Fluent API)
     */
    public StageBuilder initialise(Stage stage){
        this.stage = stage;
        return this;
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
