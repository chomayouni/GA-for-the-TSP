package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.GeneticAlgorithm;
import src.Population;

class GeneticAlgorithmTest {

	GeneticAlgorithm GA;
	
	private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int tournamentSize;
    private int tourSize;
    private int cityMap[][];
	
	@BeforeEach
	void setUp() throws Exception
	{
		populationSize = 10;
		mutationRate = 0.05;
		crossoverRate = 0.80;
		tournamentSize = 4;
		tourSize = 10;
		cityMap = new int[tourSize][tourSize];
		for (int i = 0; i < tourSize; i ++)
		{
			for (int j = 0; j < tourSize; j ++)
			{
				cityMap[i][j] = 1;
			}
		}
		
		GA = new GeneticAlgorithm(populationSize, mutationRate, crossoverRate,
				tournamentSize,tourSize,cityMap);
	}

	@AfterEach
	void tearDown() throws Exception
	{
		populationSize = 0;
		mutationRate = 0;
		crossoverRate = 0;
		tournamentSize = 0;
		tourSize = 0;
		cityMap = null;
		GA = null;
	}

	// NOTE
	// I was lazy and tested by making attributes public,
	// will have to make variable public to retest
	
//	@Test
//	void fitnessTest()
//	{
//		GA.fitness();
//		for (int i = 0; i < populationSize; i++)
//		{
//			System.out.println(GA.population.getFitness(i));
//			assertTrue("Fitness updated correctly", GA.population.getFitness(i) == 10);
//		}
//	}
	
//	@Test
//	void selectionTest()
//	{
//		GA.fitness();
//		GA.selection();
//		assertFalse("Parent1 array is null", GA.parent1Arr == null);
//		assertFalse("Parent2 array is null", GA.parent2Arr == null);
//	}
	
//	@Test
//	void crossoverTest()
//	{
//		GA.fitness();
//		GA.selection();
//		Population prePop = GA.population;
//		GA.crossover();
//		assertFalse("Population did not update", prePop.equals(GA.population));
//	}
	
	// This test does not work, prePop does not make a copy of GA.population,
	// it seems they become the same pointer (so I cannot test that they are different).
	// I validated the mutation method with print statements
//	@Test
//	void mutationTest()
//	{
//		mutationRate = 1;
//		GA = new GeneticAlgorithm(populationSize, mutationRate, crossoverRate,
//				tournamentSize,tourSize,cityMap);
//		Population prePop = new Population(populationSize,tourSize, false);
//		prePop = GA.population;
//		GA.fitness();
//		GA.mutation();
//		for (int i = 0; i < populationSize; i++)
//		{
//			assertFalse("Population did not mutate "+i, prePop.getRoute(i) == GA.population.getRoute(i));
//		}
//	}

}
