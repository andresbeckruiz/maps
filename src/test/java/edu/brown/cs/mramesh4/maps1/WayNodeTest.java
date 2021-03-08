package edu.brown.cs.mramesh4.maps1;

import edu.brown.cs.mramesh4.maps.MapsLogic;
import edu.brown.cs.mramesh4.maps.Way;
import edu.brown.cs.mramesh4.maps.WayNodes;
import edu.brown.cs.mramesh4.stars.Star;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;



public class WayNodeTest{
  private WayNodes way;
  private WayNodes way2;

  private static final double DELTA = 0.05;
  @Before
  public void setUp() {
    way = new WayNodes("/n/0", -72, 71, null);
    way2 = new WayNodes("/n/1", -75, 70, null);
  }

  @After
  public void tearDown() {
    way = null;
    way2 = null;
  }

  @Test
  public void testWayNodeAccessors(){
    setUp();
    assertEquals(way.numDimensions(), 2, DELTA);
    assertEquals(way.getId(), way.getInfo("id"));
    assertEquals("/n/0", way.getId());
    assertEquals(way.getLat(), -72, DELTA);
    assertEquals(way.getLong(), 71, DELTA);
    assertEquals(way.getAxisDistance(1, way2), 1, DELTA);
    assertEquals(way.distanceBetween(way2), Math.sqrt(10), DELTA);
    assertEquals(way.getCoordinate(0), -72, DELTA);
    assertEquals(way.getCoordinate(1), 71, DELTA);
    assertEquals("/n/0", way.getName());
    WayNodes way3 = new WayNodes("/n/0", -72, 71, null);
    assertTrue(way.equals(way3));
    assertFalse(way.equals(way2));
    way.setCurrentDistance(13);
    assertEquals(way.getCurrentDistance(), 13, DELTA);
    way.setTotalWeight(13);
    assertEquals(way.getTotalWeight(), 13, DELTA);
    Way connection = new Way("way", way, way2);
    way.setFrom(connection);
    assertEquals(connection, way.getFrom());
    tearDown();
  }
  @Test
  public void testHaversineDistance(){
    setUp();
    assertEquals(335.064833650095, way.toGoal(way2), DELTA);
    tearDown();
  }
  @Test
  public void testWayNodeGetters(){
    try {
      setUp();
      Connection conn = DriverManager.getConnection("data/maps/smallMaps.sqlite3");
      way = new WayNodes("/n/0", 41.82, -71.4, conn);
      assertEquals(way.neighbors().get(0).getId(), "/n/1");
      assertEquals(way.neighbors().get(1).getId(), "/n/3");
      assertEquals(way.neighbors().get(0).getName(), "/n/1");
      assertEquals(way.neighbors().size(), 2);
      assertNotNull(way.neighbors());
      tearDown();
      //test neighbors method here with real node
    } catch(SQLException e){
      tearDown();
    }
  }

  @Test
  public void testDistanceBetween(){
    setUp();
    assertEquals(way.distanceBetween(new Star("id", "name", "14", "15", "16")), -1, DELTA);
    tearDown();
  }
  @Test
  public void testSQLException(){
    try {
      setUp();
      Connection conn = DriverManager.getConnection("data/maps/smallMaps.sqlite3");
      way = new WayNodes("/n/0", 41.82, -71.4, conn);
      assertNotNull(way.neighbors());
      for(WayNodes w: way.neighbors()){
        assertNotNull(w);
      }
      tearDown();
      //test neighbors method here with real node
    } catch(SQLException e){
      tearDown();
    }
  }


}
