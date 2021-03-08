package edu.brown.cs.mramesh4.Graph;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a cache used in the Graph class for A* to improve
 * runtime. It maps Nodes to a list of their neighbors.
 * @param <N> representing type of Node being used in graph.
 * @param <E> representing type of Edge being used in graph.
 */
public class GraphNodeNeighborsCache<N extends GraphNode<N, E>, E extends GraphEdge<N, E>> {

  private LoadingCache<N, List<N>> cache;

  /**
   * In the constructor for this class, we initialize our cache.
   */
  public GraphNodeNeighborsCache() {
    cache = CacheBuilder.newBuilder()
      .refreshAfterWrite(2, TimeUnit.SECONDS)
      .build(new CacheLoader<N, List<N>>() {
        @Override
        public List<N> load(N key) throws Exception {
          return key.neighbors();
        }
      });
  }

  /**
   * This method gets the neighbors of the node passed in from the cache.
   * @param key representing current Node we are
   * @return the neighbors of the Node.
   * @throws ExecutionException if our cache cannot execute the query
   */
  public List<N> getNode(N key) throws ExecutionException {
    return cache.get(key);
  }

}
