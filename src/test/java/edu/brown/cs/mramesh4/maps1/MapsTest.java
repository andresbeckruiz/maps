package edu.brown.cs.mramesh4.maps;
import static org.junit.Assert.*;
import edu.brown.cs.mramesh4.stars.*;
import edu.brown.cs.mramesh4.CSVParser.*;
import edu.brown.cs.mramesh4.KDTree.*;
import edu.brown.cs.mramesh4.Dimensional.*;
import edu.brown.cs.mramesh4.maps.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.util.*;
import java.util.List;
import java.util.ArrayList;


public class MapsTest{
  private MapsLogic _maps;
  private static final double DELTA = 1e-15;
  @Before
  public void setUp() {
    _maps = new MapsLogic();
    String[] db = new String[]{"map", "data/maps/smallMaps.sqlite3"};
    _maps.map(db);
  }

  @After
  public void tearDown() {
    _maps = null;
  }

  @Test
  public void testMapsRun(){
    _maps = new MapsLogic();
    String[] db = new String[]{"map", "data/maps/smallMaps.sqlite3"};
    _maps.run(db);
    assertFalse(_maps.getWayNodeList().isEmpty());
    assertEquals(_maps.getWayNodeList().size(), 6, DELTA);
    String[] db2 = new String[]{"map", "data/maps/maps.sqlite3"};
    _maps.run(db2);
    assertEquals(_maps.getWayNodeList().size(), 639498, DELTA);
    assertFalse(_maps.getWayNodeList().isEmpty());
    String[] db3 = new String[]{"ways", "41.02", "-38.22", "43.02", "-37.22"};
    _maps.run(db3);
    String[] db4 = new String[]{"ways", "41.02", "-38.22", "39.02", "-37.22"};
    _maps.run(db4);
    String[] db5 = new String[]{"route", "41.02", "-38.22", "39.02", "-37.22"};
    _maps.run(db5);
    String[] db6 = new String[]{"route", "\"Radish Spirit Blvd\"", "\"Yubaba St\"", "\"Yubaba St\"", "\"Sootball Ln\""};
    _maps.run(db6);
    tearDown();
  }

  @Test
  public void testMapTableValid(){
    setUp();
    String[] table = new String[]{"node", "way"};
    String[] table2 = new String[]{"myCustomTable"};
    String[] table3 = new String[]{"node", "way", "myCustom"};
    assertTrue(_maps.isValidTable(table));
    assertFalse(_maps.isValidTable(table2));
    assertFalse(_maps.isValidTable(table3));
    tearDown();
  }

  @Test
  public void testInteraction(){
    setUp();
    WayNodes ret = _maps.getWayNodesAtIntersection("\"Radish Spirit Blvd\"", "\"Yubaba St\"");
    assertNotNull(ret);
    assertEquals(ret.getId(), "/n/3");
    assertEquals(ret.getLong(), -71.4003, DELTA);
    //WayNodes ret2 = _maps.getWayNodesAtIntersection("\"Kamaji Pl\"", "\"Radish Spirit Blvd\"");
    //assertNull(ret);
    tearDown();
  }

  @Test
  public void testgetByLatLong(){
    setUp();
    assertTrue(_maps.getWayNodeByLatLong(41.82, -71.4003).equals( _maps.getWayNodesAtIntersection("\"Radish Spirit Blvd\"", "\"Yubaba St\"")));
    assertEquals(_maps.getWayNodeByLatLong(41.82, -71.4003).getId(), "/n/3");
    assertEquals(_maps.getWayNodeByLatLong(44.8224, -72.3003).getId(), "temp");
    tearDown();
  }

  @Test
  public void testIsClassInt(){
    setUp();
    String[] entry = new String[]{"ways", "2", "3", "4", "5", "6"};
    String[] entry2 = new String[]{"ways", "ways", "ways", "ways", "ways", "6"};
    assertTrue(_maps.isClassInt(entry));
    assertFalse(_maps.isClassInt(entry2));
    tearDown();
  }
  @Test
  public void testNearest(){
    _maps = new MapsLogic();
    String[] entry = new String[]{"nearest", "41.82", "-71.4"};
    String[] entry2 = new String[]{"nearest", "41.2", "32.7", "Extra"};
    assertNull(_maps.nearest(entry));
    setUp();
    assertNull(_maps.nearest(entry2));
    assertEquals(_maps.nearest(entry).getId(), "/n/0");
    tearDown();
  }

}