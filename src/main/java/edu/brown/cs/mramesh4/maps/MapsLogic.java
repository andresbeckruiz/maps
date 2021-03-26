package edu.brown.cs.mramesh4.maps;

import edu.brown.cs.mramesh4.Graph.Graph;
import edu.brown.cs.mramesh4.KDTree.KDTree;
import edu.brown.cs.mramesh4.stars.ActionMethod;
import edu.brown.cs.mramesh4.stars.IllegalArgumentException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * This class represents all of the logic we need for Maps 1 + 2. It handles all finding which
 * command to call, getting a connection to the database, and calling commands and the necessary
 * helper methods to complete them. It also handles parsing the output to print in the terminal.
 */
public class MapsLogic implements ActionMethod<String> {

  private List<WayNodes> wayNodeList;
  private KDTree<WayNodes> wayNodesTree;
  private Connection conn = null;
  private Graph<WayNodes, Way> graph = new Graph<>();
  private WayNodeCache wnc;
  private HashMap<String, WayNodes> wayNodeCache = new HashMap<>();
  private HashMap<String, Object> frontendReturn;
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;
  private static final int TEN = 10;
  private static final int ELEVEN = 11;


  /**
   * In the constructor of MapsLogic, we initailize our list instance variable which holds
   * all the nodes in the database.
   */
  public MapsLogic() {
    wayNodeList = new ArrayList<WayNodes>();
    frontendReturn = new HashMap<>();
  }

  /**
   * Takes in a command from the command line and executes it.
   * @param coms String from the command line
   */
  @Override
  public HashMap<String, Object> run(String[] coms) {
    //assuming the length > 0
    frontendReturn = new HashMap<>();
    if (coms.length > 0) {
      //run regardless of uppercase and trimmed space
      switch (coms[0]) {
        case "map":
          this.map(coms);
          break;
        case "ways":
          this.ways(coms);
          break;
        case "nearest":
          this.nearest(coms);
          break;
        case "route":
          this.route(coms);
          break;
        default:
          System.err.println("ERROR: You entered" + coms[0] + "Please enter a valid command");
          break;
      }
    }
    return frontendReturn;
  }

  /**
   * Loads the map database and builds our KDTree.
   * @param coms whitesace separated list of arguments.
   */
  public void map(String[] coms) {
    //checking that command has correct number of args
    if (coms.length != 2) {
      System.err.println("ERROR: Incorrect number or args provided for map command");
      return;
    }
    File file = new File(coms[1]);
    //check to see if database file exists
    if (!file.exists()) {
      System.err.println("ERROR: File does not exist");
      //make sure file is valid
      return;
    }
    try {
      Class.forName("org.sqlite.JDBC");
      String pathToDB = "jdbc:sqlite:" + coms[1];
      //create a connection
      conn = DriverManager.getConnection(pathToDB);
      String[] tables = new String[]{"node", "way"};
      String[] expectedCols = new String[]{"id", "latitude", "longitude"};
      //makes sure our table we are reading from exists
      if (isValidTable(tables)) {
        PreparedStatement prep;
        //get all of the nodes table
      //  prep = conn.prepareStatement(
      //  prep = conn.prepareStatement(
        //    "SELECT * from node");
        prep = conn.prepareStatement(
            "SELECT DISTINCT node.* FROM 'node', 'way' WHERE"
                + "((way.start = node.id) OR (way.end = node.id))"
                + " AND (way.type != '') AND (way.type != ?);");
        prep.setString(1, "unclassified");
        ResultSet rs = prep.executeQuery();
        wayNodeList.clear();
        //makes sure our table is correctly columned
        if (!isValidQuery(rs, expectedCols)) {
          System.err.println("ERROR: Table is ill-formated");
          return;
        } else {
          while (rs.next()) {
            //create a Node object for each entry in table
            String id = rs.getString(1);
            double longitude = rs.getDouble(2);
            double latitude = rs.getDouble(3);
            //we want to make sure this is valid and that the table is well formatted. If
            //it isn't we throw an error.
            WayNodes wayNode = new WayNodes(id, longitude, latitude, conn);
            wayNodeList.add(wayNode);
          }
          rs.close();
          //create KDTree with the wayNode List we creaed
          wayNodesTree = new KDTree<WayNodes>(wayNodeList, 2);
          graph = new Graph<WayNodes, Way>();
          wnc = new WayNodeCache(this.conn);
          wayNodeCache = new HashMap<>();
          System.out.println("map set to " + coms[1]);
        }
      } else {
        System.err.println("ERROR: Table is ill-formated");
      }
    } catch (SQLException e) {
      System.err.println("ERROR: Error connecting to database");
      return;
    } catch (ClassNotFoundException e) {
      System.err.println("ERROR: Invalid database class");
      return;
    } catch (IllegalArgumentException e) {
      System.err.println("ERROR: Ill-formatted database");
      return;
    }
  }

