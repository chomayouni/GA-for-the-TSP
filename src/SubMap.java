package src;

public class SubMap extends Map {

    // variable to hold number of cities
    private int numberOfCities;
    // variable to hold city distances from txt file
    private int[][] map;
    // Variable to hold string of cities
    private String[] cityNames;
    // Variable for child map, which is a user created map from the whole DB

    // Constructor for sub map
    public SubMap(int[] route) {
        numberOfCities = route.length;
        // System.out.println("Sub route length is " + this.numberOfCities);
        setCityNames(route);
        createCitiesMatrix(route);
    }


    // Method for creating cities matrix for the submap, takes in route. Refernces parent map
    private void createCitiesMatrix(int[] route) {
        // Map var that will be returned
        this.map = new int[numberOfCities][numberOfCities];
        //Print through the matrices
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                // very dirty way to ignore the new lines in the txt file (each row)
                try {
                    this.map[i][j] = super.map[route[i]-1][route[j]];
                }
                catch(NumberFormatException exception) {
                    // exception.printStackTrace();
                }
            }
        }
    }

    // Method for setting city names via input route, references the parent map
    private void setCityNames(int[] route) {
        // Initialize the array size
        this.cityNames = new String[numberOfCities];
        for (int i = 0; i < numberOfCities; i++) {
            this.cityNames[i] = (super.cityNames[route[i]-1]);
        }
    }
    
    @Override
    public int[][] getCityMatrix() {
        return map;
    }

    @Override
    public int getNumberOfCities() {
        return numberOfCities;
    }
}
