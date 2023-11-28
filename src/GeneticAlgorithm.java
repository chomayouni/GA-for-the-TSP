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
        parent1Arr = new int[populationSize/2];
        parent2Arr = new int[populationSize/2];
        
        // Can add initialization logic here
        population = new Population(populationSize,tourSize, true);
    }

    // Calculate fitness for each individual
    // To reduce calculations, we use the COST as fitness
    // and are aware in our implementation that lower "fitness" (cost)
    // is better
    public void fitness()
    {
    	// For each member of the population
    	for (int i = 0; i < populationSize; i++)
    	{
    		double fitness = 0; 
    		int[] route = population.getRoute(i);
    		
    		// Debug
			// System.out.println("(fitness) route " + i + " : " + Arrays.toString(route));
    		
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
    	for (int i = 0; i < populationSize/2; i++)
    	{
//	    	parent1Arr[i] = tournamentSelection();
//	    	parent2Arr[i] = tournamentSelection();
	    	parent1Arr[i] = proportionalSelection();
	    	parent2Arr[i] = proportionalSelection();
    	}
    	
    	// Debug
    	// System.out.println("(selection) parent1Arr : " + Arrays.toString(parent1Arr));
    	// System.out.println("(selection) parent2Arr : " + Arrays.toString(parent2Arr));
    }

    // Paper 4 Selection
    private int proportionalSelection() {
    	double totalFitness = 0;
    	double runningSumFitness = 0;
    	double[] survivalProbability = new double[populationSize];
    	// Calculate total fitness 
    	for (int i = 0; i < populationSize; i++)
        {
        	totalFitness += 1/population.getFitness(i);
        }
    	
    	// Normalize, store each population members index for survival probability
    	for (int i = 0; i < populationSize; i++)
        {
    		runningSumFitness += (1/population.getFitness(i))/totalFitness;
    		
    		// Debug
    		// System.out.println("runningSumFitness : " + runningSumFitness);
        	survivalProbability[i] = runningSumFitness;
        }
    	
        // Generate a random value
    	double randomValue = (double) (Math.random());

    	// Debugging
    	// System.out.println("totalFitness :" + totalFitness);
    	// System.out.println("survivalProbability : " + Arrays.toString(survivalProbability));
        // System.out.println("randomValue :" + randomValue);
        
        // Select a parent based on proportional fitness (weighted roulette)
        for (int i = 0; i < populationSize; i++)
        {
        	if (randomValue < survivalProbability[i])
        	{
        		// Debugging
            	// System.out.println("Selected index :" + i);
        		
            	// Return the index of the randomly selected individual
            	return i;
            }
        }

        // This should not happen, but just in case
        return populationSize-1;
    }
    
    // Selection not from paper
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
    	// Each crossover returns two children, hold them in this temp array
    	int[][] children = new int[2][tourSize];
    	
    	Random random = new Random();
    	
    	// Based on the crossover rate, create a new child or pass
    	// a current member of the population forward
    	for (int i = 0; i < populationSize; i+=2)
    	{ 
    		if (random.nextDouble() < crossoverRate)
    		{
    			children = onePointCrossover(parent1Arr[i/2],parent2Arr[i/2]);
//				children = greedyCrossover(parent1Arr[i/2],parent2Arr[i/2]);
//    			children = twoPointCrossover(parent1Arr[i/2],parent2Arr[i/2]);
    			// Each set of parents should create two children
    			for (int j = 0; j < 2; j++)
    			{
	    			childPop.setRoute(i+j, children[j]);
    			}
    		}
    		else
    		{
    			for (int j = 0; j < 2; j++)
    			{
    				if (j == 0)
    				{
    					childPop.setRoute(i+j, population.getRoute(parent1Arr[i/2]));
    				}
    				else
    				{
    					childPop.setRoute(i+j, population.getRoute(parent2Arr[i/2]));
    				}
    			}
    		}
    	}
    	// Debug
    	// for (int i = 0; i < populationSize; i++)
    	// 	 System.out.println("(crossover) childPop " + i + " : " + Arrays.toString(childPop.getRoute(i)));
    	
    	// Save the new population
    	population = childPop;
    }

    // Paper four
	private int[][] greedyCrossover(int parent1Idx, int parent2Idx) {
		int[] parent1 = population.getRoute(parent1Idx);
		int[] parent2 = population.getRoute(parent2Idx);
    	int[][] children = new int[2][tourSize+1];
		int[] illegal_cities = new int[tourSize];
		int last_city;
		int shortestDistanceTracker; // covert to double after fix
		int temporary_best_index = 0;
    	
    	Random random = new Random();
    	// Do not crossover at the first or last index
    	// as they must be the starting city
    	int crossover = 1+random.nextInt(tourSize);
    	
    	// Create the first child
    	for (int i = 0; i < tourSize; i++)
    	{
    		// Before the crossover point, take the parent
    		if (i<crossover)
    		{
    			children[0][i] = parent1[i];
    		}
    		// After the crossover point, greedily look for the nearest neighbor
    		else
			{
				// setup
				last_city = children[0][i-1]-1;
				for(int j = 0; j < tourSize; j++) {
					illegal_cities[j] = -1;
				}
				shortestDistanceTracker = Integer.MAX_VALUE;
				
				// find the nearest neighbor.
				for(int j = 0; j < tourSize; j++) {
					// any time that we finds a city that is already in the child, we skip it
					if(!contains(children[0], j+1)) {
						if(cityMap[last_city][j] < shortestDistanceTracker) {
							shortestDistanceTracker = cityMap[last_city][j];
							temporary_best_index = j;
						}
					}
				}
				children[0][i] = temporary_best_index+1;
			}
    	}
    	// Set the final index to the first city (untouched in for loop)
    	children[0][tourSize] = 1;
    	
    	// Create the second child
    	for (int i = 0; i < tourSize; i++)
    	{
    		// Before the crossover point, take the parent
    		if (i<crossover)
    		{
    			children[1][i] = parent2[i];
    		}
    		// After the crossover point, greedily look for the nearest neighbor
    		else
			{
				// setup
				last_city = children[1][i-1]-1;
				for(int j = 0; j < tourSize; j++) {
					illegal_cities[j] = -1;
				}
				shortestDistanceTracker = Integer.MAX_VALUE;
				
				// find the nearest neighbor.
				for(int j = 0; j < tourSize; j++) {
					// any time that we finds a city that is already in the child, we skip it
					if(!contains(children[1], j+1)) {
						if(cityMap[last_city][j] < shortestDistanceTracker) {
							shortestDistanceTracker = cityMap[last_city][j];
							temporary_best_index = j;
						}
					}
				}
				children[1][i] = temporary_best_index+1;
			}
    	}
    	// Set the final index to the first city (untouched in for loop)
    	children[1][tourSize] = 1;
    	
    	return children;
	}
	
    // Paper three
    private int[][] twoPointCrossover(int parent1, int parent2) {
    	// Initialize the two child chromosomes
        int[][] children = new int[2][tourSize+1];
        Arrays.fill(children[0], -1);
        Arrays.fill(children[1], -1);

        // Select the two points to crossover
        int startPoint = (int) (Math.random() * tourSize);
        int endPoint = (int) (Math.random() * tourSize);

        // Ensure the start point is before the end point
        if (startPoint > endPoint) {
            int temp = startPoint;
            startPoint = endPoint;
            endPoint = temp;
        }
        
        // Create the first child
        // Take parent one inside of the two crossover points
        System.arraycopy(population.getRoute(parent1), startPoint, children[0], startPoint, endPoint - startPoint);

        // Take parent two outside of the two crossover points if possible
        for (int i = 0; i < tourSize; i++) {
            if (children[0][i] == -1) {
                for (int city : population.getRoute(parent2)) {
                    if (!contains(children[0], city)) {
                        children[0][i] = city;
                        break;
                    }
                }
            }
        }
        // Final city must be the starting city
        children[0][tourSize] = 1;
        
        // Create the second child
        // Take parent two inside of the two crossover points
        System.arraycopy(population.getRoute(parent2), startPoint, children[1], startPoint, endPoint - startPoint);

        // Take parent one outside of the two crossover points if possible
        for (int i = 0; i < tourSize; i++) {
            if (children[1][i] == -1) {
                for (int city : population.getRoute(parent1)) {
                    if (!contains(children[1], city)) {
                        children[1][i] = city;
                        break;
                    }
                }
            }
        }
        // Final city must be the starting city
        children[1][tourSize] = 1;
        
        return children;
    }

    // NOTE this is for paper three, it is unclear whether they use
    // one-point crossover or two-point crossover
    private int[][] onePointCrossover(int parent1Idx, int parent2Idx)
    {
    	int[] parent1 = population.getRoute(parent1Idx);
    	int[] parent2 = population.getRoute(parent2Idx);
    	int[][] children = new int[2][tourSize+1];
    	
    	Random random = new Random();
    	// Do not crossover at the first or last index
    	// as they must be the starting city
    	int crossover = 1+random.nextInt(tourSize);
    	
    	// Create the first child
    	for (int i = 0; i < tourSize; i++)
    	{
    		if (i<crossover)
    		{
    			children[0][i] = parent1[i];
    		}
    		else
    		{
    			if (contains(children[0], parent2[i]) == true)
    			{
    				children[0][i] = getNextValidCity(children[0]);
    			}
				else
				{
					children[0][i] = parent2[i];
				}
    		}
    	}
    	// Set the final index to the first city (untouched in for loop)
    	children[0][tourSize] = 1;
    	// Create the second child
    	for (int i = 0; i < tourSize; i++)
    	{
    		if (i<crossover)
    		{
    			children[1][i] = parent2[i];
    		}
    		else
    		{
    			if (contains(children[1], parent1[i]) == true)
    			{
    				children[1][i] = getNextValidCity(children[1]);
    			}
				else
				{
					children[1][i] = parent1[i];
				}
    		}
    	}
    	// Set the final index to the first city (untouched in for loop)
    	children[1][tourSize] = 1;
    	
    	return children;
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

