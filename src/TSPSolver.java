package src;

import java.util.Arrays;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
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
    private int[] userRoute = {1,2,5,7,8,9};
    // General GA parameters
    private int tourSize;
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int tournamentSize;

    // Degbug control for print statements
    private int printInterval;

    public TSPSolver() {
        // Create whole map from dataset
        wholeMap = new Map();
        // set a user route, and create the sub map for testing that route
        userMap = new SubMap(userRoute);


        // MATT TO DO

    	// Application configuration
    	printInterval = 10;
    	
    	// GA Configuration
    	tourSize = userMap.getNumberOfCities();
    	populationSize = 5;
		mutationRate = 0.05; // 0-1
		crossoverRate = 0.80; // 0-1
		tournamentSize = 2; // Must be less than populationSize
		

        
        // Initialize GA
        GA =  new GeneticAlgorithm(populationSize, mutationRate, crossoverRate,
				tournamentSize,tourSize,userMap.getCityMatrix());
        


    }

    public void run(TextArea textOutput) {
        // Must get fitness before GA operation loop
        GA.fitness();
        
        // Print best initial solution
        Tour bestTour = GA.getFittest();
        System.out.println("Initial route : " + Arrays.toString(bestTour.getRoute()));
        // textOutput.appendText("\nInitial route : " + Arrays.toString(bestTour.getRoute()));
        System.out.println("Initial distance : " + bestTour.getFitness());
        // textOutput.appendText("\nInitial distance : " + bestTour.getFitness());
        System.out.println("");

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
	        	bestTour = GA.getFittest();
	        	System.out.println("Iteration "+i);
                // textOutput.appendText("\nIteration "+i);
	        	System.out.println("Route : " + Arrays.toString(bestTour.getRoute()));
                // textOutput.appendText("\nRoute : " + Arrays.toString(bestTour.getRoute()));
	            System.out.println("Distance : " + bestTour.getFitness());
                // textOutput.appendText("\nDistance : " + bestTour.getFitness());
	            System.out.println("");
        	}
        }

        // Print results
        bestTour = GA.getFittest();
        System.out.println("Finished");
        // textOutput.appendText("\nFinished");
        System.out.println("Final distance: " + bestTour.getFitness());
        // textOutput.appendText("\nFinal distance: " + bestTour.getFitness());
        System.out.println("Final Solution:");
        // textOutput.appendText("\nFinal Solution:");
        // textOutput.setText("Finished");
        System.out.println(Arrays.toString(bestTour.getRoute()));
        wholeMap.printRouteNames(bestTour.getRoute());
    }
}
