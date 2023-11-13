package src;

import java.util.Arrays;
import java.util.Random;

public class Tour {
    private int route[];
    private double fitness;
    
    public Tour(int tourSize, boolean initialize)
    {
    	// Initialize route array with all cities
    	route = new int[tourSize+1];
    	
    	if (initialize == true)
    	{
	    	for (int i = 0; i <= tourSize; i++)
	    		route[i] = i+1;
	    	
	    	// Debug
	    	//System.out.println(Arrays.toString(route));
	    	
			// Randomly shuffle cities for randomly initialized solution
	        Random randomGenerator = new Random();
	        int randomIndex; // the randomly selected index each time through the loop
	        int randomValue; // the value at route[randomIndex] each time through the loop
	
	        // randomize order of values
	        for(int i = 0; i < route.length; ++i)
	        {
	             // select a random index
	             randomIndex = randomGenerator.nextInt(route.length);
	
	             // swap values
	             randomValue = route[randomIndex];
	             route[randomIndex] = route[i];
	             route[i] = randomValue;
	        }
	        
	        // Debug
	    	//System.out.println(Arrays.toString(route));
	        
	        // Correct solution such that start and end city is city 1
	        boolean firstDone = false;
	        boolean lastDone = false;
	        
	        for (int i = 0; i <= tourSize; i++)
	        {
	        	if (route[i] == 1 && !(i == tourSize))
	        	{
	        		int temp = route[0];
	        		route[0] = 1;
	        		route[i] = temp;
	        		firstDone = true;
	        	}
	        	if (route[i] == tourSize+1)
	        	{
	        		if (i == tourSize)
	        		{
	        			route[tourSize] = 1;
	        		}
	        		else
	        		{
		        		int temp = route[tourSize];
		        		if (temp == 1)
		        		{
		        			int temp2 = route[0];
			        		route[i] = temp2;
			        		route[0] = 1;
			        		firstDone = true;
			        		lastDone = true;
		        		}
		        		else
		        		{
			        		route[tourSize] = 1;
			        		route[i] = temp;
			        		lastDone = true;
		        		}
	        		}
	        	}
	        	if (firstDone && lastDone)
	        	{
	        		break;
	        	}
	        }
	        
	        // Debug
			//System.out.println(Arrays.toString(route));
    	}
    }
    
    public int[] getRoute()
    {
    	return route;
    }
    
    public void setRoute(int[] route)
    {
    	this.route = route;
    }
    
    public double getFitness()
    {
    	return this.fitness;
    }
    
    public void setFitness(double fitness)
    {
    	this.fitness = fitness;
    }
    
}
