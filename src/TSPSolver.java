package src;

import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.input.CharInput;

public class TSPSolver {
    // GA object
    private GeneticAlgorithm GA;
    // Map object (WHole database, all cities)
    private Map map;
    // User defined route, used to create the user defined map
    private int[] userRoute = {};
    // General GA parameters, most of which user has control over (Tour size is set but number of cities in data)
    private int tourSize;
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int tournamentSize;
    private String crossoverFcn;
    private int numGenerations;
    // Objects for the output
    private TextArea txtAreaOutput;
    private WebView webViewConfig;
    private LineChart<String, Integer> lineChartFitness;
    private XYChart.Series<String, Integer> fitnessData;
    // Control var for the line chart scaling, set it artficially big so it will scale down
    private Integer chartLowBound = 100000;


    public TSPSolver(int numGenerations, int populationSize, double mutationRate, double crossoverRate, int tournamentSize, String crossoverFcn) {
        this.numGenerations = numGenerations;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.tournamentSize = tournamentSize;
        this.crossoverFcn = crossoverFcn;
        map = new Map();
    }

    private void updateTSP() {
        GA = null;
        tourSize = map.getUserNumberOfCities();
        System.out.println("New Tour Size is " + this.tourSize);
        // Initialize GA
        GA =  new GeneticAlgorithm(populationSize, mutationRate, crossoverFcn,
                crossoverRate, tournamentSize,tourSize,map.getUserCityMatrix());
    }

    public void showConfig() {
        // txtAreaConfig.setText("Crossover Function set to: " + this.crossoverFcn);
        // txtAreaConfig.appendText("\nNumber of Generations set to: " + this.numGenerations);
        // txtAreaConfig.appendText("\nTour Size is: " + this.tourSize);
        // txtAreaConfig.appendText("\nPopulation Size set to: " + this.populationSize);
        // txtAreaConfig.appendText("\nMutation Rate set to: " + this.mutationRate);
        // txtAreaConfig.appendText("\nCrossover Rate set to: " + this.crossoverRate);
        // txtAreaConfig.appendText("\nTournament Size set to: " + this.tournamentSize);
        // txtAreaConfig.appendText("\nUser Route includes: " +  map.toStringUser());
        
        // String cssFilePath = System.getProperty("user.dir") + "/GA-for-the-TSP/css/GAConfigStyle.css";
        // File cssFile = new File(cssFilePath);
        // String cssUrl = "";
        // try {
        //     cssUrl = cssFile.toURI().toURL().toExternalForm();
        // } catch (MalformedURLException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        String content = "<table>"
        + "<tr><th>Crossover Function</th><td>" + this.crossoverFcn + "</td></tr>"
        + "<tr><th>Number of Generations</th><td>" + this.numGenerations + "</td></tr>"
        + "<tr><th>Tour Size</th><td>" + this.tourSize + "</td></tr>"
        + "<tr><th>Population Size</th><td>" + this.populationSize + "</td></tr>"
        + "<tr><th>Mutation Rate</th><td>" + this.mutationRate + "</td></tr>"
        + "<tr><th>Crossover Rate</th><td>" + this.crossoverRate + "</td></tr>"
        + "<tr><th>Tournament Size</th><td>" + this.tournamentSize + "</td></tr>"
        + "<tr><th>User Route</th><td>" + map.toStringUser() + "</td></tr>"
        + "</table>";
    webViewConfig.applyCss();
    webViewConfig.getEngine().loadContent(content);
    }

    // Add a city to the data base
    public Boolean addCity(String city) {
        // Check to make sure city is not already in the db
        if (map.addCity(city)) {
            map.reload();
            txtAreaOutput.setText(city + " added to database");
            return true;
            
        } else {
            txtAreaOutput.setText(city + " already in database");
            return false;
        }

    }

    private void updateChart(int i, double fitness) {
        fitnessData.getData().add(new XYChart.Data(Integer.toString(i), fitness));
        lineChartFitness.getData().remove(fitnessData);
        lineChartFitness.getData().add(fitnessData);
    }

    private void clearChart() {
        fitnessData.getData().clear();
        lineChartFitness.getData().removeAll(fitnessData);
    }


