package src;

import java.util.Arrays;
import java.util.List;
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
    private TextArea txtAreaConfig;
    private TextArea txtAreaOutput;
    private LineChart<String, Integer> lineChartFitness;
    private XYChart.Series<String, Integer> fitnessData;
    // Degbug control for print statements
    private int printInterval;

    public TSPSolver() {
        map = new Map();
    	printInterval = 200;    	

    }

    private void updateTSP() {
        GA = null;
        tourSize = map.getUserNumberOfCities();
        System.out.println("New Tour Size is " + this.tourSize);
        // Initialize GA
        GA =  new GeneticAlgorithm(populationSize, mutationRate, crossoverFcn,
                crossoverRate, tournamentSize,tourSize,map.getUserCityMatrix());
    }


    public void setUserRoute(int[] userRoute) {
        map.setUserRoute(userRoute);
        // System.out.println("New User Route is " + this.userRoute[0]);
        // User route and actual city data are not part of the TSP, they are part of the map object. 
        //      so those must be updated first via the update TSP then we can print the setup (which prints) 
        //      the routes
        updateTSP();
        printSetup();
    } 

    public void setCrossoverFcn(String crossoverFcn) {
        this.crossoverFcn = crossoverFcn;
        printSetup();
        System.out.println("New Crossover Fcn is " + this.crossoverFcn);
        // updateTSP();
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        printSetup();
        System.out.println("New Population Size is " + this.populationSize);
        updateTSP();
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
        printSetup();
        System.out.println("New Mutation Rate is " + this.mutationRate);
        updateTSP();
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
        printSetup();
        System.out.println("New Crossover Rate is " + this.crossoverRate);
        updateTSP();
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
        printSetup();
        System.out.println("New Tournament Size is " + this.tournamentSize);
        updateTSP();
    }


    public void setOutput(TextArea txtAreaOutput, TextArea txtAreaConfig, LineChart<String, Integer> lineChartFitness) {
        // txtAreaOutput.setText("New TSP Solver Initialized");
        this.txtAreaConfig = txtAreaConfig;
        this.txtAreaOutput = txtAreaOutput;
        this.lineChartFitness = lineChartFitness;
        fitnessData = new XYChart.Series<>();
        lineChartFitness.getData().add(fitnessData);
    }

    public void printSetup() {
        txtAreaConfig.setText("Crossover Function set to: " + this.crossoverFcn);
        txtAreaConfig.appendText("\nTour Size is: " + this.tourSize);
        txtAreaConfig.appendText("\nPopulation Size set to: " + this.populationSize);
        txtAreaConfig.appendText("\nMutation Rate set to: " + this.mutationRate);
        txtAreaConfig.appendText("\nCrossover Rate set to: " + this.crossoverRate);
        txtAreaConfig.appendText("\nTournament Size set to: " + this.tournamentSize);
        txtAreaConfig.appendText("\nUser Route includes: " +  map.toStringUser());
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

    // Add a city to the data base
    public void addCity(String city) {
        map.addCity(city);
        map.reload();

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
            for (int i = 0; i < 10000; i++) // This dictates stopping criteria
            {
                GA.selection();
                GA.crossover();
                GA.mutation();
                GA.fitness();
                // Intermediate output for sanity
                if (i%printInterval == 0)
                {        
                    txtAreaOutput.appendText("\n---------------------");
                    bestTour = GA.getFittest();
                    System.out.println("Iteration "+i);
                    txtAreaOutput.appendText("\nIteration "+i);
                    System.out.println("Route : " + Arrays.toString(bestTour.getRoute()));
                    txtAreaOutput.appendText("\nRoute : " + Arrays.toString(bestTour.getRoute()));
                    System.out.println("Distance : " + bestTour.getFitness());
                    txtAreaOutput.appendText("\nDistance : " + bestTour.getFitness());
                    updateChart(i, bestTour.getFitness());
                    System.out.println("");
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
}
