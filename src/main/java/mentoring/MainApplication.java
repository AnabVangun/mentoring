package mentoring;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mentoring.concurrency.ConcurrencyHandler;

public class MainApplication extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("JavaFX and Gradle");
        stage.setScene(scene);
        stage.show();
    }
        
    @Override
    public void stop(){
        //TODO put magic number in parameters
        ConcurrencyHandler.globalHandler.awaitTermination(5000);
        if (! ConcurrencyHandler.globalHandler.isShutDown()){
            throw new RuntimeException("Can't shutdown concurrency handler");
        }
    }
}
