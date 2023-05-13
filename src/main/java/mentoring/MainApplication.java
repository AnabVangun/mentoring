package mentoring;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.concurrency.ConcurrencyModule;
import mentoring.viewmodel.datastructure.PersonViewModelModule;

public class MainApplication extends Application {
    
    private final Injector injector = Guice.createInjector(new ConcurrencyModule(), 
            new PersonViewModelModule());
    private final ConcurrencyHandler handler = injector.getInstance(ConcurrencyHandler.class);
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("JavaFX and Gradle");
        stage.setScene(scene);
        stage.show();
    }
        
    @Override
    public void stop(){
        //TODO put magic number in parameters
        handler.shutdown(5000);
        if (! handler.isShutDown()){
            throw new RuntimeException("Can't shutdown concurrency handler");
        }
    }
}
