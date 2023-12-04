package src;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.util.Pair;

public class TSPSolver {
    // GA object
    private GeneticAlgorithm GA;
    // Map object (WHole database, all cities)
    private Map map;

    // Data object for fitness data
    private ArrayList<String> fitnessXData;
    private ArrayList<Double> fitnessYData;
    // String for the config output and Stringbuilder sovlerOutput;
    private StringBuilder TSPSolverTableData;

    // Control for running the GA, should be in GA object, but thats A LOT of refactoring
    private int numGenerations;

    // Constructor for the solver. Will also create a map. 
    public TSPSolver(int numGenerations, int populationSize, double mutationRate, double crossoverRate, int tournamentSize, String crossoverFcn, String selectionFcn, String dataset) {
        // set initial num generations
        this.numGenerations = numGenerations;
        // Create new Map object to handle database stuff. 
        map = new Map(dataset);
        map.reload();
        // Created before any user cites are set, so just go off of the base map (all the database). Will be updated imediately due
        //      to TSPSoverController setting other defaults, and control stack following through
        GA = new GeneticAlgorithm(crossoverFcn, selectionFcn, populationSize, mutationRate,
                crossoverRate, tournamentSize,map.getNumberOfCities(),map.getCityMatrix());
        
        // Create the data objects for the fitness graph
        fitnessXData = new ArrayList<String>();
        fitnessYData = new ArrayList<Double>();
    }

    // Add a city to the data base
    public Boolean addCity(String city) {
        // Check to make sure city is not already in the db
        if (map.addCity(city)) {
            map.reload();
            // txtAreaOutput.setText(city + " added to database");
            return true;
            
        } else {
            // txtAreaOutput.setText(city + " already in database");
            return false;
        }

    }

    public void run() {
        // Must get fitness before GA operation loop
        // updateTSP();
        GA.newPopulation();
        GA.fitness();
    
        // Print best initial solution
        Tour bestTour = GA.getFittest();
        System.out.println("Initial route : " + Arrays.toString(bestTour.getRoute()));
        System.out.println("Initial distance : " + bestTour.getFitness());
        System.out.println("");
        
        // Clear the fitness data
        fitnessXData.clear();
        fitnessYData.clear();

        TSPSolverTableData = new StringBuilder("<table><tr><th>Iteration</th><th>Route</th><th>Distance</th></tr>");
    
        // Add initial solution to the table
        TSPSolverTableData.append("<tr><td>Initial</td><td>")
                .append(Arrays.toString(bestTour.getRoute()))
                .append("</td><td>")
                .append(bestTour.getFitness())
                .append("</td></tr>");

        // Add initial solution to the chart
        fitnessXData.add("0");
        fitnessYData.add(bestTour.getFitness());
    
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numGenerations; i++) {
            GA.selection();
            GA.crossover();
            GA.mutation();
            GA.fitness();

            System.out.println("numGeneration is " + i);
    
            if ((i%(numGenerations/25) == 0) && (i != 0)) {        
                bestTour = GA.getFittest();
    
                // Add current record to the table
                TSPSolverTableData.append("<tr><td>").append(i)
                       .append("</td><td>").append(Arrays.toString(bestTour.getRoute()))
                       .append("</td><td>").append(bestTour.getFitness())
                       .append("</td></tr>");
    
                fitnessXData.add(Integer.toString(i));
                fitnessYData.add(bestTour.getFitness());
            }
        }
        
        long endTime = System.currentTimeMillis();
        double elapsedTime = (endTime - startTime);
        if (elapsedTime > 1000)
        {
        	System.out.println("Elapsed time : " + elapsedTime/1000 + " s");
        }
        else
        {
        	System.out.println("Elapsed time : " + elapsedTime + " ms");
        }
        // Add final solution to the table
        bestTour = GA.getFittest();
        TSPSolverTableData.append("<tr><td>Final</td><td>")
               .append(Arrays.toString(bestTour.getRoute()))
               .append("</td><td>")
               .append(bestTour.getFitness())
               .append("</td></tr>");
        // Finish the table
        TSPSolverTableData.append("</table>");

        // Add final solution to the chart
        fitnessXData.add(Integer.toString(numGenerations));
        fitnessYData.add(bestTour.getFitness());
    
