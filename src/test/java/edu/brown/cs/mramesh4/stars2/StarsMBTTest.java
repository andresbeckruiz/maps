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


public class StarsMBTTest {
  private StarsLogic _stars;
  private static final double DELTA = 1e-15;
  @Before
  public void setUp() {
    _stars = new StarsLogic();
    CSVParser csv = new CSVParser();
    _stars.fillStarsList(csv.parseCSV("data/stars/tie-star.csv"));
  }


  @After
  public void tearDown() {
    _stars = null;
  }

  public void MBTTestNearestNeighborsXYZ() {
    setUp();
    assertEquals(_stars.getStarList().size(), 10);
    for(int j = 0; j < 100; j++) {
      double x = createInputForNeighbors();
      double y = createInputForNeighbors();
      double z = createInputForNeighbors();
      int k = createInputForNearest();
      //run nearest neighbors
      List<Star> naive = _stars.naiveNearestNeighbors(k, x, y, z);
      List<Star> nonnaive = _stars.nearestNeighbors(k, x, y, z);
      if (k != 0) {
        assertFalse(naive.isEmpty());
        assertFalse(nonnaive.isEmpty());
      }
      assertEquals(naive.size(), nonnaive.size());
      for (int i = 0; i < k; i++) {
        assertTrue(naive.get(i).equals(nonnaive.get(i)));
      }
    }
    tearDown();
  }

  public void MBTTestNearestRadiusXYZ() {
    setUp();
    assertEquals(_stars.getStarList().size(), 10);
    for(int j = 0;  j < 100; j++) {
      double x = createInputForNeighbors();
      double y = createInputForNeighbors();
      double z = createInputForNeighbors();
      int k = createInputForNearest();
      List<Star> naive = _stars.naiveNearestRadius(k, x, y, z);
      List<Star> nonnaive = _stars.nearestRadius(k, x, y, z);
      if (k != 0) {
        assertFalse(naive.isEmpty());
        assertFalse(nonnaive.isEmpty());
      }
      assertEquals(naive.size(), nonnaive.size());
      for (int i = 0; i < naive.size(); i++) {
        assertTrue(naive.get(i).equals(nonnaive.get(i)));
      }
    }
    tearDown();
  }

  public void MBTTestNearestNeighborsName(){
    setUp();
    assertEquals(_stars.getStarList().size(), 10);
    for(int j = 0; j < 10000; j++) {
      String name = createStarForNearest();
      int k = createInputForNearest();
      //run nearest neighbors
      List<Star> naive = _stars.naiveNearestNeighbors(k, name);
      List<Star> nonnaive = _stars.nearestNeighbors(k, name);

      assertEquals(naive.size(), nonnaive.size());
      for (int i = 0; i < k; i++) {
        assertTrue(naive.get(i).equals(nonnaive.get(i)));
      }
    }
    tearDown();
  }

  public void MBTTestFailNeighbors(){
    setUp();
    assertEquals(_stars.getStarList().size(), 10);
    for(int j = 0; j < 1000; j++){
      List<Star> naive = _stars.naiveNearestNeighbors(2, 0, 0, 0);
      List<Star> nonnaive = _stars.nearestNeighbors(2, 0, 0, 0);
      assertEquals(naive.size(), nonnaive.size());
      for (int i = 0; i < 2; i++) {
        assertTrue(naive.get(i).equals(nonnaive.get(i)));
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