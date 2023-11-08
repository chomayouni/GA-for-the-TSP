package src;

public class Population {
    private Tour[] tours;

    public Population(int populationSize, int numCities) {
        tours = new Tour[populationSize];
        for (int i = 0; i < populationSize; i++) {
            tours[i] = new Tour(numCities);
        }
    }

    public void evaluateFitness(CityMap cityMap) {
        for (Tour tour : tours) {
            tour.calculateFitness(cityMap);
        }
    }

}