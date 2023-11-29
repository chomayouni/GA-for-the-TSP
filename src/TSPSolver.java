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
    private String selectionFcn;
    private int numGenerations;
    // Objects for the output
    private WebView webViewConfig;
    private WebView webViewOutput;
    // Objects for the fitness graph
    private LineChart<String, Integer> lineChartFitness;
    private XYChart.Series<String, Integer> fitnessData;

    // Constructor for the solver. Will also create a map. 
    public TSPSolver(int numGenerations, int populationSize, double mutationRate, double crossoverRate, int tournamentSize, String crossoverFcn, String selectionFcn) {
        this.numGenerations = numGenerations;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.tournamentSize = tournamentSize;
        this.crossoverFcn = crossoverFcn;
        this.selectionFcn = selectionFcn;
        map = new Map();
    }

    private void updateTSP() {
        GA = null;
        tourSize = map.getUserNumberOfCities();
        System.out.println("New Tour Size is " + this.tourSize);
        // Initialize GA
        GA =  new GeneticAlgorithm(populationSize, mutationRate, crossoverFcn, selectionFcn,
                crossoverRate, tournamentSize,tourSize,map.getUserCityMatrix());
    }

    private void showConfig() {        
        String content = "<table>"
        + "<tr><th>Crossover Function</th><td>" + this.crossoverFcn + "</td></tr>"
        + "<tr><th>Selection Function</th><td>" + this.selectionFcn + "</td></tr>"
        + "<tr><th>Number of Generations</th><td>" + this.numGenerations + "</td></tr>"
        + "<tr><th>Tour Size</th><td>" + this.tourSize + "</td></tr>"
        + "<tr><th>Population Size</th><td>" + this.populationSize + "</td></tr>"
        + "<tr><th>Mutation Rate</th><td>" + this.mutationRate + "</td></tr>"
        + "<tr><th>Crossover Rate</th><td>" + this.crossoverRate + "</td></tr>"
        + "<tr><th>Tournament Size</th><td>" + this.tournamentSize + "</td></tr>"
        + "<tr><th>User Route</th><td>" + map.toStringUser() + "</td></tr>"
        + "</table>";
    // webViewConfig.applyCss();
    webViewConfig.getEngine().loadContent(content);
    }

    private void showOutput(String content) {
        // webViewOutput.applyCss();
        webViewOutput.getEngine().loadContent(content);
    }

    // Add a city to the data base
    public Boolean addCity(String city) {
        // Check to make sure city is not already in the db
        if (map.addCity(city)) {
            map.reload();
            // txtAreaOutput.setText(city + " added to database");
            return true;
            
        } else {
            // txtAreaOutput.setText(city + " already in database");
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
        GA.fitness();
    
        // Print best initial solution
        Tour bestTour = GA.getFittest();
        System.out.println("Initial route : " + Arrays.toString(bestTour.getRoute()));
        System.out.println("Initial distance : " + bestTour.getFitness());
        System.out.println("");
    
        // Clear chart for new runs, create new content for the output
        clearChart();
        StringBuilder content = new StringBuilder("<table><tr><th>Iteration</th><th>Route</th><th>Distance</th></tr>");
    
        // Add initial solution to the table
        content.append("<tr><td>Initial</td><td>")
               .append(Arrays.toString(bestTour.getRoute()))
               .append("</td><td>")
               .append(bestTour.getFitness())
               .append("</td></tr>");
    
        for (int i = 0; i < numGenerations; i++) {
            GA.selection();
            GA.crossover();
            GA.mutation();
            GA.fitness();
    
            if ((i%(numGenerations/25) == 0) && (i != 0)) {        
                bestTour = GA.getFittest();
    
                // Add current record to the table
                content.append("<tr><td>").append(i)
                       .append("</td><td>").append(Arrays.toString(bestTour.getRoute()))
                       .append("</td><td>").append(bestTour.getFitness())
                       .append("</td></tr>");
    
                updateChart(i, bestTour.getFitness());
            }
        }
    
        // Add final solution to the table
        bestTour = GA.getFittest();
        content.append("<tr><td>Final</td><td>")
               .append(Arrays.toString(bestTour.getRoute()))
               .append("</td><td>")
               .append(bestTour.getFitness())
               .append("</td></tr>");
        // Finish the table
        content.append("</table>");
        // Show the table. 
        showOutput(content.toString());
    
        // Print results to the console
        System.out.println("Finished");
        System.out.println("Final distance: " + bestTour.getFitness());
        System.out.println("Final Solution:");
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

    public void setSelectionFcn(String selectionFcn) {
        this.selectionFcn = selectionFcn;
        showConfig();
        System.out.println("New Selection Fcn is " + this.selectionFcn);
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


    public void setOutput(WebView webViewConfig, WebView webViewOutput, LineChart<String, Integer> lineChartFitness) {
        // txtAreaOutput.setText("New TSP Solver Initialized");
        this.webViewConfig = webViewConfig;
        webViewConfig.getEngine().setUserStyleSheetLocation(getClass().getResource("../css/GAConfigStyle.css").toString());

        this.webViewOutput = webViewOutput;
        webViewOutput.getEngine().setUserStyleSheetLocation(getClass().getResource("../css/GASolverStyle.css").toString());


        this.lineChartFitness = lineChartFitness;
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

    // Return the current selection function
    public String getSelectionFcn() {
        return this.selectionFcn;
    }

}
