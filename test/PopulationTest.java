package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.Population;
import src.Tour;

class PopulationTest {

	Population population;
	int populationSize;
	int tourSize;
	int[] route;
	double fitness;
	Tour tour;
	
	@BeforeEach
	void setUp() throws Exception {
		populationSize = 2;
		tourSize = 10;
		population = new Population(populationSize,tourSize,true);
		fitness = 200;
		route = new int[tourSize+1];
		for (int i = 0; i<=tourSize; i++)
		{
			route[i] = i+1;
		}
	}

	@AfterEach
	void tearDown() throws Exception
	{
		populationSize = 0;
		tourSize = 0;
		population = null;
		route = null;
		fitness = 0;
		tour = null;
	}

	@Test
	void populationTest()
	{
		population = new Population(populationSize,tourSize,true);
		for (int i = 0; i < populationSize; i++)
		{
			assertFalse("Route not created", population.getRoute(i) == null);
			assertTrue("Correct tourSize", population.getRoute(i).length == tourSize+1);
		}
	}
	
	@Test
	void getTourTest()
	{
		// Also tests get/set route and get/set fitness
		population.setRoute(1,route);
		assertTrue("Route set succesfully", population.getRoute(1) == route);	
		population.setFitness(1, fitness);
		assertTrue("Fitness set succesfully", population.getFitness(1) == fitness);
		Tour tour = population.getTour(1);
		assertTrue("Tour get succesfull (route)", tour.getRoute() == route);
		assertTrue("Tour get succesfull (fitness)", tour.getFitness() == fitness);
	}

}
