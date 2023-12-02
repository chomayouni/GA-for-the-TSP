package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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

	private String crossoverFcn;
	private String selectionFcn;
    
    private Population population;

    public GeneticAlgorithm(String crossoverFcn, String selectionFcn, int populationSize, double mutationRate, double crossoverRate,
    						int tournamentSize, int tourSize, int[][] cityMap)
    {
		this.crossoverFcn = crossoverFcn;
		this.selectionFcn = selectionFcn;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.tournamentSize = tournamentSize;
        this.tourSize = tourSize;
        this.cityMap = cityMap;

		// Initialize the population
		newPopulation();
        // parent1Arr = new int[(int)Math.ceil((double)populationSize/2)];
        // parent2Arr = new int[(int)Math.ceil((double)populationSize/2)];
        
        // // Can add initialization logic here
        // population = new Population(populationSize,tourSize, true);
    }


	// Create a new population
	public void newPopulation()
	{
		parent1Arr = new int[(int)Math.ceil((double)populationSize/2)];
		parent2Arr = new int[(int)Math.ceil((double)populationSize/2)];
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

		switch (selectionFcn) {
			case "Tournament Selection":
				// System.out.println("Performing " + selectionFcn);
				for (int i = 0; i < populationSize/2; i++)
				{
					parent1Arr[i] = tournamentSelection();
					parent2Arr[i] = tournamentSelection();
				}
				break;
			case "Proportional Selection":
				// System.out.println("Performing " + selectionFcn);
				for (int i = 0; i < populationSize/2; i++)
				{
					parent1Arr[i] = proportionalSelection();
					
					parent2Arr[i] = proportionalSelection();
				}
				break;
			default:
				System.out.println("Invalid selection function passed into GA");
				break;
		}
    }

    // Paper 4 Selection
    private int proportionalSelection() {
    	double totalFitness = 0;
    	double runningSumFitness = 0;
    	double[] survivalProbability = new double[populationSize];    	
    	double minFitness = (double)Double.MAX_VALUE;
    	
    	// Find the minimum fitness to normalize
    	for (int i = 0; i < populationSize; i++)
    	{
    		if (population.getFitness(i) < minFitness)
    		{
    			minFitness = population.getFitness(i);
    		}
    	}
    	
    	minFitness--;
    	
    	// Calculate total fitness 
    	for (int i = 0; i < populationSize; i++)
        {
        	totalFitness += 1/(population.getFitness(i)-minFitness);
        }
    	
    	// Normalize, store each population members index for survival probability
    	for (int i = 0; i < populationSize; i++)
        {
    		runningSumFitness += (1/(population.getFitness(i)-minFitness))/totalFitness;
        	survivalProbability[i] = runningSumFitness;
        }
    	
        // Generate a random value
    	double randomValue = (double) (Math.random());

    	// Debugging
//    	 System.out.println("totalFitness :" + totalFitness);
//    	 System.out.println("survivalProbability : " + Arrays.toString(survivalProbability));
//       System.out.println("randomValue :" + randomValue);
        
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
        double best = (double)Double.MAX_VALUE;
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
    	
		switch (crossoverFcn) {
			case "One-Point Crossover":
				// System.out.println("Performing " + crossoverFcn);
				// Based on the crossover rate, create a new child or pass
				// a current member of the population forward
				for (int i = 0; i < populationSize; i+=2)
				{ 
					if (random.nextDouble() < crossoverRate)
					{
						children = onePointCrossover(parent1Arr[i/2],parent2Arr[i/2]);
						// Each set of parents should create two children
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, children[0]);
						}
						else
						{
							for (int j = 0; j < 2; j++)
							{
								childPop.setRoute(i+j, children[j]);
							}
						}
					}
					else
					{
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, population.getRoute(parent1Arr[i/2]));
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
				}
				// Debug
				// for (int i = 0; i < populationSize; i++)
				// 	 System.out.println("(crossover) childPop " + i + " : " + Arrays.toString(childPop.getRoute(i)));
				
				// Save the new population
				population = childPop;
				break;
		
			case "Two-Point Crossover":
				// System.out.println("Performing " + crossoverFcn);
				// Based on the crossover rate, create a new child or pass
				// a current member of the population forward
				for (int i = 0; i < populationSize; i+=2)
				{ 
					if (random.nextDouble() < crossoverRate)
					{
		   				children = twoPointCrossover(parent1Arr[i/2],parent2Arr[i/2]);
						// Each set of parents should create two children
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, children[0]);
						}
						else
						{
							for (int j = 0; j < 2; j++)
							{
								childPop.setRoute(i+j, children[j]);
							}
						}
					}
					else
					{
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, population.getRoute(parent1Arr[i/2]));
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
				}
				// Debug
				// for (int i = 0; i < populationSize; i++)
				// 	 System.out.println("(crossover) childPop " + i + " : " + Arrays.toString(childPop.getRoute(i)));
				
				// Save the new population
				population = childPop;
				break;

			case "CX Crossover":
				// System.out.println("Performing " + crossoverFcn);
				// Based on the crossover rate, create a new child or pass
				// a current member of the population forward
				for (int i = 0; i < populationSize; i+=2)
				{ 
					if (random.nextDouble() < crossoverRate)
					{
		   				children = cxCrossover(parent1Arr[i/2],parent2Arr[i/2]);
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, children[0]);
						}
						else
						{
							for (int j = 0; j < 2; j++)
							{
								childPop.setRoute(i+j, children[j]);
							}
						}
					}
					else
					{
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, population.getRoute(parent1Arr[i/2]));
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
				}
				// Debug
				// for (int i = 0; i < populationSize; i++)
				// 	 System.out.println("(crossover) childPop " + i + " : " + Arrays.toString(childPop.getRoute(i)));
				
				// Save the new population
				population = childPop;
				break;
			case "CX2 Crossover":
				// System.out.println("Performing " + crossoverFcn);
				// Based on the crossover rate, create a new child or pass
				// a current member of the population forward
				for (int i = 0; i < populationSize; i+=2)
				{ 
					if (random.nextDouble() < crossoverRate)
					{
		   				children = cx2Crossover(parent1Arr[i/2],parent2Arr[i/2]);
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, children[0]);
						}
						else
						{
							for (int j = 0; j < 2; j++)
							{
								childPop.setRoute(i+j, children[j]);
							}
						}
					}
					else
					{
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, population.getRoute(parent1Arr[i/2]));
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
				}
				// Debug
				// for (int i = 0; i < populationSize; i++)
				// 	 System.out.println("(crossover) childPop " + i + " : " + Arrays.toString(childPop.getRoute(i)));
				
				// Save the new population
				population = childPop;
				break;
			case "Greedy Crossover":
				// System.out.println("Performing " + crossoverFcn);
				// Based on the crossover rate, create a new child or pass
				// a current member of the population forward
				for (int i = 0; i < populationSize; i+=2)
				{ 
					if (random.nextDouble() < crossoverRate)
					{
						children = greedyCrossover(parent1Arr[i/2],parent2Arr[i/2]);
						// Each set of parents should create two children
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, children[0]);
						}
						else
						{
							for (int j = 0; j < 2; j++)
							{
								childPop.setRoute(i+j, children[j]);
							}
						}
					}
					else
					{
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, population.getRoute(parent1Arr[i/2]));
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
				}
				// Save the new population
				population = childPop;
				break;
				
			case "PMX Crossover":
				// System.out.println("Performing " + crossoverFcn);
				// Based on the crossover rate, create a new child or pass
				// a current member of the population forward
				for (int i = 0; i < populationSize; i+=2)
				{ 
					if (random.nextDouble() < crossoverRate)
					{
						children = pmxCrossover(parent1Arr[i/2],parent2Arr[i/2]);
						// Each set of parents should create two children
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, children[0]);
						}
						else
						{
							for (int j = 0; j < 2; j++)
							{
								childPop.setRoute(i+j, children[j]);
							}
						}
					}
					else
					{
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, population.getRoute(parent1Arr[i/2]));
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
				}
				// Save the new population
				population = childPop;
				break;
				
			case "OX Crossover":
				// System.out.println("Performing " + crossoverFcn);
				// Based on the crossover rate, create a new child or pass
				// a current member of the population forward
				for (int i = 0; i < populationSize; i+=2)
				{ 
					if (random.nextDouble() < crossoverRate)
					{
						children = oxCrossover(parent1Arr[i/2],parent2Arr[i/2]);
						// Each set of parents should create two children
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, children[0]);
						}
						else
						{
							for (int j = 0; j < 2; j++)
							{
								childPop.setRoute(i+j, children[j]);
							}
						}
					}
					else
					{
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, population.getRoute(parent1Arr[i/2]));
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
				}
				// Debug
				// for (int i = 0; i < populationSize; i++)
				// 	 System.out.println("(crossover) childPop " + i + " : " + Arrays.toString(childPop.getRoute(i)));
				
				// Save the new population
				population = childPop;
				break;
			case "SCX Crossover": // This one only produces one child so its quirky
				// System.out.println("Performing " + crossoverFcn);
				// Based on the crossover rate, create a new child or pass
				// a current member of the population forward
				for (int i = 0; i < populationSize; i+=2)
				{ 
					if (random.nextDouble() < crossoverRate)
					{
						// Each set of parents should create two children
						if (i == populationSize-1 && i%2 == 0)
						{
							int[] child = scxCrossover(parent1Arr[i/2],parent2Arr[i/2]);
							childPop.setRoute(i, child);
						}
						else
						{
							int[] child = scxCrossover(parent1Arr[i/2],parent2Arr[i/2]);
							childPop.setRoute(i, child);
							if (i == populationSize-2)
							{
								child = scxCrossover(parent1Arr[i/2],parent2Arr[(i/2)-1]);
								childPop.setRoute(i+1, child);
							}
							else
							{
								child = scxCrossover(parent1Arr[i/2],parent2Arr[(i/2)+1]);
								childPop.setRoute(i+1, child);
							}
						}
					}
					else
					{
						if (i == populationSize-1 && i%2 == 0)
						{
							childPop.setRoute(i, population.getRoute(parent1Arr[i/2]));
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
				}
				// Debug
				// for (int i = 0; i < populationSize; i++)
				// 	 System.out.println("(crossover) childPop " + i + " : " + Arrays.toString(childPop.getRoute(i)));
				
				// Save the new population
				population = childPop;
				break;
			default:
				System.out.println("Invalid crossover function passed into GA");
				break;
		}
    }

    // Started implementing ES from https://arxiv.org/pdf/1402.4699.pdf, 
    // then I decided it was too complicated with due date of the project coming up
