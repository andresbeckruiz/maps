package edu.brown.cs.mramesh4.stars;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;



public class StarTest{
  private static final double DELTA = 1e-15;

  @Test
  public void testAccessors(){
    Star _star = new Star("1", "MyStar", "1", "0", "0");
    assertEquals(_star.getX(), 1, DELTA);
    assertEquals(_star.getY(), 0, DELTA);
    assertEquals(_star.getZ(), 0, DELTA);
    assertEquals(_star.getStarID(), "1");
    assertEquals(_star.getName(), "MyStar");
    assertEquals(_star.numDimensions(), 3, DELTA);
    assertEquals(_star.getInfo("name"), "MyStar");
    assertNull(_star.getInfo("other cat"));
    assertEquals(_star.getCoordinate(0), 1, DELTA);
    assertEquals(_star.getCoordinate(1), 0, DELTA);
    assertEquals(_star.getCoordinate(2), 0, DELTA);
  }
  @Test
  public void testDistance(){
    Star a = new Star("1", "MyStar", "0", "0", "0");
    Star b = new Star("2", "MyStar2", "3", "4", "0");
    assertEquals(b.calculateDistanceFromStar(a), 5, DELTA);
  }

  @Test
  public void testEquals(){
    Star a = new Star("1", "MyStar", "0", "0", "0");
    Star b = new Star("1", "MyStar", "0", "0", "0");
    Star c = new Star("1", "MyStar", "0", "0", "0");
    Star d = new Star("2", "MyStar2", "20", "20", "20");
    assertTrue(a.equals(b));
    assertTrue(b.equals(a));
    assertTrue(a.equals(a));
    assertTrue(a.equals(a));
    assertTrue(a.equals(c));
    assertTrue(c.equals(a));
    assertTrue(b.equals(c));
    assertTrue(c.equals(b));
    assertFalse(c.equals(d));
    assertFalse(a.equals(d));
    assertFalse(b.equals(d));
  }

  @Test
  public void testAxisDistance(){
    Star a = new Star("1", "MyStar", "0", "0", "0");
    Star b = new Star("1", "MyStar", "1", "0", "0");
    assertEquals(a.getAxisDistance(0,b ), 1, DELTA);
  }

  @Test(expected = IllegalArgumentException.class)
  public void exceptionCheckingforCons(){
    Star a = new Star(null, "3", "4", "5", null);
  }
  @Test(expected = IllegalArgumentException.class)
  public void exceptionCheckingforCons2(){
    Star a = new Star("1", "MyStar", "Darth", "Vader", "5");
  }
}