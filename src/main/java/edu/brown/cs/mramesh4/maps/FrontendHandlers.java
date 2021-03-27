package edu.brown.cs.mramesh4.maps;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.brown.cs.mramesh4.stars.Star;
import edu.brown.cs.mramesh4.stars.StarsLogic;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;
import com.google.gson.Gson;

/**
 * This class stores all the front end handlers we need for our routes
 * in the Main class.
 */
public class FrontendHandlers {

  private static StarsLogic db;
  private static MapsLogic mapsLogic;
  private static CheckinThread thread;
  private static Gson gson;

  /**
   * Initializes instance variables from Main class.
   * @param dB representing StarsLogic instance
   * @param mL representing MapsLogic instance
   * @param t representing CheckinThread instance
   * @param g representing Gson intance
   */
  public FrontendHandlers(StarsLogic dB, MapsLogic mL, CheckinThread t, Gson g) {
    db = dB;
    mapsLogic = mL;
    thread = t;
    gson = g;
  }

  /**
   * Handle requests to the front page of our Stars website.
   */
  public static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String ret = "No results to display";
      if (db.getStarList().size() == 0) {
        ret = "Please load a file in the command line";
      }
      Map<String, Object> variables = ImmutableMap.of("title",
              "Stars: Query the database", "results", ret);
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  public static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * Handles the radius call on the gui.
   */
  public static class RadiusHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String cmd = qm.value("text2");
      String ret = "No results to display";
      if (db.getStarList().size() == 0) {
        ret = "Please load a file in the command line";
      } else if (cmd == null || cmd.equals("")) {
        ret = "Please input a valid answer";
      } else {
        String neighbs = qm.value("radius_switched");
        String start = "naive_radius";
        if (neighbs != null) {
          start = "radius";
        }
        String cmd2 = start.concat(" ").concat(cmd);
        String[] coms = cmd2.split(" ");
        List<Star> stars = db.radiusRet(cmd2, coms);

        if (db.getStarList().size() == 0) {
          ret = "Please load a file in the command line";
        }
        if (coms[0].toUpperCase().equals("NAIVE_NEIGHBORS")
                || coms[0].toUpperCase().equals("NEIGHBORS")) {
          ret = "Please use the neighbors box for this action";
        } else {
          if (db.getStarList().size() == 0) {
            ret = "Please load a file in the command line";
          }
          if (stars != null && !stars.isEmpty()) {
            ret = "The stars within the radius: " + coms[1] + " are";
            ret += "<br> <br>";
            ret += "<table id=\"table\">"
                    + "<tr> <th> Match # </th> <th> Name </th>"
                    + "<th> ID </th> <th> X </th> <th> Y </th> <th> Z </th> </tr>";
            for (int i = 0; i < stars.size(); i++) {
              Star curr = stars.get(i);
              ret += "<tr><td>" + Integer.toString(i + 1) + "</td>" + "<td>" + curr.getName()
                      + "</td>" + "<td>" + curr.getStarID() + "</td>"
                      + "<td>" + curr.getX() + "</td>"
                      + "<td>" + curr.getY() + "</td>"
                      + "<td>" + curr.getZ() + "</td>";
              ret += "</tr>";
            }
            ret += "</table>";
          } else {
            ret = "No stars found in the search";
          }
        }
      }
      Map<String, Object> variables = ImmutableMap.of("title",
              "Stars: Query the database", "results", ret);
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * Handles the neighbors call on the GUI.
   */
  public static class NeighborsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String cmd = qm.value("text");
      String ret = "No results to display";
      if (db.getStarList().size() == 0) {
        ret = "Please load a file in the command line";
      } else if (cmd == null || cmd.equals("")) {
        ret = "Please input a valid answer";
      } else {
        String neighbs = qm.value("neighbors_switched");
        String start = "naive_neighbors";
        if (neighbs != null) {
          start = "neighbors";
        }
        String cmd2 = start.concat(" ").concat(cmd);
        String[] coms = cmd2.split(" ");
        List<Star> stars = db.neighborsRet(cmd2, coms);
        if (coms[0].toUpperCase().equals("NAIVE_RADIUS")
                || coms[0].toUpperCase().equals("RADIUS")) {
          ret = "Please use the radius box for this action";
        } else {
          if (stars != null && !stars.isEmpty()) {
            ret = "The " + Integer.toString(stars.size()) + " nearest neighbors are:";
            //
            ret += "<br> <br>";
            ret += "<table id=\"table\">"
                    + "<tr> <th> Match # </th> <th> Name </th>"
                    + "<th> ID </th> <th> X </th> <th> Y </th> <th> Z </th> </tr>";
            for (int i = 0; i < stars.size(); i++) {
              Star curr = stars.get(i);
              ret += "<tr><td>" + Integer.toString(i + 1) + "</td>"
                      + "<td>" + curr.getName() + "</td>"
                      + "<td>" + curr.getStarID() + "</td>"
                      + "<td>" + curr.getX() + "</td>" + "<td>" + curr.getY()
                      + "</td>" + "<td>" + curr.getZ() + "</td>";
              ret += "</tr>";
            }
            ret += "</table>";
          } else {
            ret = "No stars found in the search";
          }
        }
      }
      Map<String, Object> variables = ImmutableMap.of("title",
              "Stars: Query the database", "results", ret);
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * Handles route call on the GUI.
   */
  public static class RouteHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

