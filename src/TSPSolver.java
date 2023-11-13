package src;

import java.util.Arrays;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.input.CharInput;

public class TSPSolver {
    public static void main(String[] args) {



        // Create database from city txt file data
        cities database = new cities();
        // MATT TO DO
        // The Driver for the other classes
        // Output the best solution found
        // I think that this will contain the GUI

    	// Application configuration
    	int printInterval = 10;
    	
    	// GA Configuration
    	int tourSize = database.getNumberOfCities();
    	int populationSize = 5;
		double mutationRate = 0.05; // 0-1
		double crossoverRate = 0.80; // 0-1
		int tournamentSize = 2; // Must be less than populationSize
		

        
        // Initialize GA
        GeneticAlgorithm GA =  new GeneticAlgorithm(populationSize, mutationRate, crossoverRate,
				tournamentSize,tourSize,database.getCityMatrix());
        
        // Must get fitness before GA operation loop
        GA.fitness();
        
        // Print best initial solution
        Tour bestTour = GA.getFittest();
        System.out.println("Initial route : " + Arrays.toString(bestTour.getRoute()));
        System.out.println("Initial distance : " + bestTour.getFitness());
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
	        	System.out.println("Route : " + Arrays.toString(bestTour.getRoute()));
	            System.out.println("Distance : " + bestTour.getFitness());
	            System.out.println("");
        	}
        }

        // Print results
        bestTour = GA.getFittest();
        System.out.println("Finished");
        System.out.println("Final distance: " + bestTour.getFitness());
        System.out.println("Final Solution:");
        System.out.println(Arrays.toString(bestTour.getRoute()));
        database.getRouteNames(bestTour.getRoute());

    }
}