//    public int[] esCrossover(int parent1Idx, int parent2Idx)
//    {
//    	int[] parent1 = population.getRoute(parent1Idx);
//    	int[] parent2 = population.getRoute(parent2Idx);
//    	int[] child = new int[tourSize+1];
//    	Arrays.fill(child, -1);
//    	// First and last city must always be the starting city
//    	child[0] = 1;
//    	child[tourSize] = 1;
//    	
//    	// Make the child by tracing edges in both parents
//    	while (true)
//    	{
//	    	int startCity;
//	    	do
//	    	{
//	    		startCity = Math.max(2,(int) (Math.random() * tourSize));
//	    	} while (contains(child,startCity) || contains(child,parent1[indexOf(parent1,startCity)+1]));
//	    	
//	    	int parentIdx = startCity;
//	    	int childIdx = 1;
//	    	child[childIdx] = parent1[parentIdx];
//	    	childIdx++;
//	    	parentIdx++;
//	    	
//	    	while(true)
//	    	{
//	    		child[childIdx] = parent1[parentIdx];
//	    		childIdx++;
//	    		parentIdx = indexOf(parent2, parent1[parentIdx]);
//	    		parentIdx++;
//		    	if (contains(child,parent2[parentIdx]))
//		    	{
//		    		break;
//		    	}
//		    	child[childIdx] = parent2[parentIdx];
//		    	childIdx++;
//		    	parentIdx = indexOf(parent1, parent2[parentIdx]);
//	    	}
//	    	if(!contains(child, -1))
//	    	{
//	    		break;
//	    	}
//    	}
//        
//        return child;
//    }
    
    public int[] scxCrossover(int parent1Idx, int parent2Idx)
    {
    	int[] parent1 = population.getRoute(parent1Idx);
    	int[] parent2 = population.getRoute(parent2Idx);
    	int[] child = new int[tourSize+1];
    	Arrays.fill(child, -1);
    	// First and last city must always be the starting city
    	child[0] = 1;
        child[tourSize] = 1;
        int childIdx = 1;
        
    	int p = parent1[0];
    	int alpha;
    	int beta;
    	
    	do
    	{
    		alpha = -1;
    		beta = -1;
	    	for (int i = indexOf(parent1,p)+1; i < tourSize+1; i++)
	    	{
	    		if (!contains(child,parent1[i]))
	    		{
	    			alpha = parent1[i];
	    			break;
	    		}
	    	}
	    	if (alpha == -1)
	    	{
	    		for (int i = 1; i < tourSize+1; i++)
	    		{
	    			if (!contains(child,i))
	    			{
	    				alpha = i;
	    				break;
	    			}
	    		}
	    	}
	    	for (int i = indexOf(parent2,p)+1; i < tourSize+1; i++)
	    	{
	    		if (!contains(child,parent2[i]))
	    		{
	    			beta = parent2[i];
	    			break;
	    		}
	    	}
	    	if (beta == -1)
	    	{
	    		for (int i = 1; i < tourSize+1; i++)
	    		{
	    			if (!contains(child,i))
	    			{
	    				beta = i;
	    				break;
	    			}
	    		}
	    	}
	    	
	    	if (cityMap[p-1][alpha-1] < cityMap[p-1][beta-1])
	    	{
	    		p = alpha;
	    		child[childIdx] = alpha;
	    		
	    	}
	    	else
	    	{
	    		p = beta;
	    	}
	    	child[childIdx] = p;
	    	childIdx++;
    	} while (contains(child,-1));
    	
//    	System.out.println("(SCX) Child : " + Arrays.toString(child));
    	
        return child;
    }
    
    // Paper four Order Crossover
    public int[][] oxCrossover(int parent1Idx, int parent2Idx)
    {
    	int[] parent1 = population.getRoute(parent1Idx);
    	int[] parent2 = population.getRoute(parent2Idx);
    	int[][] children = new int[2][tourSize+1];
    	Arrays.fill(children[0], -1);
        Arrays.fill(children[1], -1);
        List<Integer> subTour1 = new ArrayList<Integer>();
        List<Integer> subTour2 = new ArrayList<Integer>();
        
        // Select the two points to crossover
        int startPoint = Math.max(1,(int) (Math.random() * tourSize));
        int endPoint = Math.max(1,(int) (Math.random() * tourSize));

        // Ensure the start point is before the end point
        if (startPoint > endPoint) {
            int temp = startPoint;
            startPoint = endPoint;
            endPoint = temp;
        }
        
        // Children take the matching parents between the points
        for (int i = startPoint; i < endPoint; i++)
        {
        	children[0][i] = parent1[i];
        	children[1][i] = parent2[i];
        }
        
        // Create sub tours
        int idx = endPoint;
        do
        {
        	if (!contains(children[0],parent2[idx]))
        	{
        		subTour1.add(parent2[idx]);
        	}
        	if (!contains(children[1],parent1[idx]))
        	{
        		subTour2.add(parent1[idx]);
        	}
        	idx++;
        	if (idx == tourSize)
        	{
        		idx = 1;
        	}
        } while (idx != endPoint);
        
        // Finish populating the children from the sub tours
        int subTourIdx = 0;
        do
        {
        	
        	children[0][idx] = subTour1.get(subTourIdx);
        	children[1][idx] = subTour2.get(subTourIdx);
        	subTourIdx++;
        	idx++;
        	if (idx == tourSize)
        	{
    			idx = 1;
        	}
        } while (idx != startPoint);
        
        // First and last city must always be the starting city
    	children[0][0] = 1;
        children[1][0] = 1;
    	children[0][tourSize] = 1;
        children[1][tourSize] = 1;
        
        return children;
    }
    
    // Paper four partially-mapped crossover
    public int[][] pmxCrossover(int parent1Idx, int parent2Idx)
    {
    	int[] parent1 = population.getRoute(parent1Idx);
    	int[] parent2 = population.getRoute(parent2Idx);
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
    	
        // Children take the opposing parents between the points
        for (int i = startPoint; i < endPoint; i++)
        {
        	children[0][i] = parent2[i];
        	children[1][i] = parent1[i];
        }
        
        // If possible, take the matching parent outside of the two points
        for (int i = 0; i < tourSize; i++)
        {
        	if (i < startPoint || i >= endPoint)
        	{
        		if (!contains(children[0], parent1[i]))
        		{
        			children[0][i] = parent1[i];
        		}
        		if (!contains(children[1], parent2[i]))
        		{
        			children[1][i] = parent2[i];
        		}
        	}
        }
        
        // Fill in the remaining indices by swapping the locations
        // of missing cities in parent 1 and parent 2.
        for (int i = 0; i < tourSize; i++)
        {
        	if (children[0][i] == -1)
        	{
        		int index = i;
    			int value = parent1[index];
    			index = indexOf(parent2,value);
    			while(true)
    			{
        			value = parent1[index];
        			if (!contains(children[0],value))
        			{
        				children[0][i] = value;
        				break;
        			}
        			index = indexOf(parent2,value);
        		}
        	}
        	if (children[1][i] == -1)
        	{
        		int index = i;	
    			int value = parent2[index];
    			index = indexOf(parent1,value);
    			while(true)
        		{
        			value = parent2[index];
        			if (!contains(children[1],value))
        			{
        				children[1][i] = value;
        				break;
        			}
        			index = indexOf(parent1,value);
        		}
        	}
        }
        
        // Last city must always be the starting city
        children[0][tourSize] = 1;
        children[1][tourSize] = 1;
        
    	return children;
    }
    
    public int[][] cxCrossover(int parent1Idx, int parent2Idx)
    {
    	int[] parent1 = population.getRoute(parent1Idx);
    	int[] parent2 = population.getRoute(parent2Idx);
    	int[][] children = new int[2][tourSize+1];
    	Arrays.fill(children[0], -1);
        Arrays.fill(children[1], -1);
        List<Integer> cycle1 = new ArrayList<Integer>();
        List<Integer> values1 = new ArrayList<Integer>();
        List<Integer> cycle2 = new ArrayList<Integer>();
        List<Integer> values2 = new ArrayList<Integer>();
        int startPos = 1;
           
        // Create the first cycle.
    	cycle1.add(startPos);
    	values1.add(parent1[startPos]);
    	int value = parent2[startPos];
    	values1.add(value);
    	int index = indexOf(parent1,value);
    	while (index != startPos) 
    	{
    		cycle1.add(index);
    		value = parent2[index];
    		values1.add(value);
    		index = indexOf(parent1,value);
    	}
    	
    	// Create the second cycle.
    	startPos = 1;
    	cycle2.add(startPos);
    	values2.add(parent2[startPos]);
    	value = parent1[startPos];
    	values2.add(value);
    	index = indexOf(parent2,value);
    	while (index != startPos) 
    	{
    		cycle2.add(index);
    		value = parent1[index];
    		values2.add(value);
    		index = indexOf(parent2,value);
    	}
    	
    	// Populate the first child using the first cycle
    	for (int i = 0; i < cycle1.size(); i++)
    	{
    		children[0][cycle1.get(i)] = values1.get(i);
    	}
    	
    	// Populate the second child using the second child
    	for (int i = 0; i < cycle2.size(); i++)
    	{
    		children[1][cycle2.get(i)] = values2.get(i);
    	}
    	
    	// Fill in missing cities from opposing parent
    	for (int i = 1; i < tourSize; i++)
    	{
    		if (children[0][i] == -1)
    		{
    			children[0][i] = parent2[i];
    		}
    		if (children[1][i] == -1)
    		{
    			children[1][i] = parent1[i];
    		}
    	}
    	
    	// First and last city must always be the starting city
    	children[0][0] = 1;
        children[1][0] = 1;
    	children[0][tourSize] = 1;
        children[1][tourSize] = 1;
        
    	return children;
    }

    // Paper four crossover
    public int[][] cx2Crossover(int parent1Idx, int parent2Idx)
    {
    	int[] parent1 = population.getRoute(parent1Idx);
    	int[] parent2 = population.getRoute(parent2Idx);
        int[][] children = new int[2][tourSize+1];
        List<Integer> cycle1 = new ArrayList<Integer>();
        List<Integer> cycle2 = new ArrayList<Integer>();

        // This crossover creates children by following
        // "cycles" of cities and their corresponding indices
        // in both parents. Essentially, you take a starting city
        // from parent2. You find that cities index in parent 1, then
        // look at the city in the corresponding index in parent 2.
        // Once again find this cities index in parent 1, then take the
        // corresponding city in parent 2. Through a repetition of this
        // (do once, take for child 1, do twice, take for child 2, repeat)
        // the children are created.
        int startPos = 0;
        while (true) {
            // Create the cycles.
        	cycle1.add(startPos);
            int value = parent2[startPos];
            int index = indexOf(parent1, value);
            value = parent2[index];
            index = indexOf(parent1, value);
            cycle2.add(index);
            value = parent2[index];
            index = indexOf(parent1, value);
            int startIdx = -1;
            // Continue finding the cycle until it loops back on itself
            while (index != startPos && startIdx != index) {
            	startIdx = index;
                cycle1.add(index);
                value = parent2[index];
                index = indexOf(parent1, value);
                value = parent2[index];
                index = indexOf(parent1, value);
                cycle2.add(index);
                value = parent2[index];
                index = indexOf(parent1, value);
            }
            

            // Check if the cycles are complete. If not,
            // find the starting point for the next cycle
        	if (cycle1.size() == tourSize) {
                break;
            }
            else
            {
                Set<Integer> remainingIndices = new HashSet<>();
                for (int i = 0; i < tourSize; i++) {
                    if (!cycle1.contains(i)) {
                        remainingIndices.add(i);
                    }
                }
                startPos = remainingIndices.stream().min(Integer::compareTo).orElse(-1);
            }
        }
        
        // Generate the children using the cycles
        for (int i = 0; i < tourSize; i++) {
        	int tempIdx = cycle1.get(i);
            children[0][i] = parent2[tempIdx];
            tempIdx = cycle2.get(i);
            children[1][i] = parent2[tempIdx];
        }
        // Last city must always be the starting city
        children[0][tourSize] = 1;
        children[1][tourSize] = 1;

        return children;
    }

    // Paper five
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
				shortestDistanceTracker = (int)Integer.MAX_VALUE;
				
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
				shortestDistanceTracker = (int)Integer.MAX_VALUE;
				
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
    
    // Utility function, returns the index of a value
    // if it is in an array
    public static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
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
    	double bestFitness = (double)Double.MAX_VALUE;
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

	// get population size
	public int getPopulationSize() {
		return populationSize;
	}

	// get mutation rate
	public double getMutationRate() {
		return mutationRate;
	}
	
	// get crossover rate
	public double getCrossoverRate() {
		return crossoverRate;
	}

	// get tournament size
	public int getTournamentSize() {
		return tournamentSize;
	}

	// get tour size
	public int getTourSize() {
		return tourSize;
	}

	// get crossover function
	public String getCrossoverFcn() {
		return crossoverFcn;
	}

	// get selection function
	public String getSelectionFcn() {
		return selectionFcn;
	}

	// get city map
	public int[][] getCityMap() {
		return cityMap;
	}

	// set population size
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
		newPopulation();
	}

	// set mutation rate
	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	// set crossover rate
	public void setCrossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}

	// set tournament size
	public void setTournamentSize(int tournamentSize) {
		this.tournamentSize = tournamentSize;
	}

	// set tour size
	public void setTourSize(int tourSize) {
		this.tourSize = tourSize;
		newPopulation();
	}

	// set crossover function
	public void setCrossoverFcn(String crossoverFcn) {
		this.crossoverFcn = crossoverFcn;
	}

	// set selection function
	public void setSelectionFcn(String selectionFcn) {
		this.selectionFcn = selectionFcn;
	}

	// set city map
	public void setCityMap(int[][] cityMap) {
		this.cityMap = cityMap;
	}
}