      JSONObject data = new JSONObject(request.body());
      double sLat = data.getDouble("srclat");
      sLat = sLat + 1.0;
      Map<String, Double> variables = ImmutableMap.of("route", sLat);
      return gson.toJson(variables);
    }
  }

  /**
   * Gets all of the ways and their start/end coordinates within bounding box sent from front end.
   */
  public static class WayHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      double minLatit = data.getDouble("minLat");
      double minLong = data.getDouble("minLon");
      double maxLatit = data.getDouble("maxLat");
      double maxLong = data.getDouble("maxLon");
      String[] command = {"ways", Double.toString(maxLatit), Double.toString(minLong),
              Double.toString(minLatit), Double.toString(maxLong)};
      HashMap<String, Object> map = mapsLogic.run(command);
      Map<String, Object> variables = ImmutableMap.of("way", map);
      return gson.toJson(variables);
    }
  }

  /**
   * Sets the initial map to be displayed on frontend.
   * Returns hash map of ways corresponding to their start/end coordinates.
   */
  public static class InitialMapHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      String[] wayCommand = {"ways", "41.82953", "-71.40729", "41.82433", "-71.39572"};
      HashMap<String, Object> map = mapsLogic.run(wayCommand);
      Map<String, Object> variables = ImmutableMap.of("map", map);
      return gson.toJson(variables);
    }
  }

  /**
   * Finds shortest route between two coordinate points on the map.
   */
  public static class ShortestRouteHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      double startLon = data.getDouble("startLon");
      double startLat = data.getDouble("startLat");
      double endLon = data.getDouble("endLon");
      double endLat = data.getDouble("endLat");
      String[] command = {"route", Double.toString(startLon), Double.toString(startLat),
              Double.toString(endLon), Double.toString(endLat)};
      HashMap<String, Object> map = mapsLogic.run(command);
      Map<String, Object> variables = ImmutableMap.of("shortestRoute", map);
      return gson.toJson(variables);
    }
  }

  /**
   * Finds the intersection lat/lon of two streets.
   */
  public static class IntersectionHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String startLon = '"' + data.getString("startLon") + '"';
      String startLat = '"' + data.getString("startLat") + '"';
      WayNodes nodeOne = mapsLogic.getWayNodesAtIntersection(startLon, startLat);
      String[] nodeOneInfo = {Double.toString(nodeOne.getCoordinate(0)),
              Double.toString(nodeOne.getCoordinate(1))};
      HashMap<String, Object> map = new HashMap<>();
      map.put(nodeOne.getId(), nodeOneInfo);
      Map<String, Object> variables = ImmutableMap.of("intersection", map);
      return gson.toJson(variables);
    }
  }

  /**
   * Gets nearest neighbors.
   */
  public static class NearestHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      double nearestLat = data.getDouble("nearLat");
      double nearestLon = data.getDouble("nearLon");
      String[] command = {"nearest", Double.toString(nearestLat), Double.toString(nearestLon)};
      HashMap<String, Object> map = mapsLogic.run(command);
      Map<String, Object> variables = ImmutableMap.of("nearest", map);
      return gson.toJson(variables);
    }
  }
}


