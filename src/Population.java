package src;

import java.util.ArrayList;
import java.util.List;

public class Population {
	private int populationSize;
    private List<Tour> tours = new ArrayList<>(populationSize);
    
    public Population(int populationSize, int tourSize, boolean initialize)
    {
        this.populationSize = populationSize;
    	for (int i = 0; i < populationSize; i++)
    	{
            Tour tour = new Tour(tourSize, initialize);
            tours.add(tour);
        }
    }
    
    public int[] getRoute(int index)
    {
        return tours.get(index).getRoute();
    }
    
    public void setRoute(int index, int[] route)
    {
    	tours.get(index).setRoute(route);
    }
    
    public void setFitness(int index, double fitness)
    {
    	tours.get(index).setFitness(fitness);
    }
    
    public double getFitness(int index)
    {
    	return tours.get(index).getFitness();
    }
    
    public Tour getTour(int index)
    {
    	return tours.get(index);
    }

}