  /**
   * Accessor method for wayNodeList.
   * @return list of waynodes
   */
  public List<WayNodes> getWayNodeList() {
    return this.wayNodeList;
  }

  /**
   * Tells us if a given table name exists in our connection.
   * @param tablename name of table to search
   * @return a boolean if table exists
   */
  public boolean isValidTable(String[] tablename) {
    try {
      for (int i = 0; i < tablename.length; i++) {
        boolean exist = conn.getMetaData().getTables(null,
            null, tablename[i], null).next();
        if (!exist) {
          return false;
        }
      }
      return true;
    } catch (SQLException e) {
      System.out.println("Table SQL Exception");
      return false;
    }
  }

  /**
   * Tells us if the given query of our table has the rows we expected.
   * @param rs: ResultSet Query
   * @param expectedCols: Columns we are expecting
   * @return a boolean representing if query is valid
   */
  private boolean isValidQuery(ResultSet rs, String[] expectedCols) {
    try {
      //get the metadata of the result set
      ResultSetMetaData rsmd = rs.getMetaData();
      //make sure cols match up to what we want
      for (int i = 1; i <= expectedCols.length; i++) {
        if (expectedCols[i - 1].equals(rsmd.getColumnName(i))) {
          continue;
        } else {
          return false;
        }
      }
      return true;
    } catch (SQLException e) {
      System.out.println("SQL Exception");
      return false;
    }
  }

