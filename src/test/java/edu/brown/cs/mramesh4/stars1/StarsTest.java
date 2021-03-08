package edu.brown.cs.mramesh4.stars;
import static org.junit.Assert.*;
import edu.brown.cs.mramesh4.stars.*;
import edu.brown.cs.mramesh4.CSVParser.*;
import edu.brown.cs.mramesh4.KDTree.*;
import edu.brown.cs.mramesh4.Dimensional.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.util.*;
import java.util.List;
import java.util.ArrayList;


public class StarsTest{
  private StarsLogic _stars;
  private static final double DELTA = 1e-15;
  @Before
  public void setUp() {
    _stars = new StarsLogic();
    CSVParser csv = new CSVParser();
    _stars.fillStarsList(csv.parseCSV("data/stars/three-star.csv"));
  }


  @After
  public void tearDown() {
    _stars = null;
  }

  @Test
  public void testStarsFilled(){
    setUp();
    assertEquals(_stars.getStarList().size(), 3);
    tearDown();
  }

  @Test
  public void testStarsCanBeReFilled(){
    setUp();
    assertEquals(_stars.getStarList().size(), 3);
    CSVParser csv = new CSVParser();
    String[] s = new String[2];
    s[0] = "stars";
    s[1] = "data/stars/one-star.csv";
    _stars.fillStarsListByComs(s);
    assertEquals(_stars.getStarList().size(), 1);
    assertTrue((_stars.getStarList().get(0).getName()).equals("Lonely Star"));
    tearDown();
  }

  @Test
  public void testFindStarXYZ(){
    setUp();
    assertNotNull(_stars.findStar(3,0,0));
    tearDown();
  }

  @Test
  public void testNearestNeighbors(){
    setUp();
    assertEquals(_stars.naiveNearestNeighbors(2, 1, 0, 0).get(1).getX(), 2, DELTA);
    tearDown();
  }

  @Test
  public void testNearestRadius(){
    setUp();
    assertEquals(_stars.naiveNearestRadius(2, 1, 0, 0).get(1).getX(), 2, DELTA);
    tearDown();
  }

  @Test
  public void testFindStarByName(){
    setUp();
    assertNotNull(_stars.findStar("Star One"));
    tearDown();
  }

  @Test
  public void testNullStarByName() {
    setUp();
    assertNull(_stars.findStar("Star Five"));
    tearDown();
  }

  @Test
  public void testIsClassInt(){
    setUp();
    String[] withInt = new String[]{"nn", "n", "5"};
    assertTrue(_stars.isClassInt(withInt));
    String[] withIntTwo = new String[]{"nn", "n", "fourty"};
    assertFalse(_stars.isClassInt(withIntTwo));
    tearDown();
  }

  @Test
  public void testNearestNull(){
    setUp();
    assertNull(_stars.naiveNearestRadius(5, "ERAIZFVXIVORSGVJFBGIDB"));
    assertNull(_stars.naiveNearestNeighbors(5, "ERAIZFVXIVORSGVJFBGIDB"));
    tearDown();
  }


  @Test
  public void testProcessComs(){
    setUp();
    String[] cmd = new String[]{"naive_neighbors", "5", ""};
    String com = "naive_neighbors 5 \"Lonely Star\"";
    String comNull = "naive_neighbors 5 \"Lonely \"S\"tar\"";
    String comNull2 = "naive_neighbors 5 \"Lonely Star\" extra";
    String[] coms = _stars.processComs(com, cmd);
    assertEquals("Lonely Star", coms[2]);
    assertEquals(coms.length,3);
    assertNull(_stars.processComs(comNull, cmd));
    assertNull(_stars.processComs(comNull2, cmd));
    tearDown();
  }

