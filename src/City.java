package src;


// This class calculates the distance between each city
// Will need to be updated to handle the G Maps api
public class City {
    private double x;
    private double y;

    public City(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceBetweenCities(City city) {
        double xCord = Math.abs(this.x - city.x);
        double yCord = Math.abs(this.y - city.y);
        return Math.sqrt((xCord * xCord) + (yCord * yCord));
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // public void setY(double y) {
    //     this.y = y;
    // }

    // public void setX(double x) {
    //     this.x = x;
    // }

    // Printing the e
    @Override
    public String toString() {
        return "City{" + "x=" + x + ", y=" + y + '}';
    }
}

