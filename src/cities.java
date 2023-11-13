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
    public int numberOfCities;
    
    // overloaded constructors, lets us pass in new path if we want, as of now, would have to be COMPLETE path for your system
    public cities(String path) {
        this.citiesFile = new File(path);
        initilizeCitiesScanner();
    }

    public cities() {
        this.citiesFile = new File("data/cities.txt");
        initilizeCitiesScanner();
    }

    public void initilizeCitiesScanner() {
        //Scanner object wants to be in a try catch to get rid of warning if there is an invalid file path
        try {
            citiesScanner = new Scanner(this.citiesFile);
            citiesScanner.useDelimiter(",");
            //Get the number of cities from the first piece of data in txt file
            this.numberOfCities = Integer.parseInt(this.citiesScanner.next());
            // citiesScanner.useDelimiter(",");

        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void printCities() {
        //Reset scanner each time we go back into it
        this.citiesScanner.reset();
        citiesScanner.useDelimiter(",");

        //Print the number of cities grabbed on initialization
        System.out.print("This city database contains " + this.numberOfCities + " cities.");
        for (int i = 0; i < this.numberOfCities; i++) {
            System.out.println(this.citiesScanner.next());
        }
    }

    public void printCitiesMatrixFromTxt() {
        //I'm just usually reseting the scanner each time we need to go back into the txt file
        this.citiesScanner.reset();
        citiesScanner.useDelimiter(",");

        //SKip to the data, I'm not sure why I only need this once insteas of twice (To skp the number and names)
        citiesScanner.nextLine();

        //Print through the matrices
        for (int i = 0; i < this.numberOfCities; i++) {
            for (int j = 0; j < this.numberOfCities; j++) {
                System.out.print(this.citiesScanner.next() + "\t");
            }
            System.out.println();
        }
    }

    public int[][] createCitiesMatrix() {
        //I'm just usually reseting the scanner each time we need to go back into the txt file
        this.citiesScanner.reset();
        citiesScanner.useDelimiter(",");
        //SKip to the data, I'm not sure why I only need this once insteas of twice (To skp the number and names)
        // citiesScanner.nextLine();
        int[][] map = new int[this.numberOfCities][this.numberOfCities];
        
        //Print through the matrices
        //row
        for (int i = 0; i < this.numberOfCities; i++) {
            //column
            for (int j = 0; j < this.numberOfCities; j++) {
                map[i][j] = Integer.parseInt(this.citiesScanner.next());
            }
        }
        return map;
    }
}
