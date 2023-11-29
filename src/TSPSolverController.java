package src;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import javafx.scene.web.WebView;

import org.controlsfx.control.CheckComboBox;

public class TSPSolverController implements Initializable {

    
    private TSPSolver TSPSolver;

    // These are the FXML contatiners and objects, they have the @FXML tag, which is used to know that it needs 
    //      to correspond to the contatiner that exists in the FXML
    @FXML private VBox mainLayout;
    @FXML private ChoiceBox<String> choiceBoxCrossover;
    @FXML private ChoiceBox<String> choiceBoxSelection;
    @FXML private TextField txtFieldNumGenerations;
    @FXML private TextField txtFieldTourSize;
    @FXML private TextField txtFieldPopSize;
    @FXML private TextField txtFieldMutationRate;
    @FXML private TextField txtFieldCrossoverRate;
    @FXML private TextField txtFieldTournamentSize;
    @FXML private TextField txtFieldNewCity;
    @FXML private Button btnRun;
    @FXML private Button btnAdd;
    @FXML private LineChart<String, Integer> lineChartFitness;
    @FXML private GridPane gridPaneOptions;
    @FXML private WebView webViewConfig;
    @FXML private WebView webViewOutput;

    // Check combo box for cities
    private CheckComboBox<String> chkComboBoxCities;

    // Observable lists to tie to the choice box and check combo box, then we will add a listner to them to update the TSP model
    private ObservableList<String> crossoverList = FXCollections.observableArrayList("One-Point Crossover", "Two-Point Crossover", "CX2 Crossover", "Greedy Crossover");
    private ObservableList<String> selectionList = FXCollections.observableArrayList("Tournament Selection", "Proportional Selection");
    private ObservableList<String> citiesList = FXCollections.observableArrayList();

    //
    private ListChangeListener<String> chkComboListener;

    public TSPSolverController() {
        // Initial Constants, can pull this up if we want. Will initial txt fields with these as well. NOT part of this class. Should be passed 
        //      into the TSP constructor to be honest
        int numGenerations = 10000;
        int populationSize = 10;
        double mutationRate = 0.05;
        double crossoverRate = 0.80;
        int tournamentSize = 2;
        String crossoverFcn = crossoverList.get(0); // First one default
        String selectionFcn = selectionList.get(0); // first one default
        TSPSolver = new TSPSolver(numGenerations, populationSize, mutationRate, crossoverRate, tournamentSize, crossoverFcn, selectionFcn);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Pass in the FXML txt area boxes so that the TSP can output to them. A MVC approach
        //      in likely more proper, but, that would increase the run time. May tweak for final implementation 
        //      when we have some graph outputs, maybe. 
        TSPSolver.setOutput(webViewConfig, webViewOutput, lineChartFitness);

        // Initialize the choice box for the crossover function as well as supporting stuff.
        initializeChoiceBoxCrossover();

        // Initialize the choice box fo the selection fcn as well as supporting stuff
        initializeChoiceBoxSelection();

        // Initialize the check combo box for the cities as well as supporting stuff. 
        initializeChkComboBoxCities();

        // Initilize GUI with TSP settings
        // Initilize Text fields and update TSP with pertinent information from startup
        txtFieldNumGenerations.setText(Integer.toString(TSPSolver.getNumGenerations()));
        txtFieldPopSize.setText(Integer.toString(TSPSolver.getPopulationSize()));
        txtFieldMutationRate.setText(Double.toString(TSPSolver.getMutationRate()));
        txtFieldCrossoverRate.setText(Double.toString(TSPSolver.getCrossoverRate()));
        txtFieldTournamentSize.setText(Integer.toString(TSPSolver.getTournamentSize()));

        // Update user route with the defaults, also updated tour szie. 
        setUserRoute();
    }

