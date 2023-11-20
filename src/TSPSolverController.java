package src;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.parallel.Resources;


import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
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
    @FXML private TextArea txtAreaConfig;
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
        Number crossoverFcn = 0; // First one default

        // Pass in the FXML txt area boxes so that the TSP can output to them. A MVC approach
        //      in likely more proper, but, that would increase the run time. May tweak for final implementation 
        //      when we have some graph outputs, maybe. 
        TSPSolver.setOutput(txtAreaOutput, txtAreaConfig);

        // Add the observable list to the choice box for the crossover fcn, and update TSP model with it. Not as clean an implementation,
        //      should be able to call a method or soemthing.
        choiceBoxCrossover.setItems(crossoverList);
        choiceBoxCrossover.setValue(crossoverList.get(crossoverFcn.intValue()));
        setCrossoverFcn(crossoverFcn);

        // Create checkComboBox for cities selection
        chkComboBoxCities = new CheckComboBox<String>(citiesList);
        chkComboBoxCities.setTitle("Cities");

        // Add the observable list to the checkComboBox for the cities
        citiesList.addAll(TSPSolver.getCityNames());

        // Add check combo box listener, will fire when cities are added/removed. This will subsequentily update
        //      the user route, and the tour size. getTourSize in this context, gets the tourSize from the TSP
        //      and updated the uneditable text field in the gui
        chkComboBoxCities.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {
                setUserRoute();
                getTourSize();
            }
        });

        choiceBoxCrossover.getSelectionModel().selectedIndexProperty().addListener(new 
        ChangeListener<Number>() {
            public void changed(ObservableValue ov, 
            Number valie, Number new_value) {
                setCrossoverFcn(new_value);
            }
            
        });

        // add the check combo box to the pertinent container in the gui
        gridPaneOptions.add(chkComboBoxCities, 1, 6);

        // Initilize Text fields and update TSP with pertinent information from startup
        txtFieldPopSize.setText(Integer.toString(populationSize));
        setPopulationSize();
        txtFieldMutationRate.setText(Double.toString(mutationRate));
        setMutationRate();
        txtFieldCrossoverRate.setText(Double.toString(crossoverRate));
        setCrossoverRate();
        txtFieldTournamentSize.setText(Integer.toString(tournamentSize));
        setTournamentSize();

    }



    public void run() {
        setUserRoute();
        TSPSolver.run();
    }

    public void getTourSize() {
        txtFieldTourSize.setText(Integer.toString(TSPSolver.getTourSize()));
    }

    public void setPopulationSize() {
        TSPSolver.setPopulationSize(Integer.parseInt(txtFieldPopSize.getText()));
    }    

    public void setMutationRate() {
        TSPSolver.setMutationRate(Double.parseDouble(txtFieldMutationRate.getText()));
    }
    
    public void setCrossoverRate() {
        TSPSolver.setCrossoverRate(Double.parseDouble(txtFieldCrossoverRate.getText()));
    }

    public void setTournamentSize() {
        TSPSolver.setTournamentSize(Integer.parseInt(txtFieldTournamentSize.getText()));
    }

    public void setCrossoverFcn(Number idx) {
        TSPSolver.setCrossoverFcn(crossoverList.get(idx.intValue()));
    }
    
    public void setUserRoute() {
        List<String> cities = chkComboBoxCities.getCheckModel().getCheckedItems();
        String[] selectedCities = cities.toArray(new String[0]);
        int[] userRoute = TSPSolver.getRouteIndices(selectedCities);
        TSPSolver.setUserRoute(userRoute);


        // System.out.println("Update user route method: " + selectedCities);
        // TSPSolver.setUserRoute(TSPSolver.getRouteIndices(selectedCitiesList));
    }

    
}
