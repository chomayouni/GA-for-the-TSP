package src;

import java.util.Arrays;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.web.WebView;

public class TSPSolver {
    // GA object
    private GeneticAlgorithm GA;
    // Map object (WHole database, all cities)
    private Map map;

    // Objects for the output
    private WebView webViewConfig;
    private WebView webViewOutput;
    // Objects for the fitness graph
    private LineChart<String, Integer> lineChartFitness;
    private XYChart.Series<String, Integer> fitnessData;

    // Control for running the GA, should be in GA object, but thats A LOT of refactoring
    private int numGenerations;

    // Constructor for the solver. Will also create a map. 
    public TSPSolver(int numGenerations, int populationSize, double mutationRate, double crossoverRate, int tournamentSize, String crossoverFcn, String selectionFcn) {
        this.numGenerations = numGenerations;
        // Create new Map object to handle database stuff. 
        map = new Map();
        // Created before any user cites are set, so just go off of the base map (all the database). Will be updated imediately due
        //      to TSPSoverController setting other defaults, and control stack following through
        GA = new GeneticAlgorithm(crossoverFcn, selectionFcn, populationSize, mutationRate,
                crossoverRate, tournamentSize,map.getNumberOfCities(),map.getCityMatrix());

    }


    private void showConfig() {        
        String content = "<table>"
        + "<tr><th>Crossover Function</th><td>" + GA.getCrossoverFcn() + "</td></tr>"
        + "<tr><th>Selection Function</th><td>" + GA.getSelectionFcn() + "</td></tr>"
        + "<tr><th>Number of Generations</th><td>" + numGenerations + "</td></tr>"
        + "<tr><th>Tour Size</th><td>" + GA.getTourSize() + "</td></tr>"
        + "<tr><th>Population Size</th><td>" + GA.getPopulationSize() + "</td></tr>"
        + "<tr><th>Mutation Rate</th><td>" + GA.getMutationRate() + "</td></tr>"
        + "<tr><th>Crossover Rate</th><td>" + GA.getCrossoverRate() + "</td></tr>"
        + "<tr><th>Tournament Size</th><td>" + GA.getTournamentSize() + "</td></tr>"
        + "<tr><th>User Route</th><td>" + map.toStringUser() + "</td></tr>"
        + "</table>";
    // webViewConfig.applyCss();
    webViewConfig.getEngine().loadContent(content);
    }

    private void showOutput(String content) {
        // webViewOutput.applyCss();
        webViewOutput.getEngine().loadContent(content);
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

    private void updateChart(int i, double fitness) {
        fitnessData.getData().add(new XYChart.Data(Integer.toString(i), fitness));
        lineChartFitness.getData().remove(fitnessData);
        lineChartFitness.getData().add(fitnessData);
    }

    private void clearChart() {
        fitnessData.getData().clear();
        lineChartFitness.getData().removeAll(fitnessData);
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
    
        // Clear chart for new runs, create new content for the output
        clearChart();
        StringBuilder content = new StringBuilder("<table><tr><th>Iteration</th><th>Route</th><th>Distance</th></tr>");
    
        // Add initial solution to the table
        content.append("<tr><td>Initial</td><td>")
               .append(Arrays.toString(bestTour.getRoute()))
               .append("</td><td>")
               .append(bestTour.getFitness())
               .append("</td></tr>");
               updateChart(0, bestTour.getFitness());
    
        for (int i = 0; i < numGenerations; i++) {
            GA.selection();
            GA.crossover();
            GA.mutation();
            GA.fitness();
    
            if ((i%(numGenerations/25) == 0) && (i != 0)) {        
                bestTour = GA.getFittest();
    
                // Add current record to the table
                content.append("<tr><td>").append(i)
                       .append("</td><td>").append(Arrays.toString(bestTour.getRoute()))
                       .append("</td><td>").append(bestTour.getFitness())
                       .append("</td></tr>");
    
                updateChart(i, bestTour.getFitness());
            }
        }
    
        // Add final solution to the table
        bestTour = GA.getFittest();
        content.append("<tr><td>Final</td><td>")
               .append(Arrays.toString(bestTour.getRoute()))
               .append("</td><td>")
               .append(bestTour.getFitness())
               .append("</td></tr>");
        // Finish the table
        content.append("</table>");
        // Show the table. 
        showOutput(content.toString());
    
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
        showConfig();
    } 

    public void setCrossoverFcn(String crossoverFcn) {
        // this.crossoverFcn = crossoverFcn;
        GA.setCrossoverFcn(crossoverFcn);
        showConfig();
        System.out.println("New Crossover Fcn is " + GA.getCrossoverFcn());
        // updateTSP();
    }

    public void setSelectionFcn(String selectionFcn) {
        // this.selectionFcn = selectionFcn;
        GA.setSelectionFcn(selectionFcn);
        showConfig();
        System.out.println("New Selection Fcn is " + GA.getSelectionFcn());
        // updateTSP();
    }

    public void setPopulationSize(int populationSize) {
        // this.populationSize = populationSize;
        GA.setPopulationSize(populationSize);
        showConfig();
        System.out.println("New Population Size is " + GA.getPopulationSize());
        // updateTSP();
    }

    public void setMutationRate(double mutationRate) {
        // this.mutationRate = mutationRate;
        GA.setMutationRate(mutationRate);
        showConfig();
        System.out.println("New Mutation Rate is " + GA.getMutationRate());
        // updateTSP();
    }

    public void setCrossoverRate(double crossoverRate) {
        // this.crossoverRate = crossoverRate;
        GA.setCrossoverRate(crossoverRate);
        showConfig();
        System.out.println("New Crossover Rate is " + GA.getCrossoverRate());
        // updateTSP();
    }

    public void setTournamentSize(int tournamentSize) {
        // this.tournamentSize = tournamentSize;
        GA.setTournamentSize(tournamentSize);
        showConfig();
        System.out.println("New Tournament Size is " + GA.getTournamentSize());
        // updateTSP();
    }

    public void setNumGenerations(int numGenerations) {
        this.numGenerations = numGenerations;
        showConfig();
        System.out.println("New Number of Generations is " + this.numGenerations);
        // updateTSP();
    }


    public void setOutput(WebView webViewConfig, WebView webViewOutput, LineChart<String, Integer> lineChartFitness) {
        // txtAreaOutput.setText("New TSP Solver Initialized");
        this.webViewConfig = webViewConfig;
        webViewConfig.getEngine().setUserStyleSheetLocation(getClass().getResource("../css/GAConfigStyle.css").toString());

        this.webViewOutput = webViewOutput;
        webViewOutput.getEngine().setUserStyleSheetLocation(getClass().getResource("../css/GASolverStyle.css").toString());


        this.lineChartFitness = lineChartFitness;
        fitnessData = new XYChart.Series<>();
        lineChartFitness.getData().add(fitnessData);
        updateChart(5, 5);
        clearChart();
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

}
