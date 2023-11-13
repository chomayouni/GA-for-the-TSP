package src;
import java.io.*;
import java.util.Scanner;  
import java.util.List;
import java.util.Arrays;


public class cities {
    // file object ot point to the cities DB
    private File citiesFile;
    // scanner object which is used to parse text in the associiated file
    private Scanner citiesScanner;
    // variable to hold number of cities
    private int numberOfCities;
    // variable to hold city distances from txt file
    private int[][] map;
    // Variable to hold string of cities
    private String[] cityNames;
    
    // overloaded constructors, lets us pass in new path if we want, as of now, would have to be COMPLETE path for your system
    public cities(String path) {
        this.citiesFile = new File(path);
        initilizeCitiesScanner();
        setNumberOfCities();
        setCityNames();
        createCitiesMatrix();
    }

    public cities() {
        this.citiesFile = new File("data/cities.txt");
        initilizeCitiesScanner();
        setNumberOfCities();
        setCityNames();
        createCitiesMatrix();
    }

    // This method is called repeatedly to reset the scanner, for when we go into the txt file multiple times, more or less
    //      for debugging purposes
    public void initilizeCitiesScanner() {
        //Scanner object wants to be in a try catch to get rid of warning if there is an invalid file path
        citiesScanner = null;
        try {
            citiesScanner = new Scanner(this.citiesFile);
            citiesScanner.useDelimiter(",");
        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void printCities() {
        //Reset scanner each time we go back into it
        initilizeCitiesScanner();
        //Skip the number of cities, and name of cities
        citiesScanner.nextLine();
        citiesScanner.nextLine();
        //Print the number of cities grabbed on initialization
        System.out.print("This city database contains " + this.numberOfCities + " cities.");
        for (int i = 0; i < this.numberOfCities; i++) {
            System.out.println(this.citiesScanner.next());
        }
    }

    public void printCitiesMatrixFromTxt() {
        //I'm just usually reseting the scanner each time we need to go back into the txt file
        initilizeCitiesScanner();

        //skip the number of cities
        citiesScanner.nextLine();

        //Print through the matrices
        for (int i = 0; i < this.numberOfCities; i++) {
            for (int j = 0; j < this.numberOfCities; j++) {
                System.out.print(this.citiesScanner.next() + "\t");
            }
            System.out.println();
        }
    }


    public void createCitiesMatrix() {
        //I'm just usually reseting the scanner each time we need to go back into the txt file
        initilizeCitiesScanner();
        //Skip the number of cities, and name of cities
        citiesScanner.nextLine();
        citiesScanner.nextLine();
        // Map var that will be returned
        this.map = new int[this.numberOfCities][this.numberOfCities];
        //Print through the matrices
        for (int i = 0; i < this.numberOfCities; i++) {
            for (int j = 0; j < this.numberOfCities; j++) {
                // very dirty way to ignore the new lines in the txt file (each row)
                try {
                    this.map[i][j] = Integer.parseInt(this.citiesScanner.next());
                }
                catch(NumberFormatException exception) {
                    // exception.printStackTrace();
                }
            }
        }
    }

    public int[][] getCityMatrix() {
        return this.map;
    }

    public int getNumberOfCities() {
        return this.numberOfCities;
    }

    public void setNumberOfCities() {
        // Reset scanner object
        initilizeCitiesScanner();
        // Get the number of cities from the first piece of data in txt file
        this.numberOfCities = Integer.parseInt(this.citiesScanner.next());
    }

    public void setCityNames() {
        // Reset scanner object
        initilizeCitiesScanner();
        // Initialize the array size
        this.cityNames = new String[this.numberOfCities];
        // Skip the number of cities parameter
        citiesScanner.nextLine();
        for (int i = 0; i < this.numberOfCities; i++) {
            this.cityNames[i] = this.citiesScanner.next();
        }
    }

    // This method can take in a list, and will return the name of the cities cooresponding to the values (indeces) in the list
    public void getRouteNames(int[] route) {
        for (int i = 0; i < route.length; i++) {
            System.out.print(this.cityNames[route[i]-1]);
            if (i < route.length-1) {
                System.out.print(" -> ");
            }
        }
    }
}
