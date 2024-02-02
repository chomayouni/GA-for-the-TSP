package src;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Pair;

import org.controlsfx.control.CheckComboBox;

import javafx.scene.chart.CategoryAxis;
import java.nio.file.*;


public class TSPSolverController implements Initializable {

    
    private TSPSolver TSPSolver;

    // These are the FXML contatiners and objects, they have the @FXML tag, which is used to know that it needs 
    //      to correspond to the contatiner that exists in the FXML
    @FXML private VBox mainLayout;
    @FXML private ChoiceBox<String> choiceBoxCrossover;
    @FXML private ChoiceBox<String> choiceBoxSelection;
    @FXML private ComboBox<String> comboBoxDataset;
    @FXML private TextField txtFieldNumGenerations;
    @FXML private TextField txtFieldTourSize;
    @FXML private TextField txtFieldPopSize;
    @FXML private TextField txtFieldMutationRate;
    @FXML private TextField txtFieldCrossoverRate;
    @FXML private TextField txtFieldTournamentSize;
    @FXML private TextField txtFieldNewCity;
    @FXML private TextField txtFieldAvgRuns;
    @FXML private TextField txtFieldAPIKey;
    @FXML private Button btnRun;
    @FXML private Button btnAdd;
    @FXML private Button btnAvg;
    @FXML private LineChart<String, Integer> lineChartFitness;
    @FXML private GridPane gridPaneDataset;
    @FXML private WebView webViewConfig;
    @FXML private WebView webViewOutput;
    @FXML private CategoryAxis categoryAxisXFitness;
    @FXML private NumberAxis numberAxisYFitness;

    // Check combo box for cities
    private CheckComboBox<String> chkComboBoxCities;

    // Observable lists to tie to the choice box and check combo box, then we will add a listener to them to update the TSP model
    private ObservableList<String> crossoverList = FXCollections.observableArrayList("One-Point Crossover", "Two-Point Crossover", "CX Crossover", "CX2 Crossover", "Greedy Crossover", "PMX Crossover", "OX Crossover", "SCX Crossover");
    private ObservableList<String> selectionList = FXCollections.observableArrayList("Tournament Selection", "Proportional Selection");
    // This one is initilized in its own method, as its populated from the directory full of datasets. 
    private ObservableList<String> datasetList;
    private ObservableList<String> citiesList = FXCollections.observableArrayList();

    // List change listener, we 
    private ListChangeListener<String> chkComboListener;

    // These are for the fitness graph. Need to keep global for avg runs (dirty, but whatever)
    private Integer minFitness = Integer.MAX_VALUE;
    private Integer maxFitness = Integer.MIN_VALUE;
    private Integer avgRunCount;


    public TSPSolverController() {
        // Initial Constants, can pull this up if we want. Will initial txt fields with these as well. NOT part of this class. Should be passed 
        //      into the TSP constructor to be honest
        int numGenerations = 10000;
        int populationSize = 10;
        double mutationRate = 0.05;
        double crossoverRate = 0.80;
        int tournamentSize = 2;
        // Get we get the dataset names from the dataset folder. SHould really be in the initilize method for the datasetChoice box, but need it to grab a default here. 
        getDatasetNames();
        String initialCrossoverFcn = crossoverList.get(0); // First one default
        String initialSelectionFcn = selectionList.get(0); // first one default
        String initialDataset = datasetList.get(0); // first one default
        System.out.println("Initial Dataset: " + initialDataset);
        TSPSolver = new TSPSolver(numGenerations, populationSize, mutationRate, crossoverRate, tournamentSize, initialCrossoverFcn, initialSelectionFcn, initialDataset);
    }