  /**
   * This is the method to run the ways command, prints all ways within a given
   * area.
   * @param coms input to command split by white space in an array
   */
  public void ways(String[] coms) {
    if (wayNodesTree == null || wayNodeList == null) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return;
    }
    //make sure we were passed in right arguments
    if (wayNodesTree.isEmpty() || wayNodeList.isEmpty()) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return;
    } else if (coms.length != 5) {
      System.err.println("ERROR: Incorrect number of args provided for ways command");
      return;
    } else {
      try {
        //make sure we were passed in doubles
        Double latMin = Double.parseDouble(coms[3]);
        Double lonMin = Double.parseDouble(coms[2]);
        Double latMax = Double.parseDouble(coms[1]);
        Double lonMax = Double.parseDouble(coms[4]);
        if (latMax < latMin || lonMax < lonMin) {
          System.err.println("ERROR: Please make sure the values for "
              + "<lat2><long2> are less than <lat1><long1>");
          return;
        }
        PreparedStatement prep;
//        prep = conn.prepareStatement("SELECT w.id, w.start, w.end FROM node AS n JOIN way "
//                + "AS w WHERE ( (n.id = w.end) OR (n.id = w.start) ) AND (n.latitude >= ?) AND "
//                + "(n.latitude <= ?) AND (n.longitude >= ?) AND (n.longitude <= ?) "
//                + "ORDER BY w.id");
//        // group by -- or distinct
//        prep.setDouble(1, latMin);
//        prep.setDouble(2, latMax);
//        prep.setDouble(3, lonMin);
//        prep.setDouble(4, lonMax);
//        ResultSet rs = prep.executeQuery();
//        LinkedHashSet<String> toPrint = new LinkedHashSet<>();
//        //HashMap<String, String> newValue;
//        while (rs.next()) {
//          String wayId = rs.getString(1);
//          String startId = rs.getString(2);
//          String endId = rs.getString(3);
//          toPrint.add(wayId);
//        //  newValue = new HashMap<>();
//          String[] newValue = new String[4];
//
//          PreparedStatement startPrep = conn.prepareStatement(
//                  "SELECT node.latitude, node.longitude FROM node JOIN way "
//                  + "WHERE node.id = ?");
//          startPrep.setString(1, startId);
//          ResultSet startRs = startPrep.executeQuery();
//        //  newValue.add("sLat", startRs.getString(1));
//          newValue[0] = startRs.getString(1);
//        //  newValue.put("sLot", startRs.getString(2));
//          newValue[1] = startRs.getString(2);
//          PreparedStatement endPrep = conn.prepareStatement(
//                  "SELECT node.latitude, node.longitude FROM node JOIN way "
//                  + "WHERE node.id = ?");
//          endPrep.setString(1, endId);
//          ResultSet endRs = endPrep.executeQuery();
//          // newValue.put("eLat", endRs.getString(1));
//          newValue[2] = endRs.getString(1);
//          //newValue.put("eLot", endRs.getString(2));
//          newValue[3] = endRs.getString(2);
//          frontendReturn.put(wayId, newValue);
//        }
//        rs.close();
        prep = conn.prepareStatement("WITH startN AS (SELECT id AS startId, latitude AS "
            + "startLat, longitude AS startLong FROM node), endN AS (SELECT id AS "
            + "endId, latitude AS endLat, longitude AS endLong FROM node), "
            + "relativeN AS (SELECT id as nodeId FROM node WHERE CAST(latitude AS DOUBLE)"
            + "BETWEEN ? AND ? AND CAST(longitude AS DOUBLE) BETWEEN ? and ?),"
            + "resultingWays AS (SELECT * FROM way WHERE start IN relativeN OR end IN "
            + "relativeN) SELECT * FROM resultingWays AS rw JOIN startN AS sn ON "
            + "rw.start == sn.startId JOIN endN AS en ON rw.end == en.endId ");
        // group by -- or distinct
        prep.setDouble(1, latMin);
        prep.setDouble(2, latMax);
        prep.setDouble(3, lonMin);
        prep.setDouble(4, lonMax);
        ResultSet rs = prep.executeQuery();
        LinkedHashSet<String> toPrint = new LinkedHashSet<>();
        //HashMap<String, String> newValue;
        while (rs.next()) {
          String wayId = rs.getString(1);
          toPrint.add(wayId);
          String[] frontEndInfo = new String[5];
          frontEndInfo[0] = rs.getString(SEVEN);
          frontEndInfo[1] = rs.getString(EIGHT);
          frontEndInfo[2] = rs.getString(TEN);
          frontEndInfo[3] = rs.getString(ELEVEN);
          frontEndInfo[4] = rs.getString(3);
          frontendReturn.put(wayId, frontEndInfo);
        }
        rs.close();
        for (String print: toPrint) {
          System.out.println(print);
        }
      } catch (NumberFormatException e) {
        //if the number is not an integer, throw an exception
        System.err.println("ERROR: Please supply a valid double");
        return;
      } catch (SQLException e) {
        System.err.println("ERROR: Error parsing your input");
        e.printStackTrace();
        return;
      }
    }
  }

  /**
   * This is the implementation of the nearest method.
   * @param coms an array of whitespace split commands
   * @return the nearest Way Node
   */
  public WayNodes nearest(String[] coms) {
    if (wayNodesTree == null || wayNodeList == null) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return null;
    }
    if (wayNodesTree.isEmpty() || wayNodeList.isEmpty()) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return null;
    } else if (coms.length != 3) {
      System.err.println("ERROR: Incorrect number or args provided for nearest command");
      return null;
    } else {
      try {
        Double lat = Double.parseDouble(coms[1]);
        Double longit = Double.parseDouble(coms[2]);
        //check helper method- creates new WayNode
        WayNodes target = this.getWayNodeByLatLong(lat, longit);
        //use our KDTree search method to find nearest neighbor
        List<WayNodes> ret = wayNodesTree.nearestNeighbors(1, target, false);
        WayNodes nearest = ret.get(0);
        wayNodeCache.put(nearest.getId(), nearest);

        String[] frontEndInfo = new String[2];
        frontEndInfo[0] = Double.toString(nearest.getLat());
        frontEndInfo[1] = Double.toString(nearest.getLong());
        frontendReturn.put(nearest.getId(), frontEndInfo);

        System.out.println(nearest.getInfo("id"));
        return nearest;
      } catch (NumberFormatException e) {
        System.err.println("ERROR: Please supply a valid double");
        return null;
      }
    }
  }

  /**
   * This is the implementation of the route method.
   * @param coms an array of whitespace split commands
   */
  private void route(String[] coms) {
    //check if we haven't loaded data
    if (wayNodesTree == null || wayNodeList == null) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return;
    }
    if (wayNodesTree.isEmpty() || wayNodeList.isEmpty()) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return;
    }
    if (coms.length != 5) {
      System.err.println("ERROR: Incorrect number of args provided for route command");
      return;
    }
    WayNodes start;
    WayNodes end;
    graph = new Graph<>();
    //street names inputted
    if (!this.isClassInt(coms)) {
      for (int i = 1; i < coms.length; i++) {
        //checking that each argument is a nonempty string
        if (!(coms[i] instanceof String && coms[i] != "")) {
          System.err.println("ERROR: Street names must be nonempty strings");
          return;
        }
      }
      //check helper methods
      start = getWayNodesAtIntersection(coms[1], coms[2]);
      end = getWayNodesAtIntersection(coms[3], coms[4]);
      //if intersection not found
      if (start == null && end == null) {
        System.err.println("ERROR: No intersection found: Both set of streets don't intersect");
        return;
        //if starting node and ending node are equal, can't find a path
      } else if (start == null) {
        System.err.println("ERROR: No intersection found: The first two streets don't intersect");
        return;
      } else if (end == null) {
        System.err.println("ERROR: No intersection found: The last two streets don't intersect");
        return;
      } else if (start.equals(end)) {
        System.out.println("No route found- start and end nodes are the same");
        return;
      } else {
        String startID = start.getId();
        String endID = end.getId();
        wayNodeCache.put(startID, start);
        wayNodeCache.put(endID, end);
        end = graph.aStar(start, end);
        //no path exists
        if (end == null) {
          System.out.println(startID + " -/- " + endID);
          return;
        } else {
          //check helper method, prints to terminal
          this.printRoute(start, end);
        }
      }
      //lat and long inputted
    } else {
      try {
        double lat1 = Double.parseDouble(coms[1]);
        double long1 = Double.parseDouble(coms[2]);
        WayNodes startTarget = this.getWayNodeByLatLong(lat1, long1);
        //find start node using nearestNeighbors
        List<WayNodes> startRet = wayNodesTree.nearestNeighbors(1, startTarget, false);
        start = startRet.get(0);
        double lat2 = Double.parseDouble(coms[3]);
        double long2 = Double.parseDouble(coms[4]);
        WayNodes endTarget = this.getWayNodeByLatLong(lat2, long2);
        //find ending node using nearestNeighbors
        List<WayNodes> endRet = wayNodesTree.nearestNeighbors(1, endTarget, false);
        end = endRet.get(0);
        //if start and end nodes are same, want to print nothing
        if (start.equals(end)) {
          System.out.println("No route found- start and end nodes are the same");
          return;
        }
        String startID = start.getId();
        String endID = end.getId();
        end = graph.aStar(start, end);
        //no path exists
        if (end == null) {
          System.out.println(startID + " -/- " + endID);
          return;
        } else {
          //check helper method, prints to terminal
          this.printRoute(start, end);
        }
      } catch (NumberFormatException e) {
        System.err.println("ERROR: Latitudes and longitudes must be ints or doubles");
        return;
      }

    }
  }

  /**.
   * Takes in the command line for naive_neighbors and radius
   * and sees if second arg is an int
   * @param scale string[] of commands
   * @return boolean representing if it takes an int (true if int)
   */
  public boolean isClassInt(String[] scale) {
    try {
      Double.parseDouble(scale[1]);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }


  /**
   * Helper method to print route path to terminal.
   * @param start representing starting WayNode
   * @param end representing ending WayNode
   */
  private void printRoute(WayNodes start, WayNodes end) {
    List<Way> route = new ArrayList<>();
    WayNodes currNode = end;
    //looping through the route, adding ways to list
    while (currNode.getId() != start.getId()) {
      String[] frontEndInfo = new String[5];
      Way currWay = currNode.getFrom();
      frontEndInfo[0] = Double.toString(currWay.getFrom().getLat());
      frontEndInfo[1] = Double.toString(currWay.getFrom().getLong());
      frontEndInfo[2] = Double.toString(currWay.getTo().getLat());
      frontEndInfo[3] = Double.toString(currWay.getTo().getLong());
      //need to check ways that are type "route" and color them red!
      frontEndInfo[4] = "route";
      frontendReturn.put(currWay.getId(), frontEndInfo);
      route.add(currWay);
      currNode = currWay.getFrom();
    }
    for (int i = route.size() - 1; i >= 0; i--) {
      //formatting string to print in terminal
      System.out.println(route.get(i).getFrom().getId() + " -> " + route.get(i).getTo().getId()
          + " : " + route.get(i).getId());
    }
  }

  /**
   * Method to create a WayNode for nearest search, based on specific lat-long pair.
   * @param lat latitude of the point
   * @param longit longitude of the point
   * @return a WayNodes object with those points
   */
  public WayNodes getWayNodeByLatLong(double lat, double longit) {
    for (WayNodes node: wayNodeList) {
      if (node.getLat() == lat && node.getLong() == longit) {
        return node;
      }
    }
    return new WayNodes("temp", lat, longit, this.conn);
  }

  /**
   * Gets a waynode by the string id name using the WayNodeCache.
   * @param s1 the id name
   * @return WayNode found by ID
   */
  public WayNodes getIDByString(String s1) {
    if (s1 == null || s1.equals("")) {
      return null;
    } else {
      try {
        if (wayNodeCache.containsKey(s1)) {
          return wayNodeCache.get(s1);
        } else {
          PreparedStatement getNode = conn.prepareStatement("SELECT * FROM node WHERE node.id = ?");
          getNode.setString(1, s1);
          ResultSet answer1 = getNode.executeQuery();
          WayNodes ws = null;
          //instantiate WayNode object
          while (answer1.next()) {
            ws = new WayNodes(answer1.getString(1),
                answer1.getDouble(2), answer1.getDouble(3), conn);
            wayNodeCache.put(ws.getId(), ws);
          }
          return ws;
        }
      } catch (SQLException e) {
        return null;
      }
    }
  }

  /**
   * Returns a wayNode at a given intersection street using SQL queries.
   * @param s1 street 1
   * @param s2 street 2
   * @return node at intersection of streets (or null if none exists)
   */
  public WayNodes getWayNodesAtIntersection(String s1, String s2) {
    try {
      PreparedStatement getWay1 = conn.prepareStatement("WITH street1 AS "
          + "(SELECT start, end FROM way WHERE name = ?), "
          + "cross1 as (SELECT start, end FROM way where name = ?) , "
          + "streetNodes AS (SELECT start AS nodeID FROM street1 UNION SELECT "
          + "end AS nodeID FROM street1)"
          + ", crossNodes AS (SELECT start AS nodeID FROM cross1 UNION SELECT "
          + "end as nodeID from cross1) "
          + "SELECT * FROM streetNodes INNER JOIN crossNodes ON "
          + "streetNodes.nodeID = crossNodes.nodeID");
      getWay1.setString(1, s1.substring(1, s1.length() - 1));
      getWay1.setString(2, s2.substring(1, s2.length() - 1));
      ResultSet answer1 = getWay1.executeQuery();
      while (answer1.next()) {
        String node = answer1.getString("nodeID");
        answer1.close();
        return this.getIDByString(node);
      }
    } catch (SQLException e) {
      return null;
    }
    return null;
  }
}
