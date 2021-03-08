package edu.brown.cs.mramesh4.maps1;

import edu.brown.cs.mramesh4.MockPerson.MockPersonMethod;
import edu.brown.cs.mramesh4.REPLLoop.REPL;
import edu.brown.cs.mramesh4.maps.MapsLogic;
import edu.brown.cs.mramesh4.stars.ActionMethod;
import edu.brown.cs.mramesh4.stars.StarsLogic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.HashMap;

public class REPLTest {
  REPL repl;
  @Before
  public void setUp() {
    HashMap<String, ActionMethod<?>> methods = new HashMap<>();
    MockPersonMethod m = new MockPersonMethod();
    StarsLogic db = new StarsLogic();
    MapsLogic map = new MapsLogic();
    methods.put("stars", db);
    methods.put("naive_neighbors", db);
    methods.put("naive_radius", db);
    methods.put("mock", m);
    methods.put("neighbors", db);
    methods.put("radius", db);
    methods.put("map", map);
    methods.put("ways", map);
    methods.put("nearest", map);
    methods.put("route", map);
    repl = new REPL(methods);
  }
  @After
  public void tearDown() {
    repl = null;
  }

  @Test
  public void testSplit(){
    setUp();
    String line = "nearest_neighbors \"String One\"  \"String Two\" ";
    assertEquals(repl.splitString(line).length,3 );
  }
}