    // This method is automatically called after the FXML is loaded, it is the "constructor" for the FXML. Need to place any FXMLlogic in here, as the normal, TSPSOlverController constructor does not
    //      have access to the FXML ob
    public void initialize(URL arg0, ResourceBundle arg1) {

        // Set the avg run count for controller. 
        avgRunCount = 10;
        // Initialize the choice box for the crossover function as well as supporting stuff.
        initializeChoiceBoxCrossover();

        // Initialize the choice box fo the selection fcn as well as supporting stuff
        initializeChoiceBoxSelection();

        // Initialize the choice box for the dataset as well as supporting stuff
        initializeChoiceBoxDataset();

        // Initialize the check combo box for the cities as well as supporting stuff. 
        initializeChkComboBoxCities();

        // Initilize the css for the output windows webviews
        webViewConfig.getEngine().setUserStyleSheetLocation(getClass().getResource("../css/GAConfigStyle.css").toString());
        webViewOutput.getEngine().setUserStyleSheetLocation(getClass().getResource("../css/GASolverStyle.css").toString());

        // Initilize GUI with TSP settings
        // Initilize Text fields and update TSP with pertinent information from startup
        txtFieldNumGenerations.setText(Integer.toString(TSPSolver.getNumGenerations()));
        txtFieldPopSize.setText(Integer.toString(TSPSolver.getPopulationSize()));
        txtFieldMutationRate.setText(Double.toString(TSPSolver.getMutationRate()));
        txtFieldCrossoverRate.setText(Double.toString(TSPSolver.getCrossoverRate()));
        txtFieldTournamentSize.setText(Integer.toString(TSPSolver.getTournamentSize()));
        txtFieldAvgRuns.setText(Integer.toString(avgRunCount));

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
                setSelectionFcn(selectionList.get(new_value.intValue()));
            } 
        });
    }


    private void initializeChoiceBoxCrossover() {
        // Add the observable list to the choice box for the crossover fcn
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
                setCrossoverFcn(crossoverList.get(new_value.intValue()));
            } 
        });
    }

    private void initializeChoiceBoxDataset() {
        // Add the observable list to the choice box for the dataset
        comboBoxDataset.setItems(datasetList);
        comboBoxDataset.setValue(datasetList.get(0));

        // add the listener for when the data set choice box is changed. 
        comboBoxDataset.getSelectionModel().selectedIndexProperty().addListener(new 
        ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, 
            Number value, Number new_value) {
                // System.out.println("Dataset changed from " + datasetList.get(value.intValue()) + " to " + datasetList.get(new_value.intValue()));
                setDataset(datasetList.get(new_value.intValue()));  
                getTourSize();
            } 
        });

    }

    private void initializeChkComboBoxCities() {
        // Create checkComboBox for cities 
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
        gridPaneDataset.add(chkComboBoxCities, 1, 1);
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

    private void disableInterface() {
        btnRun.setDisable(true);
        btnAvg.setDisable(true);
        btnAdd.setDisable(true);
        choiceBoxCrossover.setDisable(true);
        choiceBoxSelection.setDisable(true);
        chkComboBoxCities.setDisable(true);
        comboBoxDataset.setDisable(true);
        txtFieldNumGenerations.setDisable(true);
        txtFieldPopSize.setDisable(true);
        txtFieldMutationRate.setDisable(true);
        txtFieldCrossoverRate.setDisable(true);
        txtFieldTournamentSize.setDisable(true);
        txtFieldNewCity.setDisable(true);
        txtFieldAvgRuns.setDisable(true);
        txtFieldAPIKey.setDisable(true);

    }

    private void enableInterface() {
        btnRun.setDisable(false);
        btnAvg.setDisable(false);
        choiceBoxSelection.setDisable(false);
        choiceBoxCrossover.setDisable(false);
        comboBoxDataset.setDisable(false);
        txtFieldNumGenerations.setDisable(false);
        txtFieldPopSize.setDisable(false);
        txtFieldMutationRate.setDisable(false);
        txtFieldCrossoverRate.setDisable(false);
        txtFieldTournamentSize.setDisable(false);
        txtFieldAvgRuns.setDisable(false);


        // Conditionally turn on only if custom data set is being used. 
        if (TSPSolver.getDataset().equalsIgnoreCase("custom")) {
            btnAdd.setDisable(false);
            txtFieldAPIKey.setDisable(false);
            txtFieldNewCity.setDisable(false);
            chkComboBoxCities.setDisable(false);
            
        }
    }

    // Adds new city to database
    public void addCity() {
        // Disable buttons
        disableInterface();
        webViewOutput.getEngine().loadContent("<h1>Adding City to Dataset...</h1>");

        // Break out the non javaFX stuff into a seperate thread, this allows us to disable the buttons, then schedule them to be re-enabled AFTER 
        //      the non-javaFX stuff is done. Otherwise, the UI stuff happens at first since its so short, then the non-javaFX stuff happens, due to 
        //      not explicitly "Scheduling" it to happen after the UI stuff.
        new Thread(() -> {
            // Get the city to be added
            String city = txtFieldNewCity.getText();
            // Get the API key
            String apiKey = txtFieldAPIKey.getText();
    
            // Call addCity method, will append to cities list if its good. 
            if (TSPSolver.addCity(city, apiKey)) {
                citiesList.add(city);
            }
            // Enable buttons   
            Platform.runLater(() -> {
                removeChkComboListener();
                toggleAllCities();
                toggleAllCities();
                enableInterface();
                addChkComboListener();
                webViewOutput.getEngine().loadContent("<h1>" + city.toString() + " added to Cutsom Dataset</h1>");
            });
        }).start();

    }

    // Run the TSP solver
    public void run() {
        // Grab any changes the user didnt hit enter on
        setNumGenerations();
        setPopulationSize();
        setMutationRate();
        setCrossoverRate();
        setTournamentSize();
        // get the current config table for output
        getConfigTable();
        // disable interface
        disableInterface();
        webViewOutput.getEngine().loadContent("<h1>Running...</h1>");

        // Run the TSP solver in a seperate thread
        new Thread(() -> {
            // Run the TSP solver
            TSPSolver.clearAvgFitness();
            TSPSolver.run();
            // Enable the interface
            Platform.runLater(() -> {
                removeChkComboListener();
                getTSPTable();
                clearFitnessChart();
                getFitnessChart();
                System.out.println("Done running");
                enableInterface();
                addChkComboListener();
            });
        }).start();

    }

    public void avg() {
        // Grab any changes the user didnt hit enter on
        setNumGenerations();
        setPopulationSize();
        setMutationRate();
        setCrossoverRate();
        setTournamentSize();
        setAvgRuns();
        // get the current config table for output
        getConfigTable();
        // disable interface
        disableInterface();
        webViewOutput.getEngine().loadContent("<h1>Running...</h1>");
        // Clear graph
        clearFitnessChart();
        System.out.println("---------- Running avg ----------");
        // Run the TSP solver (10 times) in a separate thread
        new Thread(() -> {
            TSPSolver.clearAvgFitness();
            for (int i = 0; i <= avgRunCount; i++) {
                final int finalI = i;
                // Run the TSP solver
                if (i < avgRunCount) {
                    TSPSolver.run();
                }            
                // Update the interface
                Platform.runLater(() -> {
                    if (finalI < avgRunCount) {
                        removeChkComboListener();
                        getFitnessChart();
                        addChkComboListener();
                    }
                    else {
                        removeChkComboListener();
                        getAvgFitnessChart();
                        getAvgTSPTable();
                        enableInterface();
                        addChkComboListener();
                    }
                });
                // just wait for a bit each thread, want to update GUI
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getConfigTable() {
        webViewConfig.getEngine().loadContent(TSPSolver.getConfigTable());
    }

    public void getTSPTable() {
        webViewOutput.getEngine().loadContent(TSPSolver.getTSPTableData());
    }

    public void getAvgTSPTable() {
        System.out.println("Getting avg tsp table");
        webViewOutput.getEngine().loadContent(TSPSolver.getAvgTSPTableData());
    }

    public void getFitnessChart() {

        XYChart.Series<String, Integer> fitnessSeries = new XYChart.Series<String, Integer>();
        Pair<ArrayList<String>, ArrayList<Double>> fitnessData = TSPSolver.getFitnessData();

        for (int i = 0; i < fitnessData.getKey().size(); i++) {
            fitnessSeries.getData().add(new XYChart.Data<String, Integer>(fitnessData.getKey().get(i), fitnessData.getValue().get(i).intValue()));
            if (fitnessData.getValue().get(i).intValue() < minFitness) {
                minFitness = fitnessData.getValue().get(i).intValue();
            }
            if (fitnessData.getValue().get(i).intValue() > maxFitness) {
                maxFitness = fitnessData.getValue().get(i).intValue();
            }
        }

        

        lineChartFitness.getData().add(fitnessSeries);
        fitnessSeries.getNode().setStyle("-fx-stroke: red; -fx-stroke-width: 1px; -fx-background-color: red, white;");
        numberAxisYFitness.setAutoRanging(false);
        Long tickUnit = Math.round((0.1 * (maxFitness - minFitness)) / 50) * 50;
        numberAxisYFitness.setTickUnit(tickUnit.intValue());
        // System.out.println("Tick Unit: " + tickUnit);
        numberAxisYFitness.setMinorTickVisible(true);
        // System.out.println("Max: " + maxFitness + " Min: " + minFitness);
        numberAxisYFitness.setUpperBound((maxFitness + (int) (maxFitness * 0.1)) - ((maxFitness + (int) (maxFitness * 0.05)) % 100));
        numberAxisYFitness.setLowerBound((minFitness - (int) (minFitness * 0.1)) - ((minFitness - (int) (minFitness * 0.05)) % 100));
    }
    
    public void getAvgFitnessChart() {
        System.out.println("Getting avg fitness chart");
        XYChart.Series<String, Integer> AvgFitnessSeries = new XYChart.Series<String, Integer>();
        Pair<ArrayList<String>, ArrayList<Double>> avgFitnessData = TSPSolver.getAvgFitnessData(avgRunCount);
        for (int i = 0; i < avgFitnessData.getKey().size(); i++) {
            AvgFitnessSeries.getData().add(new XYChart.Data<String, Integer>(avgFitnessData.getKey().get(i), avgFitnessData.getValue().get(i).intValue()));
        }
        lineChartFitness.getData().add(AvgFitnessSeries);
        AvgFitnessSeries.getNode().setStyle("-fx-stroke: green; -fx-stroke-width: 1.5px; -fx-background-color: green, white;");        
    }

    public void clearFitnessChart() {
        lineChartFitness.getData().clear();
        minFitness = Integer.MAX_VALUE;
        maxFitness = Integer.MIN_VALUE;
    }

    // Get tour size to update GUI field
    public void getTourSize() {
        txtFieldTourSize.setText(Integer.toString(TSPSolver.getTourSize()));
        getConfigTable();
    }
    // Sets number of generations in TSP from gui
    public void setNumGenerations() {
        TSPSolver.setNumGenerations(Integer.parseInt(txtFieldNumGenerations.getText()));
        getConfigTable();
    }

    // Sets population size in TSP from gui
    public void setPopulationSize() {
        TSPSolver.setPopulationSize(Integer.parseInt(txtFieldPopSize.getText()));
        getConfigTable();
    }    

    // Sets mutation rate in TSP from gui
    public void setMutationRate() {
        TSPSolver.setMutationRate(Double.parseDouble(txtFieldMutationRate.getText()));
        getConfigTable();
    }
    
    // Sets crossover rate in TSP from gui
    public void setCrossoverRate() {
        TSPSolver.setCrossoverRate(Double.parseDouble(txtFieldCrossoverRate.getText()));
        getConfigTable();
    }

    // Sets tournament size in TSP from gui
    public void setTournamentSize() {
        TSPSolver.setTournamentSize(Integer.parseInt(txtFieldTournamentSize.getText()));
        getConfigTable();
    }

    // sets number of runs for avg runs
    public void setAvgRuns() {
        avgRunCount = (Integer.parseInt(txtFieldAvgRuns.getText()));
        System.out.println("Avg run count: " + avgRunCount);
    }

    // Sets crossover function in TSP from gui
    public void setCrossoverFcn(String arg) {
        TSPSolver.setCrossoverFcn(arg);
        getConfigTable();
    }

    // Sets selection function in TSP from gui
    public void setSelectionFcn(String arg) {
        TSPSolver.setSelectionFcn(arg);
        getConfigTable();
    }



    public void setDataset(String arg) {
        if (arg.equalsIgnoreCase("custom")) {
            btnAdd.setDisable(false);
            txtFieldNewCity.setDisable(false);
            txtFieldAPIKey.setDisable(false);
            chkComboBoxCities.setDisable(false);
        } 
        else {
            btnAdd.setDisable(true);
            txtFieldNewCity.setDisable(true);
            txtFieldAPIKey.setDisable(true);
            chkComboBoxCities.setDisable(true);
        }
        TSPSolver.setDataset(arg);
        getConfigTable();
    }
    
    // Sets user route in TSP from gui
    public void setUserRoute() {
        List<String> cities = chkComboBoxCities.getCheckModel().getCheckedItems();
        String[] selectedCities = cities.toArray(new String[0]);
        int[] userRoute = TSPSolver.getRouteIndices(selectedCities);
        TSPSolver.setUserRoute(userRoute);

        // Update tour size, since the user route has changed
        getTourSize();
        getConfigTable();
    }


    private void getDatasetNames() {
        List<String> datasetList = new ArrayList<>();
        String[] dirs = {"Symmetric", "Asymmetric"};

        for (String dirName : dirs) {
            try {
                Path dir = Paths.get("GA-for-the-TSP/data/TSP_DATASET/" + dirName);

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                    for (Path file : stream) {
                        String fileName = file.getFileName().toString();
                        if (fileName.endsWith(".txt")) {
                            fileName = fileName.substring(0, fileName.length() - 4);
                        }
                        if (fileName.equalsIgnoreCase("custom")) {
                            datasetList.add(0, fileName); // Add "CUSTOM" to the first index, otherwise it breaks everything and is annoying
                            System.out.println("Added Custom to dataset list");
                        } else {
                            datasetList.add(fileName);
                            // System.out.println("Added " + fileName + " to dataset list");
                        }
                    }
                } catch (IOException | DirectoryIteratorException x) {
                    System.err.println(x);
                }
            } catch (InvalidPathException x) {
                System.err.println("Invalid directory path: " + x);
            }
        }
        this.datasetList = FXCollections.observableArrayList(datasetList);
        System.out.println("Dataset list: " + datasetList);
        // System.out.println("First Dataset is " + datasetList.get(0));
    }
}
