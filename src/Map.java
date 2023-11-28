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
    private int[][] map;
    // Variable to hold string of cities
    private String[] cityNames;
    // Variable to hold user created Map
    private int[][] userMap;
    // Variable to hold string of cities tied to user map
    private String[] userCityNames;
    // Variable to hold number of user Cities
    private int userNumberOfCities;
    // User defined route, used to create the user defined map
    private int[] userRoute = {};
    // Object for useing google API
    private Google google;
    

    public Map() {
        map = null;
        citiesFile = new File("GA-for-the-TSP/data/cities.txt");
        System.out.println("New map");
        google = new Google();
        setNumberOfCities();
        setCityNames();
        createCitiesMatrix();
    }

    // Reload from database, used when adding cities
    public void reload() {
        map = null;
        System.out.println("Reloading data from database");
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

    public Boolean addCity(String city) {
        // check if city already exists
        for (int i = 0; i < numberOfCities; i++) {
            if (cityNames[i].equals(city)) {
                System.out.println("City already exists");
                return false;
            }
        }

        ArrayList<Integer> newDistances = new ArrayList<>();

        for (int i = 0; i < numberOfCities; i++) {
            newDistances.add(google.getDistance(cityNames[i], city));

        }
        // This is the 0 for the city -> city (self to self) which can just be manually added
        newDistances.add(0);
        addCityToDatabase(newDistances, city);
        return true;
    }

    private void addCityToDatabase(ArrayList<Integer> newDistances, String city) {
        // Object to hold new data to be written. This will be built up with the existing data, grabbed from the city matrix, and them 
        StringBuilder content = new StringBuilder();
        System.out.println("Adding to city database, current count is " + numberOfCities);
        
        // This will always hit and always be just +1 for new city count
        content.append((numberOfCities+1) + ",\n");

        // Add existing city names
        for (int i = 0; i < numberOfCities; i++) {
            content.append(cityNames[i] + ",");
        }
        // Add new city name
        content.append((city) + ",\n");
        // Append the existing distances to the content
        for(int i = 0; i < numberOfCities; i++) {
            for(int j = 0; j < numberOfCities; j++) {
                content.append((map[i][j]) + ",");
                // content.append(i + "-" + j + ",");
            }
            // append new distance
            content.append(newDistances.get(i) + ",\n");
        }

        // Append the new distances in the last row
        for (int i = 0; i < newDistances.size(); i++) {
            content.append(newDistances.get(i) + ",");
        }
        

        // Write the modified content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(citiesFile))) {
            writer.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
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

    public void setUserRoute(int[] userRoute) {
        this.userRoute = userRoute;
        System.out.print("User route is ");
        for (int i = 0; i < this.userRoute.length; i++) {
            System.out.print(this.userRoute[i] + ", ");
        }
        System.out.println();
        setUserNumberOfCities();
        createUserCitiesMatrix();
        setUserCityNames();
    }

    // This method will create map from all cities in DB
    private void createCitiesMatrix() {
        map = null;
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
                // System.out.println("i = " + i + ", j = " + j);
                try {
                    map[i][j] = Integer.parseInt(citiesScanner.next());
                    // System.out.print(map[i][j] + ",");
                }
                catch(NumberFormatException exception) {
                    // System.out.println("Here");
                }
            }
            // System.out.println();
            citiesScanner.nextLine();
        }
    }

    // Method for creating cities matrix for the submap, takes in route. Refernces parent map
    private void createUserCitiesMatrix() {
        userMap = null;
        // Map var that will be returned
        userMap = new int[userNumberOfCities][userNumberOfCities];
        //Print through the matrices
        for (int i = 0; i < userNumberOfCities; i++) {
            for (int j = 0; j < userNumberOfCities; j++) {
                // very dirty way to ignore the new lines in the txt file (each row)
                try {
                    // System.out.println("i is: " + i + ", j is: " + j);
                    userMap[i][j] = map[userRoute[i]-1][userRoute[j]-1];
                }
                catch(NumberFormatException exception) {
                    // exception.printStackTrace();
                }
            }
        }
    }
    // set the names of cities from the txt file
    private void setCityNames() {
        cityNames = null;
        System.out.println("set city names call");
        // Reset scanner object
        initilizeCitiesScanner();
        // Initialize the array size
        cityNames = new String[numberOfCities];
        // Skip the number of cities parameter
        citiesScanner.nextLine();
        for (int i = 0; i < numberOfCities; i++) {
            cityNames[i] = citiesScanner.next();
            System.out.println(cityNames[i]);
        }
    }

    // Method for setting user route names
    private void setUserCityNames() {
        // Initialize the array size
        userCityNames = new String[userNumberOfCities];
        for (int i = 0; i < userNumberOfCities; i++) {
            userCityNames[i] = (cityNames[userRoute[i]-1]);
        }
    }

    // set the number of cities from the txt file
    private void setNumberOfCities() {
        // Reset scanner object
        initilizeCitiesScanner();
        // Get the number of cities from the first piece of data in txt file
        numberOfCities = Integer.parseInt(citiesScanner.next());
    }

    // set the number of cities from the txt file
    private void setUserNumberOfCities() {
        userNumberOfCities = userRoute.length;
    }

    // Return the matrix of cities
    public int[][] getCityMatrix() {
        return map;
    }

    // return the userCityMatrix created from user route
    public int[][] getUserCityMatrix() {
        return userMap;
    }

    // return the number of cities
    public int getNumberOfCities() {
        return numberOfCities;
    }

    // return the number of cities in the users route
    public int getUserNumberOfCities() {
        return userNumberOfCities;
    }

    // return all city names in the dataset
    public String[] getCityNames() {
        return cityNames;
    }

    // return the city names in the user route set
    public String[] getUserCityNames() {
        return userCityNames;
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
        // System.out.println("route indices call");
        int[] routeIndices = new int[routeNames.length];
        for (int i = 0; i < routeIndices.length; i++) {
            // System.out.println("route city name is  " + routeNames[i] + ", checking against the following:");
            for (int j = 0; j < cityNames.length; j++) {
                // System.out.println("city name: " + cityNames[j]);
                if (routeNames[i].equals(cityNames[j])) {
                    routeIndices[i] = j+1;
                    // System.out.println("hit");
                    // System.out.println(routeIndices[i]);
                }
            }
        }
        return routeIndices;
    }

    // returns ALL cities in database in string
    public String toString() {
        String output = "";
        for (int i = 0; i < map.length; i++) {
            if (i < (map.length)-1) {
                output = output + (cityNames[i]) + ", ";
            }

            else {
                output = output + (cityNames[i]);
            }

        }
        return output;
    }

    // returns user cities in a string
    public String toStringUser() {
        String output = "";
        for (int i = 0; i < userNumberOfCities; i++) {
            if (i < (userNumberOfCities)-1) {
                output = output + (userCityNames[i]) + ", ";
            }

            else {
                output = output + (userCityNames[i]);
            }

        }
        return output;
    }
}
