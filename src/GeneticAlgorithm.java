package src;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GeneticAlgorithm {
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int tournamentSize;
    private int tourSize;
    private int cityMap[][];
    private int parent1Arr[];
    private int parent2Arr[];
    
    private Population population;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate,
    						int tournamentSize, int tourSize, int[][] cityMap)
    {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.tournamentSize = tournamentSize;
        this.tourSize = tourSize;
        this.cityMap = cityMap;
        parent1Arr = new int[populationSize];
        parent2Arr = new int[populationSize];
        
        // Can add initialization logic here
        population = new Population(populationSize,tourSize, true);
    }

    // Calculate fitness for each individual
    public void fitness()
    {
    	// For each member of the population
    	for (int i = 0; i < populationSize; i++)
    	{
    		double fitness = 0; 
    		int[] route = population.getRoute(i);
    		// For each city in the population members tour
    		for (int j = 0; j < tourSize; j++)
    		{
    			// Calculate a running total of the distance the
    			// solution covers
    			fitness += cityMap[route[j]-1][route[j+1]-1];
    		}
    		// Update the fitness of the individual
    		population.setFitness(i,fitness);
    	}
    }
    

    public void selection()
    {
    	// TODO
    	for (int i = 0; i < populationSize; i++)
    	{
	    	parent1Arr[i] = tournamentSelection();
	    	parent2Arr[i] = tournamentSelection();
    	}
    	// Debug
    	//System.out.println(Arrays.toString(parent1Arr));
    	//System.out.println(Arrays.toString(parent2Arr));
    }


    private int tournamentSelection()
    {
        double best = Double.MAX_VALUE;
        int index = 0;
        
        // Create a random subset of the population
        Random random = new Random();
        List<Integer> tournament = random.ints(1,populationSize).distinct().
        		limit(tournamentSize).boxed().collect(Collectors.toList());
        
        // Find the most fit member in the population subset
        for (int i = 0; i < tournamentSize; i++)
        {
        	if (best > population.getFitness(tournament.get(i)))
        	{
        		best = population.getFitness(tournament.get(i));
        		index = tournament.get(i);
        	}
        }
        
        // Return the index of the winning individual
        return index;

    }

    // Crossover population
    public void crossover()
    {
    	// Create a temporary population to hold children of current population
    	Population childPop = new Population(populationSize,tourSize, false);
    	
    	Random random = new Random();
    	
    	// Based on the crossover rate, create a new child or pass
    	// a current member of the population forward
    	for (int i = 0; i < populationSize; i++)
    	{ 
    		if (random.nextDouble() < crossoverRate){
    			//childPop.setRoute(i, twoPointCrossover(parent1Arr[i],parent2Arr[i]));
				//commented out the above for testing the greedy crossover 
				childPop.setRoute(i, greedyCrossover(parent1Arr[i],parent2Arr[i]));
    		}
    		else{
    			childPop.setRoute(i, population.getRoute(parent1Arr[i]));
    		}
    	}
    	
    	// Save the new population
    	population = childPop;
    }

	private int[] greedyCrossover(int parent1Idx, int parent2Idx) {
		int[] parent1 = population.getRoute(parent1Idx);
    	int[] child = new int[tourSize+1];
		int[] illegal_cities = new int[tourSize];
		int illegal_cities_index = 0;
		int last_city;
		int shortestDistanceTracker; // covert to double after fix
		int temporary_best_index = 0;
		boolean done;
    	
    	Random random = new Random();
    	// Do not crossover at the first or last index
    	// as they must be the starting city
    	int crossover = 1+random.nextInt(tourSize);
    	
    	
    	for (int i = 0; i < tourSize; i++)
    	{
    		// Before the crossover point, take the parent
    		if (i<crossover)
    		{
    			child[i] = parent1[i];
    		}
    		// After the crossover point, greedily look for the nearest neighbor
    		else
			{
				// setup
				last_city = child[i-1]-1;
				for(int j = 0; j < tourSize; j++) {
					illegal_cities[j] = -1;
				}
				illegal_cities_index = 0;
				done = false;
				shortestDistanceTracker = Integer.MAX_VALUE;
				
				// find the nearest neighbor.
				for(int j = 0; j < tourSize; j++) {
					// any time that we finds a city that is already in the child, we skip it
					if(!contains(child, j+1)) {
						if(cityMap[last_city][j] < shortestDistanceTracker) {
							shortestDistanceTracker = cityMap[last_city][j];
							temporary_best_index = j;
						}
					}
				}
				child[i] = temporary_best_index+1;
			}
    	}
    	// Set the final index to the first city (untouched in for loop)
    	child[tourSize] = 1;
    	
    	return child;
	
		
	}

    private int[] twoPointCrossover(int parent1Idx, int parent2Idx)
    {
    	int[] parent1 = population.getRoute(parent1Idx);
    	int[] parent2 = population.getRoute(parent2Idx);
    	int[] child = new int[tourSize+1];
    	
    	Random random = new Random();
    	// Do not crossover at the first or last index
    	// as they must be the starting city
    	int crossover = 1+random.nextInt(tourSize);
    	
    	
    	for (int i = 0; i < tourSize; i++)
    	{
    		if (i<crossover)
    		{
    			child[i] = parent1[i];
    		}
    		else
    		{
    			if (contains(child, parent2[i]) == true)
    			{
    				child[i] = getNextValidCity(child);
    			}
				else
				{
					child[i] = parent2[i];
				}
    		}
    	}
    	// Set the final index to the first city (untouched in for loop)
    	child[tourSize] = 1;
    	
    	return child;
    }
    
    private int getNextValidCity(int[] route)
    {
    	// For every city in the tour
    	for (int i = 0; i <= tourSize; i++)
    	{
    		// Check if the index i is contained in the tour
    		if (contains(route, i) == false)
    		{
    			return i;
    		}
    	}
    	
    	// Error should assert if this is reached
    	return 0;
    }
    
    // Utility function, check if an array of integers
    // contains a specified integer
    private boolean contains(int[] array, int target)
    {
    	for ( int i = 0; i < array.length; i++)
    	{
    		if (array[i] == target)
    			return true;
    	}
    	return false;
    }

    // Mutate population
    public void mutation()
    {
    	Random random = new Random();
    	
    	// Loop through the population. Rarely, mutate a member of the population.
    	for (int i = 0; i < populationSize; i++)
    	{ 
    		if (random.nextDouble() < mutationRate)
    		{
    			swapMutation(i);
    		}
    	}
    }
    
    private void swapMutation(int populationIdx)
    {
    	Random random = new Random();
    	
    	// Create two unique indexes to swap cities
    	// Do not mutate at the first or last index
    	// as they must be the starting city
    	int mutationIdx1 = 1+random.nextInt(tourSize-1);
    	int mutationIdx2;
    	do
    	{
    		mutationIdx2 = 1+random.nextInt(tourSize-1);
    	} while (mutationIdx1 == mutationIdx2);
    	
    	// Debug
//    	System.out.println("Mutation index 1: " + mutationIdx1);
//    	System.out.println("Mutation index 2: " + mutationIdx2);
    	
    	int[] mutant = new int[tourSize+1];
    	mutant = population.getRoute(populationIdx);
//    	System.out.println("Original mutant:\t"+Arrays.toString(mutant));
    	
    	int temp = mutant[mutationIdx1];
    	mutant[mutationIdx1] = mutant[mutationIdx2];
    	mutant[mutationIdx2] = temp;
    	
    	// Debug
    	// Population is already updated by this point. Not sure why
//    	System.out.println("Original Population:\t"+Arrays.toString(population.getRoute(populationIdx)));
//    	System.out.println("Updated Mutant:\t\t"+Arrays.toString(mutant));
    	
    	population.setRoute(populationIdx, mutant);
    	
//    	 Debug
//    	System.out.println("Updated Population:\t"+Arrays.toString(population.getRoute(populationIdx)));
    	
    }
    

    // Get most fit individual
    public Tour getFittest()
    {
    	double bestFitness = Double.MAX_VALUE;
    	int bestIndex = 0;
    	for (int i = 0; i < populationSize; i++)
    	{
    		if (population.getFitness(i) < bestFitness)
    		{
    			bestFitness = population.getFitness(i);
    			bestIndex = i;
    		}
    	}
    	return population.getTour(bestIndex);
    }
    
}

