package src;
import java.io.*;
import java.util.Scanner;  
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;


public class Map {
    // file object ot point to the cities DB
    private File citiesFile;
    // scanner object which is used to parse text in the associiated file
    private Scanner citiesScanner;
    // variable to hold number of cities
    private int numberOfCities;
    // variable to hold city distances from txt file
    protected int[][] map;
    // Variable to hold string of cities
    protected String[] cityNames;
    

    public Map() {
        citiesFile = new File("GA-for-the-TSP/data/cities.txt");
        setNumberOfCities();
        setCityNames();
        createCitiesMatrix();
    }

    // This method is called repeatedly to reset the scanner, for when we go into the txt file multiple times, more or less
    //      for debugging purposes
    private void initilizeCitiesScanner() {
        //Scanner object wants to be in a try catch to get rid of warning if there is an invalid file path
        citiesScanner = null;
        try {
            citiesScanner = new Scanner(citiesFile);
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
        System.out.print("This city database contains " + numberOfCities + " cities.");
        for (int i = 0; i < numberOfCities; i++) {
            System.out.println(citiesScanner.next());
        }
    }

    public void printCitiesMatrixFromTxt() {
        //I'm just usually reseting the scanner each time we need to go back into the txt file
        initilizeCitiesScanner();

        //skip the number of cities
        citiesScanner.nextLine();

        //Print through the matrices
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                System.out.print(citiesScanner.next() + "\t");
            }
            System.out.println();
        }
    }

    // This method will create map from all cities in DB
    private void createCitiesMatrix() {
        //I'm just usually reseting the scanner each time we need to go back into the txt file
        initilizeCitiesScanner();
        //Skip the number of cities, and name of cities
        citiesScanner.nextLine();
        citiesScanner.nextLine();
        // Map var that will be returned
        map = new int[numberOfCities][numberOfCities];
        //Print through the matrices
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                // very dirty way to ignore the new lines in the txt file (each row)
                try {
                    map[i][j] = Integer.parseInt(citiesScanner.next());
                }
                catch(NumberFormatException exception) {
                    // exception.printStackTrace();
                }
            }
        }
    }


    // Return the matrix of cities
    public int[][] getCityMatrix() {
        return map;
    }

    //

    // return the number of cities
    public int getNumberOfCities() {
        return numberOfCities;
    }

    // set the number of cities from the txt file
    private void setNumberOfCities() {
        // Reset scanner object
        initilizeCitiesScanner();
        // Get the number of cities from the first piece of data in txt file
        numberOfCities = Integer.parseInt(citiesScanner.next());
    }

    // set the names of cities from the txt file
    private void setCityNames() {
        // Reset scanner object
        initilizeCitiesScanner();
        // Initialize the array size
        cityNames = new String[numberOfCities];
        // Skip the number of cities parameter
        citiesScanner.nextLine();
        for (int i = 0; i < numberOfCities; i++) {
            cityNames[i] = citiesScanner.next();
        }
    }

    public String[] getCityNames() {
        return cityNames;
    }

    // Take in route indices, return route names as Array List
    public String[] getRouteNames(int[] routeIndices) {
        String[] routeNames = new String[routeIndices.length];
        for (int i = 0; i < routeNames.length; i++) {
            routeNames[i] = cityNames[routeIndices[i]];
        }
        return routeNames;
    }

    // Take in route names, return route indices
    public int[] getRouteIndices(String[] routeNames) {
        int[] routeIndices = new int[routeNames.length];
        for (int i = 0; i < routeIndices.length; i++) {
            for (int j = 0; j < cityNames.length; j++) {
                if (routeNames[i] == cityNames[j]) {
                    routeIndices[i] = j;
                }
            }
        }
        return routeIndices;
    }
}
