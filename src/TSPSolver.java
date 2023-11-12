package src;
public class TSPSolver {
    public static void main(String[] args) {
        // The Driver for the other classes
        // Output the best solution found
        // I think that this will contain the GUI

        // Create and the values of our city for testing
        City city = new City(60, 200);
        // Add more cities
        
        // Initialize population
        Population tesPopulation = new Population(50, true, cities);
        System.out.println("Initial distance: " + tesPopulation.getFittest().getDistance());

        // Evolve population
        tesPopulation = GA.evolvePopulation(tesPopulation);
        for (int i = 0; i < 100; i++) {
            tesPopulation = GA.evolvePopulation(tesPopulation);
        }

        System.out.println("Finished");
        System.out.println("Final distance: " + tesPopulation.getFittest().getDistance());
        System.out.println("Solution:");
        System.out.println(tesPopulation.getFittest());
    }
}
