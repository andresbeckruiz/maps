package edu.brown.cs.mramesh4.stars;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import edu.brown.cs.mramesh4.stars.*;
import edu.brown.cs.mramesh4.CSVParser.*;
import edu.brown.cs.mramesh4.KDTree.*;
import edu.brown.cs.mramesh4.Dimensional.*;


public class StarsPBTTest {
  private StarsLogic _stars;
  private static final double DELTA = 1e-15;
  @Before
  public void setUp() {
    _stars = new StarsLogic();
    CSVParser csv = new CSVParser();
    _stars.fillStarsList(csv.parseCSV("data/stars/tie-star.csv"));
  }

  @Before
  public void setUp2() {
    _stars = new StarsLogic();
    CSVParser csv = new CSVParser();
    _stars.fillStarsList(csv.parseCSV("data/stars/stardata.csv"));
  }

  @After
  public void tearDown() {
    _stars = null;
  }

  @Test(timeout = 100000)
  public void PBTTestNearestNeighborsXYZ() {
    setUp();
    assertEquals(_stars.getStarList().size(), 10);
    for(int j = 0; j < 100; j++) {
      double x = createInputForNeighbors();
      double y = createInputForNeighbors();
      double z = createInputForNeighbors();
      int k = createInputForNearest();
      Star toFind = _stars.findStar(x,y,z);
      assertNotNull(toFind);
      //run nearest neighbors
      List<Star> naive = _stars.naiveNearestNeighbors(k, x, y, z);
      List<Star> nonnaive = _stars.nearestNeighbors(k, x, y, z);
      if (k != 0) {
        assertFalse(naive.isEmpty());
        assertFalse(nonnaive.isEmpty());
      }
      assertEquals(naive.size(), nonnaive.size());
      for (int i = 0; i < k; i++) {
        assertEquals(naive.get(i).calculateDistanceFromStar(toFind),nonnaive.get(i).calculateDistanceFromStar(toFind), DELTA);
      }
    }
    tearDown();
  }
  @Test(timeout = 100000)
  public void PBTTestNearestRadiusXYZ() {
    setUp();
    assertEquals(_stars.getStarList().size(), 10);
    for(int j = 0;  j < 100; j++) {
      double x = createInputForNeighbors();
      double y = createInputForNeighbors();
      double z = createInputForNeighbors();
      Star toFind = _stars.findStar(x,y,z);
      if (toFind!=null) {
        //assertNotNull(toFind);
        int r = createInputForNearest();
        List<Star> naive = _stars.naiveNearestRadius(r, x, y, z);
        List<Star> nonnaive = _stars.nearestRadius(r, x, y, z);
        if (r != 0) {
          assertFalse(naive.isEmpty());
          assertFalse(nonnaive.isEmpty());
        }
        assertEquals(naive.size(), nonnaive.size());
        for (int i = 0; i < r; i++) {
          assertEquals(naive.get(i).calculateDistanceFromStar(toFind), nonnaive.get(i).calculateDistanceFromStar(toFind), DELTA);
        }
      }
    }
    tearDown();
  }

  @Test(timeout = 100000)
  public void PBTTestNearestRadiusXYZ2() {
    setUp2();
    for(int j = 0;  j < 2; j++) {
      double x = createInputForNeighbors();
      double y = createInputForNeighbors();
      double z = createInputForNeighbors();
      Star toFind = _stars.findStar(x,y,z);
      if (toFind!=null) {
        //assertNotNull(toFind);
        int r = createInputForNearest();
        List<Star> naive = _stars.naiveNearestRadius(r, x, y, z);
        List<Star> nonnaive = _stars.nearestRadius(r, x, y, z);
        if (r != 0) {
          assertFalse(naive.isEmpty());
          assertFalse(nonnaive.isEmpty());
        }
        assertEquals(naive.size(), nonnaive.size());
        for (int i = 0; i < r; i++) {
          assertEquals(naive.get(i).calculateDistanceFromStar(toFind), nonnaive.get(i).calculateDistanceFromStar(toFind), DELTA);
        }
      }
    }
    tearDown();
  }

