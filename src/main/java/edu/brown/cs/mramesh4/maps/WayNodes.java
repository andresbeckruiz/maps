package edu.brown.cs.mramesh4.maps;

import edu.brown.cs.mramesh4.Dimensional.Dimensional;
import edu.brown.cs.mramesh4.Graph.GraphNode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Node object from the database. It implements
 * GraphNode so that it can be used in our generic Graph class.
 */
public class WayNodes implements Dimensional, GraphNode<WayNodes, Way> {

  private final String id;
  private final double lat;
  private final double longitude;
  private static final double EARTH_RADIUS_IN_KM = 6371;
  private Way from;
  private double distance;
  private double weight;
  private static final double MIN_LNG = -180;
  private static final double MAX_LNG = 180;
  private static final double WRAP_LNG = MAX_LNG - MIN_LNG;
  private Connection conn;

  /**
   * This is a constructor for a wayNode, it takes in the following information
   * and initializes all of our instance variables. We also calculate the
   * real longitutde.
   * @param id a String representing an id
   * @param lat a double representing latitude
   * @param longit a double representing longitude
   * @param conn representing a connection to the current database
   */
  public WayNodes(String id, double lat, double longit, Connection conn) {
    this.id = id;
    this.lat = lat;
    this.longitude = longit;
    //doesn't have a from Way at first
    this.from = null;
    this.distance = Double.MAX_VALUE;
    this.weight = Double.MAX_VALUE;
    this.conn = conn;
  }


  /**
   * Gets the specific coordinate on a given pair.
   * @param index the int index
   * @return a double representing the current coordinate
   */
  public double getCoordinate(int index) {
    if (index == 0) {
      return this.lat;
    } else {
      return this.longitude;
    }
  }

  /**
   * Returns number of coordinates to consider.
   * @return 2, representing number of dimensions
   */
  public int numDimensions() {
    return 2;
  }

  /**
   * Gets the distance between two WayNode objects.
   * @param a the waynode object to compare distance with
   * @return double representing euclidean distance between the two objects
   */
  public double distanceBetween(Dimensional a) {
    if (a instanceof WayNodes) {
      double aX = a.getCoordinate(0);
      double aY = a.getCoordinate(1);
      return Math.sqrt(Math.pow(Math.abs((aX - this.lat)), 2)
        + Math.pow(Math.abs((aY - this.longitude)), 2));
    } else {
      return -1;
    }
  }

  /**
   * Fufills dimensional interface, gets the distance at a specific coordinate.
   * @param dimension A dimension to check
   * @param a the object to compare with
   * @return a double along the axis
   */
  public double getAxisDistance(int dimension, Dimensional a) {
    return this.getCoordinate(dimension) - a.getCoordinate(dimension);
  }

  /**
   * A parameter of information to check.
   * @param a representing an optional query of which string to return.
   * @return the id string, the only information we need to know
   */
  public String getInfo(String a) {
    return this.id;
  }

  /**
   * An accessor method for id.
   * @return String representing id
   */
  public String getId() {
    return this.id;
  }

  /**
   * An accessor method for Latitude.
   * @return double representing latitude
   */
  public double getLat() {
    return this.lat;
  }

  /**
   * An accessor method for Longitude.
   * @return double representing longitude;
   */
  public double getLong() {
    return this.longitude;
  }


  /**
   * An accessor for the from Way.
   * @return a Way representing from Way
   */
  @Override
  public Way getFrom() {
    return from;
  }

  /**
   * A setter method to set the from edge.
   * @param e representing Way to set as from
   */
  @Override
  public void setFrom(Way e) {
    from = e;
  }

  /**
   * Setter for distance from startNode in graph.
   * @param d a double representing distance
   */
  @Override
  public void setCurrentDistance(double d) {
    distance = d;
  }

  /**
   * Getter for distance from startNode in graph.
   * @return double representing distance.
   */
  @Override
  public double getCurrentDistance() {
    return distance;
  }

  /**
   * This method checks if two WayNodes are equal by checking
   * if the ID's are equal, since all IDS are guaranteed to be unique.
   * @param n representing the WayNode to compare
   * @return a boolean representing if the Nodes are equal (true if equal)
   */
  @Override
  public boolean equals(WayNodes n) {
    return this.id.equals(n.getId());
  }

  /**
   * This method returns the name of the WayNode, which is the ID.
   * @return String representing the ID of the current WayNode
   */
  @Override
  public String getName() {
    return this.id;
  }

  /**
   * This method returns a list of WayNodes that are neighbors of the
   * current Node. We find these neighbors by querying the database.
   * @return a List of WayNodes representing the neighbors.
   */
  @Override
  public List<WayNodes> neighbors() {
    try {
      PreparedStatement prep;
      //don't want to return Nodes that we can't reach (untraversable)
      prep = conn.prepareStatement("SELECT * FROM node AS n JOIN way AS w WHERE w.start == ? "
              + "AND (w.type != 'unclassified' AND w.type != '')  AND n.id == w.end");
      prep.setString(1, this.id);
      ResultSet rs = prep.executeQuery();
      List<WayNodes> ret = new ArrayList<>();
      while (rs.next()) {
        String idNext = rs.getString(1);
        double latitudeNext = rs.getDouble(2);
        double longitudeNext = rs.getDouble(3);
        //instantiate the neighbors as WayNodes here
        WayNodes next = new WayNodes(idNext, latitudeNext, longitudeNext, this.conn);
        //instantiate the edges connecting neighbors so we can set the from value of neighbors
        Way edge = new Way(rs.getString(4), this, next);
        next.setFrom(edge);
        ret.add(next);
      }
      return ret;
    } catch (SQLException e) {
      System.err.println("ERROR: Error querying the database with SQL");
      return null;
    }
  }

  /**
   * This getter method returns the total weight of Node.
   * @return double representing weight
   */
  @Override
  public double getTotalWeight() {
    return weight;
  }

  /**
   * This setter method sets the total weight of Node.
   * @param weightArg representing weight to set
   */
  @Override
  public void setTotalWeight(double weightArg) {
    weight = weightArg;
  }

  /**
   * This method calculates the distance to the goal using Haversine distance.
   * @param goal representing goal node
   * @return double representing distance.
   */
  @Override
  public double toGoal(WayNodes goal) {
    double thisLat = Math.toRadians(lat);
    double thisLon = Math.toRadians(longitude);
    double goalLat = Math.toRadians(goal.getLat());
    double goalLon = Math.toRadians(goal.getLong());
    /**
     * Haversine formula: I got this from
     * https://www.geeksforgeeks.org/program-distance-two-points-earth/
     */
    double dlon = goalLon - thisLon;
    double dlat = goalLat - thisLat;
    double havernmath = Math.pow(Math.sin(dlat / 2), 2)
            + Math.cos(thisLat) * Math.cos(goalLat)
            * Math.pow(Math.sin(dlon / 2), 2);
    double finalanswer = 2 * Math.asin(Math.sqrt(havernmath));
    // Radius of earth in kilometers. Use 3956 for miles
    return (finalanswer * EARTH_RADIUS_IN_KM);
  }

}