  @Test
  public void testNearestRadiusTwo(){
    setUp();
    assertEquals(_stars.nearestRadius(1, 1, 0, 0).get(0).getStarID(), "1");
    assertNull(_stars.nearestRadius(1,"jaifdnvnsira"));
    assertEquals(_stars.naiveNearestRadius(1,1,0,0).get(0).getStarID(), "1");
    assertNull(_stars.naiveNearestRadius(1,"jaifdnvnsira"));
    tearDown();
  }
  @Test
  public void testNearestNeighborsTwo(){
    setUp();
    assertNull(_stars.nearestNeighbors(1,"jaifdnvnsira"));
    assertEquals(_stars.nearestNeighbors(2, 1, 0,0).get(1).getStarID(),"2");
    assertNull(_stars.naiveNearestNeighbors(1,"jaifdnvnsira"));
    assertEquals(_stars.naiveNearestNeighbors(2, 1, 0,0).get(1).getStarID(),"2");
    assertEquals(_stars.nearestNeighbors(0, 1,0,0).size(), 0, DELTA);
    tearDown();
  }

  @Test
  public void testFillStarsList(){
    setUp();
    String[] coms = new String[]{"stars", "data/stars/ten-star.csv"};
    String[] invalidComs = new String[]{"stars", "data/stars/ten-star.csv", "extra"};
    String[] invalidFiles = new String[]{"stars", "data/stars/ten-star-two.csv"};
    String[] malformedFiles = new String[]{"stars", "data/stars/malformed_header.csv"};
    _stars.fillStarsListByComs(malformedFiles);
    assertEquals(_stars.getStarList().size(), 3);
    _stars.fillStarsListByComs(invalidComs);
    assertEquals(_stars.getStarList().size(), 3);
    _stars.fillStarsListByComs(invalidFiles);
    assertEquals(_stars.getStarList().size(), 3);
    _stars.fillStarsListByComs(coms);
    assertFalse(_stars.getStarList().isEmpty());
    tearDown();
  }

  @Test
  public void testFillingStars(){
    setUp();
    CSVParser csv = new CSVParser();
    _stars.fillStarsList(csv.parseCSV("data/stars/ten-star.csv"));
    assertEquals(_stars.getStarList().size(), 10);
    _stars.fillStarsList(csv.parseCSV("data/stars/bad-star.csv"));
    assertEquals(_stars.getStarList().size(), 0);
    tearDown();
  }

  @Test
  public void testNeighbors(){
    _stars = new StarsLogic();
    String[] coms = new String[]{"neighbors", "5", "1", "0", "0"};
    _stars.neighborsCall(coms);
    assertTrue(_stars.getStarList().isEmpty());
    setUp();
    String comNull = "naive_neighbors 5 \"Lonely \"S\"tar\"";
    String[] coms2 = new String[]{"naive_neighbors", "5", "\"Lonely \"", "Star\""};
    _stars.neighborsCall(coms2);
    assertFalse(_stars.isClassInt(coms2));
    tearDown();
  }
  @Test
  public void testRadius(){
    _stars = new StarsLogic();
    String[] coms = new String[]{"radius", "5", "1", "0", "0"};
    _stars.radiusCall(coms);
    assertTrue(_stars.getStarList().isEmpty());
    tearDown();
  }

  @Test
  public void testRun(){
    setUp();
    String[] coms = new String[]{"stars", "data/stars/ten-star.csv"};
    _stars.run(coms);
    assertEquals(_stars.getStarList().size(), 10);
    String[] coms2 = new String[]{"stars", "data/stars/three-star.csv"};
    _stars.run(coms2);
    assertEquals(_stars.getStarList().size(), 3);
    tearDown();
  }

  @Test
  public void testRunTwo(){
    _stars = new StarsLogic();
    String[] coms = new String[]{"neighbors", "2", "1", "0", "0"};
    _stars.run(coms);
    assertTrue(_stars.getStarList().isEmpty());
    String[] coms2 = new String[]{"naive_neighbors", "2", "1", "0", "0"};
    _stars.run(coms2);
    String[] coms3 = new String[]{"radius", "2", "1", "0", "0"};
    assertTrue(_stars.getStarList().isEmpty());
    _stars.run(coms3);
    assertTrue(_stars.getStarList().isEmpty());
    String[] coms4 = new String[]{"naive_radius", "2", "1", "0", "0"};
    _stars.run(coms4);
    assertTrue(_stars.getStarList().isEmpty());
    String[] coms5 = new String[]{"incoherent method ", "2", "1", "0", "0"};
    _stars.run(coms5);
    assertTrue(_stars.getStarList().isEmpty());
    tearDown();
  }

