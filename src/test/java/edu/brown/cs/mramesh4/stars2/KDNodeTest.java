package edu.brown.cs.mramesh4.KDTree;
import edu.brown.cs.mramesh4.Dimensional.DimensionalComparator;
import edu.brown.cs.mramesh4.Dimensional.Dimensional;
import edu.brown.cs.mramesh4.stars.Star;
//import edu.brown.cs.mramesh4.stars.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import java.util.List;
import java.util.ArrayList;



public class KDNodeTest{
  private Star a;
  private Star b;
  private Star c;
  private KDNode<Dimensional> compL;
  private KDNode<Dimensional> compR;
  private KDNode<Dimensional> base;


  @Before
  public void setUp() {
    a = new Star("0", "0", "1.0", "2.0", "3.0");
    b = new Star("2", "2", "2.0", "3.0", "4.0");
    c = new Star("3", "3", "3.0", "3.0", "4.0");
    compL = new KDNode<Dimensional>(b);
    compR = new KDNode<Dimensional>(c);
    base = new KDNode<Dimensional>(a, compL, compR, 0, false);
  }

  @After
  public void tearDown() {
    a = null;
    b = null;
    c = null;
    compL = null;
    compR = null;
    base = null;
  }

  @Test
  public void TestAccessors(){
    setUp();
    assertTrue(base.contains(a));
    assertEquals(base.getLeft(), compL);
    assertEquals(base.getRight(), compR);
    assertEquals(base.getDimension(), 0);
    assertEquals(base.getElem(), a);
    tearDown();
  }

}