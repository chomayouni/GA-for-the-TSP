package src;

import java.util.Arrays;

public class TSPSolver {
    public static void main(String[] args) {
        // The Driver for the other classes
        // Output the best solution found
        // I think that this will contain the GUI

    	// Application configuration
    	int printInterval = 1;
    	
    	// GA Configuration
    	int tourSize = 5; // Number of cities
    	int populationSize = 6;
		double mutationRate = 0.05; // 0-1
		double crossoverRate = 0.80; // 0-1
		int tournamentSize = 2; // Must be less than populationSize
		
        // Create and the values of our city for testing
		// TODO (mhorvath) replace cityMap with custom dataset
        int[][] cityMap = {{0,1,200,300,400},{100,0,1,200,300},{200,100,0,1,200},{300,200,100,0,1},{400,300,200,1,0}};
        
        // Initialize GA
        GeneticAlgorithm GA =  new GeneticAlgorithm(populationSize, mutationRate, crossoverRate,
				tournamentSize,tourSize,cityMap);
        
        // Must get fitness before GA operation loop
        GA.fitness();
        
        // Print best initial solution
        Tour bestTour = GA.getFittest();
        System.out.println("Initial route : " + Arrays.toString(bestTour.getRoute()));
        System.out.println("Initial distance : " + bestTour.getFitness());
        System.out.println("");

        // Perform GA operation
        for (int i = 0; i < 20; i++) // This dictates stopping criteria
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
    }
}
