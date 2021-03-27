package edu.brown.cs.mramesh4.maps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import edu.brown.cs.mramesh4.REPL.Reader;
import edu.brown.cs.mramesh4.MockPerson.MockPersonMethod;
import edu.brown.cs.mramesh4.stars.ActionMethod;
import edu.brown.cs.mramesh4.stars.StarsLogic;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
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
  private static CheckinThread thread;

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
    new FrontendHandlers(db, mapsLogic, thread, GSON);
    if (options.has("gui")) {
      // check this
      thread = new CheckinThread();
      thread.start();
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

    Spark.exception(Exception.class, new FrontendHandlers.ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/stars", new FrontendHandlers.FrontHandler(), freeMarker);
    Spark.post("/radius", new FrontendHandlers.RadiusHandler(), freeMarker);
    Spark.post("/neighbors", new FrontendHandlers.NeighborsHandler(), freeMarker);
    Spark.post("/route", new FrontendHandlers.RouteHandler());
    Spark.post("/way", new FrontendHandlers.WayHandler());
    Spark.post("/map", new FrontendHandlers.InitialMapHandler());
    Spark.post("/shortestRoute", new FrontendHandlers.ShortestRouteHandler());
    Spark.post("/nearest", new FrontendHandlers.NearestHandler());
    Spark.post("/intersection", new FrontendHandlers.IntersectionHandler());
    Spark.post("/userCheckin", new UserCheckinHandler());
    Spark.post("/pastCheckins", new PastCheckinsHandler());
  }

  /**
   * Used to store new user data in the database, and sending this data to frontend.
   */
  private static class UserCheckinHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      Map<String, Object> variables = ImmutableMap.of("userCheckin", thread.getLatestCheckins());
      return GSON.toJson(variables);
    }
  }

  /**
   * Returns all past checkins of a given user.
   */
  private static class PastCheckinsHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      Integer id = data.getInt("id");
      ArrayList<Object> checkinsArray = thread.getPastCheckins(id);
      Map<String, Object> variables = ImmutableMap.of("pastCheckins", checkinsArray);
      return GSON.toJson(variables);
    }
  }

}
