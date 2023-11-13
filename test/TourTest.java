package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.Tour;

class TourTest {

	Tour tour;
	int tourSize;
	int[] route;
	double fitness;
	
	@BeforeEach
	void setUp() throws Exception
	{
		tourSize = 10;
		fitness = 200;
		// Non-random tour
		route = new int[tourSize+1];
		for (int i = 0; i<=tourSize; i++)
		{
			route[i] = i+1;
		}
		tour = new Tour(tourSize, true);
	}

	@AfterEach
	void tearDown() throws Exception
	{
		tour = null;
		tourSize = 0;
		route = null;
		fitness = 0;
	}

	@Test
	void tourTest() {
		// Looping for thorough testing
		for (int i = 0; i < 20; i ++) 
		{
			tour = new Tour(tourSize, true);
			assertTrue("Tour is correct size", tour.getRoute().length == tourSize+1);
			assertTrue("Start city correct", tour.getRoute()[0] == 1);
			assertTrue("End city correct", tour.getRoute()[tourSize] == 1);
			assertFalse("Route is not random", tour.getRoute() == route);
			// Debug
			// System.out.println("");
		}
	}
	
	@Test
	void getSetRouteTest()
	{
		tour.setRoute(route);
		assertFalse("Route is null", tour.getRoute() == null);
		for (int i = 0; i<=tourSize; i++)
		{
			assertTrue("City is correct",tour.getRoute()[i] == route[i]);
		}
	}
	
	
	@Test
	void getSetFitnessTest()
	{
		tour.setFitness(fitness);
		assertTrue("Fitness is correct", tour.getFitness() == fitness);
	}

}
