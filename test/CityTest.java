package test;

import src.City;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

import static org.junit.Assert.*;


public class CityTest {

    @Test
    public static void main(String[] args) {
        City city1 = new City(0, 0);
        City city2 = new City(3, 4); // This should create a 3-4-5 right triangle

        double distance = city1.distanceBetweenCities(city2);

        Assert.assertEquals(5.0, distance, 0.0001);
    }
}
