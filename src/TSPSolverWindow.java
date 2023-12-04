package src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TSPSolverWindow extends Application {
    // Main layout for the window which is a Vbox, matches what it is in SceneBuilder. Likely
    //      a better way to do this, via injection from the FXML itself; Alas....
    private VBox mainLayout;


    // Start method, the launch method will call this. General JAVAfx stuff here 
    @Override
    public void start(Stage stage) throws Exception {

        // FXMLLoader object, will load the FXML file and then we can get the main layout from it
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TSPSolverWindow.class.getResource("gui.fxml"));

        // Grab the VBox from the FXML file
        mainLayout = loader.load();

        // Basic JAVAfx stuff here on out
        Scene scene = new Scene(mainLayout);
        System.out.println("current directory: " + System.getProperty("user.dir"));
        stage.setScene(scene);
        stage.setTitle("GA-for-TSP");
        stage.show();
    }

    // Entry point from the driver Main class
    public void run() {
        launch();
    }

}
