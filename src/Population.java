package src;

import java.util.ArrayList;
import java.util.List;

public class Population {
    private List<Route> routes = new ArrayList<>(PopulationSize);
    
    public Population(int populationSize, boolean initialise, List<City> cities) {
        for (int i = 0; i < populationSize; i++) {
            routes.add(null);
        }
        if (initialise) {
            for (int i = 0; i < populationSize(); i++) {
                Route newRoute = new Route(new ArrayList<>(cities));
                newRoute.generateIndividual();
                saveRoute(i, newRoute);
            }
        }
    }
    
    public void saveRoute(int index, Route route) {
        routes.set(index, route);
    }
    
    public Route getRoute(int index) {
        return routes.get(index);
    }
    
    public Route getFittest() {
        Route fittest = routes.get(0);
        for (int i = 1; i < populationSize(); i++) {
            if (fittest.getFitness() <= getRoute(i).getFitness()) {
                fittest = getRoute(i);
            }
        }
        return fittest;
    }
    
    public int populationSize() {
        return routes.size();
    }

    public int size() {
        return 0;
    }
}
