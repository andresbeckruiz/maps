package edu.brown.cs.mramesh4.maps1;

import edu.brown.cs.mramesh4.Graph.Graph;
import edu.brown.cs.mramesh4.maps.WayNodes;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.Assert.assertEquals;
import edu.brown.cs.mramesh4.maps.Way;

public class GraphTest {

  @Test
  public void aStarSimpleTest1(){
    try {
      Connection conn = DriverManager.getConnection("data/maps/smallMaps.sqlite3");
      WayNodes start = new WayNodes("/n/0", 41.82, -71.4, conn);
      WayNodes end = new WayNodes("/n/5", 41.8206, -71.4003, conn);
      Graph<WayNodes, Way> g = new Graph<WayNodes, Way>();
      WayNodes endNode = g.aStar(start, end);
      //tracing path backwards
      Way endWay = endNode.getFrom();
      WayNodes secondMiddleNode = endWay.getFrom();
      Way middleWay = secondMiddleNode.getFrom();
      WayNodes firstMiddleNode = middleWay.getFrom();
      Way startWay = firstMiddleNode.getFrom();
      WayNodes startNode = startWay.getFrom();
      assertEquals(endNode.getId(), "/n/5");
      assertEquals(endWay.getId(), "/w/4");
      assertEquals(secondMiddleNode.getId(), "/n/2");
      assertEquals(middleWay.getId(), "/w/1");
      assertEquals(firstMiddleNode.getId(), "/n/1");
      assertEquals(startWay.getId(), "/w/0");
      assertEquals(startNode.getId(), "/n/0");
    } catch (SQLException e) {
      return;
    }
  }

  @Test
  public void aStarSimpleTest2(){
    try {
      Connection conn = DriverManager.getConnection("data/maps/smallMaps.sqlite3");
      WayNodes start = new WayNodes("/n/0", 41.82, -71.4, conn);
      WayNodes end2 = new WayNodes("/n/3", 41.82, -71.4003, conn);
      Graph<WayNodes, Way> g = new Graph<WayNodes, Way>();
      WayNodes endNode = g.aStar(start, end2);
      //tracing path backwards
      Way oneWay = endNode.getFrom();
      WayNodes startNode = oneWay.getFrom();
      assertEquals(endNode.getId(), "/n/3");
      assertEquals(oneWay.getId(), "/w/2");
      assertEquals(startNode.getId(), "/n/0");
    } catch (SQLException e) {
      return;
    }
  }


}
