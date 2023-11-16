package src;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.parallel.Resources;


import javafx.event.EventHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx;



public class TSPSolverController implements Initializable {

    
    private TSPSolver TSPSolver;

    @FXML private Button btnRun;
    @FXML private ChoiceBox choiceBoxCrossover;
    @FXML private TextField txtFieldTourSize;
    @FXML private TextField txtFieldPopSize;
    @FXML private TextField txtFieldMuationRate;
    @FXML private TextField txtFieldCrossoverRate;
    @FXML private TextField txtFieldTournamentSize;
    @FXML private TextArea txtAreaOutput;
    // @FXML private CheckComboBox chkComboBoxCities;


    private ObservableList<String> crossoverList = FXCollections.observableArrayList("Add", "Sub");

    public TSPSolverController() {
        TSPSolver = new TSPSolver();
        // update(TSPSolver);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        choiceBoxCrossover.setItems(crossoverList);
        // choiceBoxCrossover.setValue("Add");
        // throw new UnsupportedOperationException("Unimplemented method 'initialize'");
    }

    public void run() {
        TSPSolver.run(txtAreaOutput);
        btnRun.setText("Ran");
        txtAreaOutput.setText("Running new test....");
    }
    

    
}
