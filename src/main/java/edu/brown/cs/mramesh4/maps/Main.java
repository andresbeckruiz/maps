package edu.brown.cs.mramesh4.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.brown.cs.mramesh4.REPL.Reader;
import edu.brown.cs.mramesh4.MockPerson.MockPersonMethod;
import edu.brown.cs.mramesh4.stars.ActionMethod;
import edu.brown.cs.mramesh4.stars.Star;
import edu.brown.cs.mramesh4.stars.StarsLogic;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;
import freemarker.template.Configuration;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;
import com.google.gson.Gson;

/**
 * The Main class of our project. This is where the execution begins.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;


  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;
  private static StarsLogic db;
  private static MapsLogic mapsLogic;
  private static final Gson GSON = new Gson();
  private boolean firstRun;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    db = new StarsLogic();
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);
    MockPersonMethod m = new MockPersonMethod();
    mapsLogic = new MapsLogic();
    HashMap<String, ActionMethod<?>> methods = new HashMap<>();
    methods.put("stars", db);
    methods.put("naive_neighbors", db);
    methods.put("naive_radius", db);
    methods.put("mock", m);
    methods.put("neighbors", db);
    methods.put("radius", db);
    methods.put("map", mapsLogic);
    methods.put("ways", mapsLogic);
    methods.put("nearest", mapsLogic);
    methods.put("route", mapsLogic);
    //loads our Brown map initially
    firstRun = true;
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
    boolean run = true;
    Reader repl = new Reader(methods);
    while (run) {
      if (!repl.read()) {
        run = false;
      }
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });
    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/stars", new FrontHandler(), freeMarker);
    Spark.post("/radius", new RadiusHandler(), freeMarker);
    Spark.post("/neighbors", new NeighborsHandler(), freeMarker);
    Spark.post("/route", new RouteHandler());
    Spark.post("/way", new WayHandler());
    Spark.post("/map", new InitialMapHandler());
    Spark.post("/shortestRoute", new ShortestRouteHandler());
    Spark.post("/nearest", new NearestHandler());
    Spark.post("/intersection", new IntersectionHandler());
  }

  /**
   * Handle requests to the front page of our Stars website.
   */
  private static class FrontHandler implements TemplateViewRoute {
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
  private static class ExceptionPrinter implements ExceptionHandler {
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
  private static class RadiusHandler implements TemplateViewRoute {
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
  private static class NeighborsHandler implements TemplateViewRoute {
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
  private static class RouteHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

      JSONObject data = new JSONObject(request.body());
      double sLat = data.getDouble("srclat");
      sLat = sLat + 1.0;
      Map<String, Double> variables = ImmutableMap.of("route", sLat);
      return GSON.toJson(variables);

    }
  }

  private static class WayHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      // request is what is from user
      JSONObject data = new JSONObject(request.body());
      double minLatit = data.getDouble("minLat");
      double minLong = data.getDouble("minLon");
      double maxLatit = data.getDouble("maxLat");
      double maxLong = data.getDouble("maxLon");
      // call ways method using these variables
      // BUT we cannot return a list of ways back to frontend!
      // we want to send back a dictionary of strings (way ID corresponds to start lat, )
      // make dictionary
      String[] command = {"ways", Double.toString(maxLatit), Double.toString(minLong),
          Double.toString(minLatit), Double.toString(maxLong)};
      HashMap<String, Object> map = mapsLogic.run(command);
      Map<String, Object> variables = ImmutableMap.of("way", map);
      return GSON.toJson(variables);
    }
  }

  private static class InitialMapHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
//      minBoundLat : minLat,
//          minBoundLon : minLon,
//          maxBoundLat : maxLat,
//          maxBoundLon : maxLon,
      String[] wayCommand = {"ways", "41.82953", "-71.40729", "41.82433", "-71.39572"};
      HashMap<String, Object> map = mapsLogic.run(wayCommand);
      Map<String, Object> variables = ImmutableMap.of("map", map);
      return GSON.toJson(variables);
    }
  }

  private static class ShortestRouteHandler implements Route {
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
      return GSON.toJson(variables);
    }
  }

  private static class IntersectionHandler implements Route {
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
      System.out.println(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * Gets nearest neighbors
   */
  private static class NearestHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      double nearestLat = data.getDouble("nearLat");
      double nearestLon = data.getDouble("nearLon");
      String[] command = {"nearest", Double.toString(nearestLat), Double.toString(nearestLon)};
      HashMap<String, Object> map = mapsLogic.run(command);
      Map<String, Object> variables = ImmutableMap.of("nearest", map);
      return GSON.toJson(variables);
    }
  }

}
