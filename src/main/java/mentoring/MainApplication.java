package mentoring;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.concurrency.ConcurrencyModule;
import mentoring.view.base.StageBuilder;
import mentoring.viewmodel.datastructure.PersonViewModelModule;

public class MainApplication extends Application {
    
    private final Injector injector = Guice.createInjector(new ConcurrencyModule(), 
            new PersonViewModelModule());
    private final ConcurrencyHandler handler = injector.getInstance(ConcurrencyHandler.class);
    private final static int SHUTDOWN_TIMEOUT = 5000;
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"));
        loader.setControllerFactory(injector::getInstance);

        new StageBuilder().initialise(stage)
                .withTitle("Mentoring match-maker")
                .build(loader.load());
        stage.setMaximized(true);
        stage.show();
    }
        
    @Override
    public void stop(){
        handler.shutdown(SHUTDOWN_TIMEOUT);
        if (! handler.isShutDown()){
            throw new RuntimeException("Can't shutdown concurrency handler");
        }
    }
}
