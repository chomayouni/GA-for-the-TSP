package src;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.parallel.Resources;


import javafx.event.EventHandler;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

public class TSPSolverController implements Initializable {

    
    private TSPSolver TSPSolver;

    @FXML private Button btnRun;
    @FXML private ChoiceBox choiceBoxCrossover;
    @FXML private TextField txtFieldTourSize;
    @FXML private TextField txtFieldPopSize;
    @FXML private TextField txtFieldMutationRate;
    @FXML private TextField txtFieldCrossoverRate;
    @FXML private TextField txtFieldTournamentSize;
    @FXML private TextArea txtAreaOutput;
    // @FXML private CheckComboBox chkComboBoxCities;
    private CheckComboBox chkComboBoxCities;
    @FXML private GridPane gridPaneOptions;


    private ObservableList<String> crossoverList = FXCollections.observableArrayList("Add", "Sub");
    private ObservableList<String> citiesList = FXCollections.observableArrayList();

    public TSPSolverController() {
        TSPSolver = new TSPSolver();
        // update(TSPSolver);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initial Constants, can pull this up if we want. Will initial txt fields with these as well. NOT part of this class. Should be passed 
        //      into the TSP constructor to be honest
        int populationSize = 5;
        double mutationRate = 0.80;
        double crossoverRate = 0.80;
        int tournamentSize = 2;
        // TODO Auto-generated method stub
        choiceBoxCrossover.setItems(crossoverList);
        choiceBoxCrossover.setValue("Add");
        // chkComboBoxCities = new CheckComboBox<String>(citiesList);
        // chkComboBoxCities = null;
        chkComboBoxCities = new CheckComboBox<String>(citiesList);
        chkComboBoxCities.setTitle("Cities");
        // throw new UnsupportedOperationException("Unimplemented method 'initialize'");
        // Get the cities from the data set; 

        citiesList.addAll(TSPSolver.getCityNames());

        chkComboBoxCities.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {
                updateUserRoute();
                updateTourSize();
            }
        });

        gridPaneOptions.add(chkComboBoxCities, 1, 6);
        TSPSolver.setOutput(txtAreaOutput);

        // Initilize Text fields and update TSP 
        txtFieldPopSize.setText(Integer.toString(populationSize));
        updatePopulationSize();
        txtFieldMutationRate.setText(Double.toString(mutationRate));
        updateMutationRate();
        txtFieldCrossoverRate.setText(Double.toString(crossoverRate));
        updateCrossoverRate();
        txtFieldTournamentSize.setText(Integer.toString(tournamentSize));
        updateTournamentSize();
    }



    public void run() {
        updateUserRoute();
        TSPSolver.run();
    }

    public void updateTourSize() {
        txtFieldTourSize.setText(Integer.toString(TSPSolver.getTourSize()));
    }


    public void updatePopulationSize() {
        TSPSolver.setPopulationSize(Integer.parseInt(txtFieldPopSize.getText()));
    }    

    public void updateMutationRate() {
        TSPSolver.setMutationRate(Double.parseDouble(txtFieldMutationRate.getText()));
    }
    
    public void updateCrossoverRate() {
        TSPSolver.setCrossoverRate(Double.parseDouble(txtFieldCrossoverRate.getText()));
    }

    public void updateTournamentSize() {
        TSPSolver.setTournamentSize(Integer.parseInt(txtFieldTournamentSize.getText()));
    }

    public void updateUserRoute() {
        List<String> cities = chkComboBoxCities.getCheckModel().getCheckedItems();
        String[] selectedCities = cities.toArray(new String[0]);
        int[] userRoute = TSPSolver.getRouteIndices(selectedCities);
        TSPSolver.setUserRoute(userRoute);


        // System.out.println("Update user route method: " + selectedCities);
        // TSPSolver.setUserRoute(TSPSolver.getRouteIndices(selectedCitiesList));
    }

    
}
