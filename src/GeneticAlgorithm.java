package src;

import java.util.ArrayList;
import java.util.Collections;

public class GeneticAlgorithm {
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    private int tournamentSize;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount, int tournamentSize) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
        this.tournamentSize = tournamentSize;
    }

    public Population initPopulation(int chromosomeLength) {
        // Initialize a new population with a given size
        Population population = new Population(this.populationSize, false, null);
    
        // Create each route in the population
        for (int routeIndex = 0; routeIndex < this.populationSize; routeIndex++) {
            // Create a random route
            Route newRoute = new Route(chromosomeLength);
            newRoute.generateIndividual();
            
            // Add the route to the population
            population.saveRoute(routeIndex, newRoute);
        }
    
        return population;
    }

    // Calculate fitness for each individual
    public double calculateFitness(Route route) {
        double totalDistance = 0.0;
    
        // Loop over cities in the route
        for (int cityIndex = 0; cityIndex < route.getRouteSize(); cityIndex++) {
            // Get city and the next city
            City startCity = route.getCity(cityIndex);
            City endCity = (cityIndex + 1 < route.getRouteSize()) ? route.getCity(cityIndex + 1) : route.getCity(0);
    
            // Calculate distance between them and add to total
            totalDistance += startCity.distanceTo(endCity);
        }
    
        double fitness = 1 / totalDistance;
        route.setFitness(fitness);
    
        return fitness;
    }
    

    public Population evolve(Population population) {
        Population newPopulation = new Population(population.s());
    
        // Loop over the current population by fitness
        for (int i = 0; i < population.size(); i++) {
            // Select parents
            Route parent1 = selectParent(population);
            Route parent2 = selectParent(population);
    
            // Crossover parents
            Route child = crossover(parent1, parent2);
    
            // Add child to new population
            newPopulation.saveRoute(i, child);
        }
    
        // Apply mutation to the new population
        for (int i = 0; i < newPopulation.size(); i++) {
            mutatePopulation(newPopulation, i);(newPopulation.getRoute(i));
        }
    
        return newPopulation;
    }
    

    public Route tournamentSelection(Population population, int tournamentSize) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * population.size());
            tournament.saveRoute(i, population.getRoute(randomId));
        }
        // Get the fittest route
        Route fittest = tournament.getFittest();
        return fittest;
    }

    // Crossover population
    public Route crossover(Route parent1, Route parent2) {
        // Create new child tour
        Route child = new Route();
    
        // Get start and end sub tour positions for parent1's route
        int startPos = (int) (Math.random() * parent1.routeSize());
        int endPos = (int) (Math.random() * parent1.routeSize());
    
        // Loop and add the sub tour from parent1 to our child
        for (int i = 0; i < child.routeSize(); i++) {
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setCity(i, parent1.getCity(i));
            } else if (startPos > endPos) {
                if (!(i < startPos && i > endPos)) {
                    child.setCity(i, parent1.getCity(i));
                }
            }
        }
    
        // Loop through parent2's city tour
        for (int i = 0; i < parent2.routeSize(); i++) {
            // If child doesn't have the city add it
            if (!child.containsCity(parent2.getCity(i))) {
                // Loop to find a spare position in the child's tour
                for (int ii = 0; ii < child.routeSize(); ii++) {
                    // Spare position found, add city
                    if (child.getCity(ii) == null) {
                        child.setCity(ii, parent2.getCity(i));
                        break;
                    }
                }
            }
        }
        return child;
    }

    // Mutate population
    public void mutatePopulation(Population population, double mutationRate) {
        for(int i = 1; i < population.size(); i++){ // Start from 1 to preserve the best route
            if(Math.random() < mutationRate){
                mutateRoute(population.getRoute(i));
            }
        }
    }
    
    private void mutateRoute(Route route) {
        // Apply swap mutation
        for(int routePos1 = 0; routePos1 < route.routeSize(); routePos1++){
            if(Math.random() < mutationRate){
                int routePos2 = (int) (route.routeSize() * Math.random());
    
                // Get the cities at target position in route
                City city1 = route.getCity(routePos1);
                City city2 = route.getCity(routePos2);
    
                // Swap them
                route.setCity(routePos2, city1);
                route.setCity(routePos1, city2);
            }
        }
    }
    

    // Get fittest individual
    public Route getFittest(Population population) {
        Route fittest = population.getRoute(0);
        for (int i = 1; i < population.size(); i++) {
            if (fittest.getFitness() <= population.getRoute(i).getFitness()) {
                fittest = population.getRoute(i);
            }
        }
        return fittest;
    }
    
}

