package edu.brown.cs.mramesh4.stars;
//import edu.brown.cs.mramesh4.stars.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import java.util.List;
import java.util.ArrayList;
import edu.brown.cs.mramesh4.CSVParser.*;


public class CSVParserTest{
  private CSVParser _csv;

  @Before
  public void setUp() {
    _csv = new CSVParser();
  }

  @After
  public void tearDown() {
    _csv = null;
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidFile(){
    setUp();
    _csv.setFileToParse("data/stars/ten.csv");
    tearDown();
  }

  @Test
  public void testifFileisInValid(){
    setUp();
    assertFalse(_csv.isValidFile("data/stars/ten.csv"));
    tearDown();
  }

  @Test
  public void testValidFile(){
    setUp();
    assertTrue(_csv.isValidFile("data/stars/ten-star.csv"));
    tearDown();
  }

  @Test
  public void checkIfParsingWorks(){
    setUp();
    ArrayList<List<String>> _ret = _csv.parseCSV("data/stars/three-star.csv");
    assertEquals(_ret.size(), 4);
    assertEquals(_csv.getRows(), 3);
    assertEquals(_csv.getCols(), 5);
    tearDown();
  }

  @Test
  public void checkIfParsingWorksTwo(){
    setUp();
    ArrayList<List<String>> _ret = _csv.parseCSV("data/stars/ten-star.csv");
    assertEquals(_ret.size(), 11);
    assertEquals(_csv.getRows(), 10);
    assertEquals(_csv.getCols(), 5);
    tearDown();
  }

  @Test(expected = IllegalArgumentException.class)
  public void exceptionCheckingforRows(){
    setUp();
    assertEquals(_csv.getRows(), 4);
    tearDown();
  }

  @Test(expected = IllegalArgumentException.class)
  public void exceptionCheckingforCols(){
    setUp();
    assertEquals(_csv.getCols(), 4);
    tearDown();
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkFileToParse(){
    setUp();
    _csv.setFileToParse("test");
  }
  @Test(expected = IllegalArgumentException.class)
  public void nullReturnForBadParse(){
    setUp();
    assertNull(_csv.parseCSV("data"));
  }

}