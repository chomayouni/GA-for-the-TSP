package src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TSPSolverWindow extends Application {
    private Stage primStage;
    private VBox mainLayout;


    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TSPSolverWindow.class.getResource("gui.fxml"));
        mainLayout = loader.load();
        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
        stage.setTitle("GA-for-TSP");
        stage.show();
    }

    public void run() {
        launch();
    }

}
