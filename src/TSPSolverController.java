package src;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.controlsfx.control.CheckComboBox;

public class TSPSolverController implements Initializable {

    
    private TSPSolver TSPSolver;

    // These are the FXML contatiners and objects, they have the @FXML tag, which is used to know that it needs 
    //      to correspond to the contatiner that exists in the FXML
    @FXML private VBox mainLayout;
    @FXML private ChoiceBox choiceBoxCrossover;
    @FXML private TextField txtFieldTourSize;
    @FXML private TextField txtFieldPopSize;
    @FXML private TextField txtFieldMutationRate;
    @FXML private TextField txtFieldCrossoverRate;
    @FXML private TextField txtFieldTournamentSize;
    @FXML private TextField txtFieldNewCity;
    @FXML private TextArea txtAreaOutput;
    @FXML private TextArea txtAreaConfig;
    @FXML private Button btnRun;
    @FXML private Button btnAdd;
    @FXML private LineChart<String, Integer> lineChartFitness;

    // @FXML private CheckComboBox chkComboBoxCities;
    @FXML private GridPane gridPaneOptions;

    // Check combo box for cities
    private CheckComboBox chkComboBoxCities;

    // Observable lists to tie to the choice box and check combo box, then we will add a listner to them to update the TSP model
    private ObservableList<String> crossoverList = FXCollections.observableArrayList("Two Point Crossover", "Future Crossover 1", "Future Crossover 2");
    private ObservableList<String> citiesList = FXCollections.observableArrayList();

    //
    private ListChangeListener chkComboListener;

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
        TSPSolver.setOutput(txtAreaOutput, txtAreaConfig, lineChartFitness);

        // Add the observable list to the choice box for the crossover fcn, and update TSP model with it. Not as clean an implementation,
        //      should be able to call a method or soemthing.
        choiceBoxCrossover.setItems(crossoverList);
        choiceBoxCrossover.setValue(crossoverList.get(crossoverFcn.intValue()));
        setCrossoverFcn(crossoverFcn);

        // Create checkComboBox for cities selection
        chkComboBoxCities = new CheckComboBox<String>(citiesList);
        chkComboBoxCities.setTitle("Cities");
        // Add cities to list
        citiesList.addAll(TSPSolver.getCityNames());

        // create check combo box listener, will fire when cities are added/removed. This will subsequentily update
        //      the user route, and the tour size. getTourSize in this context, gets the tourSize from the TSP
        //      and updated the uneditable text field in the gui
        chkComboListener = new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {
                setUserRoute();
                getTourSize();
            }
        };
        // Similar to above, but for the choice box. Will update the crossover function in the TSP. The chioce box
        //      only seemed to work with using the index of the selected value, so rather than getting the string, it is
        //      a Number type (Super class of all number types) and then just cast it to the int value to index stuff.
        //      As opposed to before, we can just add it here, as we do not need to toggle it on and off in order to update the list. 
        //      THis list is static unlike the cities list. 
        choiceBoxCrossover.getSelectionModel().selectedIndexProperty().addListener(new 
        ChangeListener<Number>() {
            public void changed(ObservableValue ov, 
            Number value, Number new_value) {
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

        // Toggles all the cities to start as picked
        for (int i = 0; i < citiesList.size(); i++) {
            chkComboBoxCities.getCheckModel().toggleCheckState(i);
        }
       // Add it. Broke it out so we can remove it too. 
        addComboListener();
        setUserRoute();
        getTourSize();
    }

    private void addComboListener() {
        chkComboBoxCities.getCheckModel().getCheckedItems().addListener(chkComboListener);
    }

    private void removeComboListener() {
        chkComboBoxCities.getCheckModel().getCheckedItems().removeListener(chkComboListener);
    }

    // Run the TSP solver
    public void run() {
        TSPSolver.run();
    }

    // Get tour size to update GUI field
    public void getTourSize() {
        txtFieldTourSize.setText(Integer.toString(TSPSolver.getTourSize()));
    }

    // Sets population size in TSP from gui
    public void setPopulationSize() {
        TSPSolver.setPopulationSize(Integer.parseInt(txtFieldPopSize.getText()));
    }    

    // Sets mutation rate in TSP from gui
    public void setMutationRate() {
        TSPSolver.setMutationRate(Double.parseDouble(txtFieldMutationRate.getText()));
    }
    
    // Sets crossover rate in TSP from gui
    public void setCrossoverRate() {
        TSPSolver.setCrossoverRate(Double.parseDouble(txtFieldCrossoverRate.getText()));
    }

    // Sets tournament size in TSP from gui
    public void setTournamentSize() {
        TSPSolver.setTournamentSize(Integer.parseInt(txtFieldTournamentSize.getText()));
    }

    // Sets crossover function in TSP from gui
    public void setCrossoverFcn(Number idx) {
        TSPSolver.setCrossoverFcn(crossoverList.get(idx.intValue()));
    }
    
    // Sets user route in TSP from gui
    public void setUserRoute() {
        List<String> cities = chkComboBoxCities.getCheckModel().getCheckedItems();
        String[] selectedCities = cities.toArray(new String[0]);
        int[] userRoute = TSPSolver.getRouteIndices(selectedCities);
        TSPSolver.setUserRoute(userRoute);


        // System.out.println("Update user route method: " + selectedCities);
        // TSPSolver.setUserRoute(TSPSolver.getRouteIndices(selectedCitiesList));
    }

    // Adds new city to database
    public void addCity() {
        removeComboListener();
        String city = txtFieldNewCity.getText();
        TSPSolver.addCity(city);
        citiesList.add(city);
        addComboListener();
    }


    
}
