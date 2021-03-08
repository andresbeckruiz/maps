package edu.brown.cs.mramesh4.maps;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a cache used in the MapsLogic class to improve runtime
 * for searching WayNodes by ID. The cache maps IDS to Nodes in the database.
 */
public class WayNodeCache {

  private LoadingCache<String, edu.brown.cs.mramesh4.maps.WayNodes> cache;

  /**
   * In the constructor of this class, we initialize our cache.
   * @param conn representing database connection.
   */
  public WayNodeCache(Connection conn) {
    cache = CacheBuilder.newBuilder()
      .refreshAfterWrite(2, TimeUnit.SECONDS)
      .build(new CacheLoader<String, edu.brown.cs.mramesh4.maps.WayNodes>() {
        @Override
        public edu.brown.cs.mramesh4.maps.WayNodes load(String key) throws Exception {
          PreparedStatement getNode = conn.prepareStatement("SELECT * FROM node WHERE node.id = ?");
          getNode.setString(1, key);
          ResultSet answer1 = getNode.executeQuery();
          edu.brown.cs.mramesh4.maps.WayNodes ws = null;
          //instantiate WayNode object
          while (answer1.next()) {
            ws = new edu.brown.cs.mramesh4.maps.WayNodes(answer1.getString(1),
                    answer1.getDouble(2), answer1.getDouble(3), conn);
          }
          return ws;
        }
      });
  }

  /**
   * This method finds the WayNode in our cache corresponding to the ID
   * we pass in.
   * @param key representing ID of WayNode we are looking for
   * @return representing WayNode found
   * @throws ExecutionException if our cache cannot execute the query
   */
  public edu.brown.cs.mramesh4.maps.WayNodes getNode(String key) throws ExecutionException {
    return cache.get(key);
  }

}
