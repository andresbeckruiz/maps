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



public class KDTreeTest{
  private Star a;
  private Star b;
  private Star c;
  private KDNode<Dimensional> compL;
  private KDNode<Dimensional> compR;
  private KDNode<Dimensional> base;
  private ArrayList<Star> list;
  private KDTree<Star> tree;


  @Before
  public void setUp() {
    a = new Star("0", "0", "1.0", "2.0", "3.0");
    b = new Star("2", "2", "2.0", "3.0", "4.0");
    c = new Star("3", "3", "3.0", "3.0", "4.0");
    list = new ArrayList<Star>();
    list.add(a);
    list.add(b);
    list.add(c);
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
    list = null;
  }

  @Test
  public void TestBuild(){
    setUp();
    tree = new KDTree<Star>(list, 3);
    KDTree<Star> tree2 = new KDTree<Star>(list,3);
    assertFalse(tree.isEmpty());
    assertEquals(b, tree.getRoot().getElem());
    assertEquals(b, tree2.getRoot().getElem());
    assertTrue(tree.getRoot().contains(b));
    assertFalse(tree.getRoot().contains(a));
    assertFalse(tree.getRoot().contains(c));
    tearDown();
  }

 @Test(expected = IllegalArgumentException.class)
  public void exceptionCheckingforCons(){
    KDTree<Star> tree2 = new KDTree<Star>(null,3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void exceptionCheckingforCons2(){
    KDTree<Star> tree2 = new KDTree<Star>(list, -1);
    KDTree<Star> tree3 = new KDTree<Star>(list, 0);
  }



}