  @Test(timeout = 100000)
  public void PBTTestNearestNeighborsXYZ2() {
    setUp2();
    for(int j = 0;  j < 2; j++) {
      double x = createInputForNeighbors();
      double y = createInputForNeighbors();
      double z = createInputForNeighbors();
      Star toFind = _stars.findStar(x,y,z);
      if (toFind!=null) {
        //assertNotNull(toFind);
        int r = createInputForNearest();
        List<Star> naive = _stars.naiveNearestNeighbors(r, x, y, z);
        List<Star> nonnaive = _stars.nearestNeighbors(r, x, y, z);
        if (r != 0) {
          assertFalse(naive.isEmpty());
          assertFalse(nonnaive.isEmpty());
        }
        assertEquals(naive.size(), nonnaive.size());
        for (int i = 0; i < r; i++) {
          assertEquals(naive.get(i).calculateDistanceFromStar(toFind), nonnaive.get(i).calculateDistanceFromStar(toFind), DELTA);
        }
      }
    }
    tearDown();
  }


  @Test(timeout = 100000)
  public void PBTTestNearestNeighborsName(){
    setUp();
    assertEquals(_stars.getStarList().size(), 10);
    for(int j = 0; j < 100; j++) {
      String name = createStarForNearest();
      int k = createInputForNearest();
      Star toFind = _stars.findStar(name);
      if(toFind!=null) {
        assertNotNull(toFind);
        //run nearest neighbors
        List<Star> naive = _stars.naiveNearestNeighbors(k, name);
        List<Star> nonnaive = _stars.nearestNeighbors(k, name);

        assertEquals(naive.size(), nonnaive.size());
        for (int i = 0; i < k; i++) {
          assertEquals(naive.get(i).calculateDistanceFromStar(toFind), nonnaive.get(i).calculateDistanceFromStar(toFind), DELTA);
        }
      }
    }
    tearDown();
  }
  @Test(timeout = 10000)
  public void PBTTestNearestRadius() {
    setUp();
    for (int j = 0; j < 100; j++) {
      String name = createStarForNearest();
      int k = createInputForNearest();
      Star toFind = _stars.findStar(name);
      if(toFind!=null) {
        assertNotNull(toFind);
        //run nearest neighbors
        List<Star> naive;
        List<Star> nonnaive;
        naive = _stars.naiveNearestRadius(k, name);
        nonnaive = _stars.nearestRadius(k, name);
        assertEquals(naive.size(), nonnaive.size());
        for (int i = 0; i < k; i++) {
          assertEquals(naive.get(i).calculateDistanceFromStar(toFind), nonnaive.get(i).calculateDistanceFromStar(toFind), DELTA);
        }
      }
    }
    tearDown();
  }

  @Test(timeout = 100000)
  public void PBTTestPassNeighbors(){
    setUp();
    assertEquals(_stars.getStarList().size(), 10);
    for(int j = 0; j < 100; j++){
      Star toFind = _stars.findStar(0,0,0);
      List<Star> naive = _stars.naiveNearestNeighbors(2, 0, 0, 0);
      List<Star> nonnaive = _stars.nearestNeighbors(2, 0, 0, 0);
      assertEquals(naive.size(), nonnaive.size());
      for (int i = 0; i < 2; i++) {
        assertEquals(naive.get(i).calculateDistanceFromStar(toFind),nonnaive.get(i).calculateDistanceFromStar(toFind), DELTA);
      }
    }
    tearDown();
  }

  public double createInputForNeighbors(){
    return (double) (-500 + (Math.random() * (1000)));
  }

  public int createInputForNearest(){
    return (int) Math.random() * 11;
  }

  public String createStarForNearest(){
    String[] names = new String[]{"Colton", "Daphne", "Daniel", "Lulu", "Harvey", "Sweetie Pie", "Mystery", "Rocket", "Tommy", "Charli"};
    int arg = (int) Math.random() * 10;
    return names[arg];
  }

}