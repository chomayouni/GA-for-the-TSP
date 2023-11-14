package src;

public class SubMap extends Map {

    // variable to hold number of cities
    private int numberOfCities;
    // variable to hold city distances from txt file
    private int[][] map;
    // Variable to hold string of cities
    private String[] cityNames;
    // Variable for child map, which is a user created map from the whole DB

    public SubMap(int[] route) {
        this.numberOfCities = route.length;
        // System.out.println("Sub route length is " + this.numberOfCities);
        setCityNames(route);
        createCitiesMatrix(route);
    }


    public void createCitiesMatrix(int[] route) {
        // Map var that will be returned
        this.map = new int[this.numberOfCities][this.numberOfCities];
        //Print through the matrices
        for (int i = 0; i < this.numberOfCities; i++) {
            for (int j = 0; j < this.numberOfCities; j++) {
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

    public void setCityNames(int[] route) {
        // Initialize the array size
        this.cityNames = new String[this.numberOfCities];
        for (int i = 0; i < this.numberOfCities; i++) {
            this.cityNames[i] = (super.cityNames[route[i]-1]);
        }
    }
    
    @Override
    public int[][] getCityMatrix() {
        return this.map;
    }

    @Override
    public int getNumberOfCities() {
        return this.numberOfCities;
    }
}