        // Print results to the console
        System.out.println("Finished");
        System.out.println("Final distance: " + bestTour.getFitness());
        System.out.println("Final Solution:");
        System.out.println(Arrays.toString(bestTour.getRoute()));
    }

    public void setUserRoute(int[] userRoute) {
        map.setUserRoute(userRoute);
        // System.out.println("New User Route is " + this.userRoute[0]);
        // User route and actual city data are not part of the TSP, they are part of the map object. 
        //      so those must be updated first via the update TSP then we can print the setup (which prints) 
        //      the routes
        GA.setTourSize(map.getUserNumberOfCities());
        GA.setCityMap(map.getUserCityMatrix());
    } 

    public void setCrossoverFcn(String crossoverFcn) {
        GA.setCrossoverFcn(crossoverFcn);
        System.out.println("New Crossover Fcn is " + GA.getCrossoverFcn());
    }

    public void setSelectionFcn(String selectionFcn) {
        GA.setSelectionFcn(selectionFcn);
        System.out.println("New Selection Fcn is " + GA.getSelectionFcn());
    }

    public void setPopulationSize(int populationSize) {
        GA.setPopulationSize(populationSize);
        System.out.println("New Population Size is " + GA.getPopulationSize());
    }

    public void setMutationRate(double mutationRate) {
        GA.setMutationRate(mutationRate);
        System.out.println("New Mutation Rate is " + GA.getMutationRate());
    }

    public void setCrossoverRate(double crossoverRate) {
        GA.setCrossoverRate(crossoverRate);
        System.out.println("New Crossover Rate is " + GA.getCrossoverRate());
    }

    public void setTournamentSize(int tournamentSize) {
        GA.setTournamentSize(tournamentSize);
        System.out.println("New Tournament Size is " + GA.getTournamentSize());
    }

    public void setNumGenerations(int numGenerations) {
        this.numGenerations = numGenerations;
        System.out.println("New Number of Generations is " + this.numGenerations);
    }

    public void setDataset(String dataset) {
        map.setDataset(dataset);
        GA.setTourSize(map.getNumberOfCities());
        GA.setCityMap(map.getCityMatrix());
    }

    public Pair<ArrayList<String>, ArrayList<Double>> getFitnessData() {
        return new Pair<ArrayList<String>, ArrayList<Double>>(fitnessXData, fitnessYData);
    }

    public int getNumGenerations() {
        return numGenerations;
    }

    public int getPopulationSize() {
        return GA.getPopulationSize();
    }

    public double getMutationRate() {
        return GA.getMutationRate();
    }

    public double getCrossoverRate() {
        return GA.getCrossoverRate();
    }

    public int getTournamentSize() {
        return GA.getTournamentSize();
    }

    public int getTourSize() {
        return GA.getTourSize();
    }

    // return ALL city names in dataset
    public String[] getCityNames() {
        return map.getCityNames();
    }

    // return the indicies from the given string of route names
    public int[] getRouteIndices(String[] routeNames) {
        return map.getRouteIndices(routeNames);
    }

    // Return the current crossover function
    public String getCrossoverFcn() {
        return GA.getCrossoverFcn();
    }

    // Return the current selection function
    public String getSelectionFcn() {
        return GA.getSelectionFcn();
    }

    // Return the current dataset  
    public String getDataset() {
        return map.getDataset();
    }

    // Set output for the config table
    public String getConfigTable() {
        if (map.getDataset().equals("CUSTOM")) {
            String configOutput = "<table>"
            + "<tr><th>Crossover Function</th><td>" + GA.getCrossoverFcn() + "</td></tr>"
            + "<tr><th>Selection Function</th><td>" + GA.getSelectionFcn() + "</td></tr>"
            + "<tr><th>Number of Generations</th><td>" + numGenerations + "</td></tr>"
            + "<tr><th>Tour Size</th><td>" + GA.getTourSize() + "</td></tr>"
            + "<tr><th>Population Size</th><td>" + GA.getPopulationSize() + "</td></tr>"
            + "<tr><th>Mutation Rate</th><td>" + GA.getMutationRate() + "</td></tr>"
            + "<tr><th>Crossover Rate</th><td>" + GA.getCrossoverRate() + "</td></tr>"
            + "<tr><th>Tournament Size</th><td>" + GA.getTournamentSize() + "</td></tr>"
            + "<tr><th>Custom Route</th><td>" + map.toStringUser() + "</td></tr>"
            + "</table>";
            return configOutput;
        } 
        else {
            String configOutput = "<table>"
            + "<tr><th>Crossover Function</th><td>" + GA.getCrossoverFcn() + "</td></tr>"
            + "<tr><th>Selection Function</th><td>" + GA.getSelectionFcn() + "</td></tr>"
            + "<tr><th>Number of Generations</th><td>" + numGenerations + "</td></tr>"
            + "<tr><th>Tour Size</th><td>" + GA.getTourSize() + "</td></tr>"
            + "<tr><th>Population Size</th><td>" + GA.getPopulationSize() + "</td></tr>"
            + "<tr><th>Mutation Rate</th><td>" + GA.getMutationRate() + "</td></tr>"
            + "<tr><th>Crossover Rate</th><td>" + GA.getCrossoverRate() + "</td></tr>"
            + "<tr><th>Tournament Size</th><td>" + GA.getTournamentSize() + "</td></tr>"
            + "<tr><th>Dataset</th><td>" + map.getDataset() + "</td></tr>"
            + "</table>";
        return configOutput;
        }
    }

    public String getTSPTableData() {
        return TSPSolverTableData.toString();
    }
}