    public void run() {
        // Must get fitness before GA operation loop
        updateTSP();
        txtAreaOutput.setText("Running new solver:");
        GA.fitness();

        // Print best initial solution
        Tour bestTour = GA.getFittest();
        System.out.println("Initial route : " + Arrays.toString(bestTour.getRoute()));
        txtAreaOutput.appendText("\nInitial route : " + Arrays.toString(bestTour.getRoute()));
        System.out.println("Initial distance : " + bestTour.getFitness());
        txtAreaOutput.appendText("\nInitial distance : " + bestTour.getFitness());
        System.out.println("");

        // Clear chart for new runs
        clearChart();
        // Technically faster to do the crossover check first, then call the loop
            for (int i = 0; i < numGenerations; i++) // This dictates stopping criteria
            {
                GA.selection();
                GA.crossover();
                GA.mutation();
                GA.fitness();
                // Intermediate output for sanity
                if (i%(numGenerations/25) == 0)
                {        
                    txtAreaOutput.appendText("\n---------------------");
                    bestTour = GA.getFittest();
                    // System.out.println("Iteration "+i);
                    txtAreaOutput.appendText("\nIteration "+i);
                    // System.out.println("Route : " + Arrays.toString(bestTour.getRoute()));
                    txtAreaOutput.appendText("\nRoute : " + Arrays.toString(bestTour.getRoute()));
                    // System.out.println("Distance : " + bestTour.getFitness());
                    txtAreaOutput.appendText("\nDistance : " + bestTour.getFitness());
                    updateChart(i, bestTour.getFitness());
                    // System.out.println("");
                }
            }
        txtAreaOutput.appendText("\n---------------------");
        // Print results
        bestTour = GA.getFittest();
        System.out.println("Finished");
        txtAreaOutput.appendText("\nFinished");
        System.out.println("Final distance: " + bestTour.getFitness());
        txtAreaOutput.appendText("\nFinal distance: " + bestTour.getFitness());
        System.out.println("Final Solution:");
        txtAreaOutput.appendText("\nFinal Solution: " + (Arrays.toString(bestTour.getRoute())));
        System.out.println(Arrays.toString(bestTour.getRoute()));
    }


    public void setUserRoute(int[] userRoute) {
        map.setUserRoute(userRoute);
        // System.out.println("New User Route is " + this.userRoute[0]);
        // User route and actual city data are not part of the TSP, they are part of the map object. 
        //      so those must be updated first via the update TSP then we can print the setup (which prints) 
        //      the routes
        updateTSP();
        showConfig();
    } 

    public void setCrossoverFcn(String crossoverFcn) {
        this.crossoverFcn = crossoverFcn;
        showConfig();
        System.out.println("New Crossover Fcn is " + this.crossoverFcn);
        // updateTSP();
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        showConfig();
        System.out.println("New Population Size is " + this.populationSize);
        updateTSP();
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
        showConfig();
        System.out.println("New Mutation Rate is " + this.mutationRate);
        updateTSP();
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
        showConfig();
        System.out.println("New Crossover Rate is " + this.crossoverRate);
        updateTSP();
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
        showConfig();
        System.out.println("New Tournament Size is " + this.tournamentSize);
        updateTSP();
    }

    public void setNumGenerations(int numGenerations) {
        this.numGenerations = numGenerations;
        showConfig();
        System.out.println("New Number of Generations is " + this.numGenerations);
        updateTSP();
    }


    public void setOutput(TextArea txtAreaOutput, LineChart<String, Integer> lineChartFitness, WebView webViewConfig) {
        // txtAreaOutput.setText("New TSP Solver Initialized");
        this.txtAreaOutput = txtAreaOutput;
        this.lineChartFitness = lineChartFitness;
        this.webViewConfig = webViewConfig;
        webViewConfig.getEngine().setUserStyleSheetLocation(getClass().getResource("../css/GAConfigStyle.css").toString());

        fitnessData = new XYChart.Series<>();
        lineChartFitness.getData().add(fitnessData);
        updateChart(5, 5);
        clearChart();
    }

    public int getNumGenerations() {
        return this.numGenerations;
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public double getMutationRate() {
        return this.mutationRate;
    }

    public double getCrossoverRate() {
        return this.crossoverRate;
    }

    public int getTournamentSize() {
        return this.tournamentSize;
    }

    public int getTourSize() {
        return tourSize;
    }

    // return ALL city names in dataset
    public String[] getCityNames() {
        return map.getCityNames();
    }

    // return the indicies from the given string of route names
    public int[] getRouteIndices(String[] routeNames) {
        return map.getRouteIndices(routeNames);
    }

    // Return the current crossover function
    public String getCrossoverFcn() {
        return this.crossoverFcn;
    }

}