    private void initializeChoiceBoxSelection() {
        choiceBoxSelection.setItems(selectionList);
        choiceBoxSelection.setValue(selectionList.get(0));
        // Will update the selection function in the TSP automatically on change. Will also update the config show. 
        choiceBoxSelection.getSelectionModel().selectedIndexProperty().addListener(new
        ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, 
            Number value, Number new_value) {
                // System.out.println("Selection changed from " + value.intValue() + " to " + new_value.intValue());
                setSelectionFcn(new_value.intValue());
            } 
        });
    }


    private void initializeChoiceBoxCrossover() {
        // Add the observable list to the choice box for the crossover fcn, and update TSP model with it. Not as clean an implementation,
        //      should be able to call a method or soemthing.
        choiceBoxCrossover.setItems(crossoverList);
        choiceBoxCrossover.setValue(crossoverList.get(0));
        
        // Will update the crossover function in the TSP. The choice box
        //      only seemed to work with using the index of the selected value, so rather than getting the string, it is
        //      a Number type (Super class of all number types) and then just cast it to the int value to index stuff.
        //      As opposed to before, we can just add it here, as we do not need to toggle it on and off in order to update the list. 
        //      This list is static unlike the cities list. 
        choiceBoxCrossover.getSelectionModel().selectedIndexProperty().addListener(new 
        ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, 
            Number value, Number new_value) {
                setCrossoverFcn(new_value.intValue());
            } 
        });
    }


    private void initializeChkComboBoxCities() {
        // Create checkComboBox for cities selection
        chkComboBoxCities = new CheckComboBox<String>(citiesList);
        chkComboBoxCities.setTitle("Cities");
        // Add cities to list
        citiesList.addAll(TSPSolver.getCityNames());
        // Toggles all the cities to true since this is in the beginning, it jsut toggles them. Used to fix the graphical bug. 
        toggleAllCities();

        // create check combo box listener, will fire when cities are added/removed. This will subsequentily update
        //      the user route, and the tour size. getTourSize in this context, gets the tourSize from the TSP
        //      and updated the uneditable text field in the gui. This is broken out to a listner than can be added and removed, 
        //      because of the toggling method, we dont want to flood the printout (in the console) with the same stuff over and over, so its
        //      the listener is removed, then added back after the toggle.
        chkComboListener = new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {
                setUserRoute();
                getTourSize();
            }
        };
        // Add the listener to the check combo box from above. 
        addChkComboListener();

        // Lastly, add the check combo box to the pertinent container in the gui
        gridPaneOptions.add(chkComboBoxCities, 1, 8);
    }

    // Toggles the elements in the city list
    private void toggleAllCities() {
        // Toggles all the cities to start as picked
        for (int i = 0; i < citiesList.size(); i++) {
            chkComboBoxCities.getCheckModel().toggleCheckState(i);
        }

    }

    private void addChkComboListener() {
        chkComboBoxCities.getCheckModel().getCheckedItems().addListener(chkComboListener);
    }

    private void removeChkComboListener() {
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
    // Sets number of generations in TSP from gui
    public void setNumGenerations() {
        TSPSolver.setNumGenerations(Integer.parseInt(txtFieldNumGenerations.getText()));
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

    // Sets selection function in TSP from gui
    public void setSelectionFcn(Number idx) {
        TSPSolver.setSelectionFcn(selectionList.get(idx.intValue()));
    }
    
    // Sets user route in TSP from gui
    public void setUserRoute() {
        List<String> cities = chkComboBoxCities.getCheckModel().getCheckedItems();
        String[] selectedCities = cities.toArray(new String[0]);
        int[] userRoute = TSPSolver.getRouteIndices(selectedCities);
        TSPSolver.setUserRoute(userRoute);

        // Update tour size, since the user route has changed
        getTourSize();
    }

    private void disableInterface() {
        btnAdd.setDisable(true);
        btnRun.setDisable(true);
        choiceBoxCrossover.setDisable(true);
        txtFieldNumGenerations.setDisable(true);
        txtFieldPopSize.setDisable(true);
        txtFieldMutationRate.setDisable(true);
        txtFieldCrossoverRate.setDisable(true);
        txtFieldTournamentSize.setDisable(true);
        chkComboBoxCities.setDisable(true);
    }

    private void enableInterface() {
        btnAdd.setDisable(false);
        btnRun.setDisable(false);
        choiceBoxCrossover.setDisable(false);
        txtFieldNumGenerations.setDisable(false);
        txtFieldPopSize.setDisable(false);
        txtFieldMutationRate.setDisable(false);
        txtFieldCrossoverRate.setDisable(false);
        txtFieldTournamentSize.setDisable(false);
        chkComboBoxCities.setDisable(false);
    }

    // Adds new city to database
    public void addCity() {
        // Disable buttons
        disableInterface();
    
        // Break out the non javaFX stuff into a seperate thread, this allows us to disable the buttons, then schedule them to be re-enabled AFTER 
        //      the non-javaFX stuff is done. Otherwise, the UI stuff happens at first since its so short, then the non-javaFX stuff happens, due to 
        //      not explicitly "Scheduling" it to happen after the UI stuff.
        new Thread(() -> {
            // Get the city to be added
            String city = txtFieldNewCity.getText();
    
            // Call addCity method, will append to cities list if its good. 
            if (TSPSolver.addCity(city)) {
                citiesList.add(city);
            }
            // Enable buttons   
            Platform.runLater(() -> {
                removeChkComboListener();
                toggleAllCities();
                toggleAllCities();
                enableInterface();
                addChkComboListener();
            });
        }).start();

    }
    
}