 @Test
  public void testNaiveNearestNeighborsZero(){
    setUp();
    assertTrue(_stars.naiveNearestNeighbors(0, 1, 1, 1).isEmpty());
    assertTrue(_stars.naiveNearestNeighbors(0, "Star One").isEmpty());
    assertTrue(!_stars.naiveNearestNeighbors(3, 1, 0, 0).isEmpty());
    assertTrue(!_stars.naiveNearestRadius(3, 1, 0, 0).isEmpty());
    assertTrue(!_stars.naiveNearestRadius(3, "Star One").isEmpty());
    tearDown();
  }

 @Test
  public void testFindStarNotNull(){
    setUp();
    assertNotNull(_stars.findStar(1,1,1));
    tearDown();
  }

  @Test
  public void getKDTree(){
    setUp();
    assertNotNull(_stars.getKDTree());
    tearDown();
  }

 @Test(timeout=10000)
  public void testRetNull(){
    _stars = new StarsLogic();

    String cmd = "neighbors 5 0 0 0";
    String[] split = cmd.split(" ");
    assertNull(_stars.neighborsRet(cmd, split));

    setUp();

    String cmd2 = "neighbors 5 \"\"Sol\"";
    String[] split2 = cmd2.split(" ");
    assertNull(_stars.neighborsRet(cmd2, split2));

    String cmd3 = "naive_neighbors 5 AXIS \"\"Sol\"";
    String[] split3 = cmd3.split(" ");
    assertNull(_stars.neighborsRet(cmd3, split3));

    String cmd4 = "neighbors 5 0 string 0";
    String[] split4 = cmd4.split(" ");
    assertNull(_stars.neighborsRet(cmd4, split4));

    String cmd5 = "neighbors -1 0 0 0";
    String[] split5 = cmd5.split(" ");
    assertNull(_stars.neighborsRet(cmd5, split5));

    String cmd6 = "neighbors 1 0 0 0";
    String[] split6 = cmd6.split(" ");
    assertNotNull(_stars.neighborsRet(cmd6, split6));

    String cmd7 = "neighbors 3 \"Star One\"";
    String[] split7 = cmd7.split(" ");
    assertNotNull(_stars.neighborsRet(cmd7, split7));

    String cmd8 = "naive_neighbors 3 \"Star One\"";
    String[] split8 = cmd8.split(" ");
    assertNotNull(_stars.neighborsRet(cmd8, split8));

    String cmd9 = "naive_neighbors 3 0 0 0";
    String[] split9 = cmd9.split(" ");
    assertNotNull(_stars.neighborsRet(cmd9, split9));

    tearDown();
  }

  @Test(timeout=10000)
  public void testRetRadiusNull(){
    _stars = new StarsLogic();
    String cmd = "radius 5 0 0 0";
    String[] split = cmd.split(" ");
    assertNull(_stars.radiusRet(cmd, split));
    setUp();
    String cmd2 = "radius 5 \"\"Sol\"";
    String[] split2 = cmd2.split(" ");
    assertNull(_stars.radiusRet(cmd2, split2));
    String cmd3 = "naive_radius 5 AXIS \"\"Sol\"";
    String[] split3 = cmd3.split(" ");
    assertNull(_stars.radiusRet(cmd3, split3));
    String cmd4 = "radius 5 0 string 0";
    String[] split4 = cmd4.split(" ");
    assertNull(_stars.radiusRet(cmd4, split4));

    String cmd5 = "radius -1 0 0 0";
    String[] split5 = cmd5.split(" ");
    assertNull(_stars.radiusRet(cmd5, split5));

    String cmd6 = "radius 3 0 0 0";
    String[] split6 = cmd6.split(" ");
    assertNotNull(_stars.radiusRet(cmd6, split6));

    String cmd7 = "radius 3 \"Star One\"";
    String[] split7 = cmd7.split(" ");
    assertNotNull(_stars.radiusRet(cmd7, split7));

    String cmd8 = "naive_radius 3 0 0 0";
    String[] split8 = cmd8.split(" ");
    assertNotNull(_stars.radiusRet(cmd8, split8));

    String cmd9 = "naive_radius 3 \"Star One\"";
    String[] split9 = cmd9.split(" ");
    assertNotNull(_stars.radiusRet(cmd9, split9));
  }

}