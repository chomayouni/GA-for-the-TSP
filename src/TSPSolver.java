package src;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
    private Map wholeMap;
    // User defined map (Could be whole map, or any combo of cities)
    private SubMap userMap;

    // User defined route, used to create the user defined map
    private int[] userRoute = {};
    // General GA parameters
    private int tourSize;
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int tournamentSize;
    private TextArea txtAreaConfig;
    private TextArea txtAreaOutput;
    private String crossoverFcn;

    // Degbug control for print statements
    private int printInterval;

    public TSPSolver() {
        // Create whole map from dataset
        wholeMap = new Map();
        // // set a user route, and create the sub map for testing that route
        userMap = new SubMap(userRoute);
        // MATT TO DO
    	// Application configuration
    	printInterval = 10;    	
    	// // GA Configuration
    	// // tourSize = userMap.getNumberOfCities();
    	// populationSize = 5;
		// mutationRate = 0.05; // 0-1
		// crossoverRate = 0.80; // 0-1
		// tournamentSize = 2; // Must be less than populationSize
		
        // updateTSP();
        // // Initialize GA
        // GA =  new GeneticAlgorithm(populationSize, mutationRate, crossoverRate,
		// 		tournamentSize,tourSize,userMap.getCityMatrix());
        
    }

    private void distance(String to, String from) {
        wholeMap.distance(to, from);
    }

    private void updateTSP() {
        userMap = null;
        GA = null;
        userMap = new SubMap(userRoute);
        tourSize = userMap.getNumberOfCities();
        System.out.println("New Tour Size is " + this.tourSize);
        // Initialize GA
        GA =  new GeneticAlgorithm(populationSize, mutationRate, crossoverRate,
				tournamentSize,tourSize,userMap.getCityMatrix());
    }


    public void setUserRoute(int[] userRoute) {
        this.userRoute = userRoute;
        System.out.println("New User Route is " + this.userRoute[0]);
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


    public void setOutput(TextArea txtAreaOutput, TextArea txtAreaConfig) {
        // txtAreaOutput.setText("New TSP Solver Initialized");
        this.txtAreaConfig = txtAreaConfig;
        this.txtAreaOutput = txtAreaOutput;
    }

    public void printSetup() {
        txtAreaConfig.setText("Crossover Function set to: " + this.crossoverFcn);
        txtAreaConfig.appendText("\nTour Size is: " + this.tourSize);
        txtAreaConfig.appendText("\nPopulation Size set to: " + this.populationSize);
        txtAreaConfig.appendText("\nMutation Rate set to: " + this.mutationRate);
        txtAreaConfig.appendText("\nCrossover Rate set to: " + this.crossoverRate);
        txtAreaConfig.appendText("\nTournament Size set to: " + this.tournamentSize);
        txtAreaConfig.appendText("\nUser Route includes: " +  userMap.toString());
    }

    public int getTourSize() {
        return tourSize;
    }

    public String[] getCityNames() {
        return wholeMap.getCityNames();
    }

    public int[] getRouteIndices(String[] routeNames) {
        return wholeMap.getRouteIndices(routeNames);
    }

    public String getCrossoverFcn() {
        return this.crossoverFcn;
    }



    public void run() {
        // Must get fitness before GA operation loop
        txtAreaOutput.setText("Running new solver:");
        GA.fitness();
        
        // Print best initial solution
        Tour bestTour = GA.getFittest();
        System.out.println("Initial route : " + Arrays.toString(bestTour.getRoute()));
        txtAreaOutput.appendText("\nInitial route : " + Arrays.toString(bestTour.getRoute()));
        System.out.println("Initial distance : " + bestTour.getFitness());
        txtAreaOutput.appendText("\nInitial distance : " + bestTour.getFitness());
        System.out.println("");


        // Technically faster to do the crossover check first, then call the loop
        switch (crossoverFcn) {
            case "Add":
                System.out.println("Running GA with Add Crossover");
                for (int i = 0; i < 200; i++) // This dictates stopping criteria
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
                        System.out.println("");
                    }
                }
                break;
            case "Sub":
                System.out.println("Running GA with Sub Crossover");
                // Perform GA operation
                for (int i = 0; i < 200; i++) // This dictates stopping criteria
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
                        System.out.println("");
                    }
                }
                break;
        
            default:
                break;
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
