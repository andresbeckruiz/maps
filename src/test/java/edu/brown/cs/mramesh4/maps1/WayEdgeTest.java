package edu.brown.cs.mramesh4.maps1;

import edu.brown.cs.mramesh4.maps.Way;
import edu.brown.cs.mramesh4.maps.WayNodes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class WayEdgeTest {
  private WayNodes way;
  private WayNodes way2;
  private Way myWay;

  private static final double DELTA = 0.05;
  @Before
  public void setUp() {
    way = new WayNodes("/n/0", -72, 71, null);
    way2 = new WayNodes("/n/1", -75, 70, null);
    myWay = new Way("/w/5", way, way2);
  }

  @After()
  public void tearDown(){
    way = null;
    way2 = null;
    myWay = null;
  }

  @Test
  public void testAccessors(){
    setUp();
    assertEquals(myWay.getFrom(), way);
    assertEquals(myWay.getTo(), way2);
    assertEquals(335.064833650095, myWay.getWeight(), DELTA);
    tearDown();
  }

  @Test
  public void testHavensire(){
    setUp();
    WayNodes way3 = new WayNodes("/n/0", 42, 10, null);
    WayNodes way4 = new WayNodes("/n/1", -32.3, 72.4, null);
    assertEquals(10431.7417385285, myWay.getHaversineDistance(way3, way4), DELTA);
    tearDown();
  }
}
