package src;

import java.util.ArrayList;

public class Route {
    private ArrayList<City> cities;
    private double fitness = 0;
    private int distance = 0;

    public Route(int chromosomeLength) {
        this.cities = new ArrayList<City>(chromosomeLength);
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    public double getFitness() {
        if (fitness == 0) {
            fitness = 1 / (double)getDistance();
        }
        return fitness;
    }

    public int getDistance() {
        if (distance == 0) {
            int routeDistance = 0;
            // Calculate the total distance of the route
            for (int cityIndex = 0; cityIndex < getCities().size(); cityIndex++) {
                City startCity = getCities().get(cityIndex);
                City destinationCity;
                
                // Check if we're not on the route's last city, if so, set our route's next city
                if (cityIndex + 1 < getCities().size()) {
                    destinationCity = getCities().get(cityIndex + 1);
                } else {
                    // If we are on the last city, set our route's next city to the starting city
                    destinationCity = getCities().get(0);
                }
                // Get the distance between the two cities
                routeDistance += startCity.distanceBetweenCities(destinationCity);
            }
            distance = routeDistance;
        }
        return distance;
    }
    @Override
    public String toString() {
        String route = "Route: ";
        for (City city : getCities()) {
            route += city.toString() + " -> ";
        }
        return route + "Start";
    }

    public void generateIndividual() {
    }

    public int getRouteSize() {
        return 0;
    }
}

