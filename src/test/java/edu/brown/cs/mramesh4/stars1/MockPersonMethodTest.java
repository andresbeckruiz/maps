package edu.brown.cs.mramesh4.stars;
import static org.junit.Assert.*;
import edu.brown.cs.mramesh4.stars.*;
import edu.brown.cs.mramesh4.MockPerson.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
public class MockPersonMethodTest{
  private MockPersonMethod mp;
  private static final double DELTA = 1e-15;

  @Before
  public void setUp() {
    mp = new MockPersonMethod();
  }

  @After
  public void tearDown(){
    mp = null;
  }

  @Test
  public void testRun(){
    String[] coms = new String[]{"mock", "nullcsv", "too", "many", "commands"};
    mp.run(coms);
    assertTrue(mp.getPeople().isEmpty());
    String[] coms2 = new String[]{"notmock", "csv"};
    mp.run(coms2);
    assertTrue(mp.getPeople().isEmpty());
    String[] coms3 = new String[]{"mock", "data/stars/no_malformed_input_mock.csv"};
    mp.run(coms3);
    assertFalse(mp.getPeople().isEmpty());
  }

  @Test
  public void testCSV(){
    mp.loadFileAndPrint("data/stars/malformed_header");
    assertTrue(mp.getPeople().isEmpty());
    mp.loadFileAndPrint("data/stars/mock_data_with_percentage_empty.csv");
    assertTrue(mp.getPeople().isEmpty());
    mp.loadFileAndPrint("data/stars/three-star.csv");
    assertTrue(mp.getPeople().isEmpty());
    mp.loadFileAndPrint("data/stars/no_malformed_input_mock.csv");
    assertFalse(mp.getPeople().isEmpty());
  }
}
