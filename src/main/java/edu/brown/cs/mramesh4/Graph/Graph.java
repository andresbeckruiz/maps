package edu.brown.cs.mramesh4.Graph;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

/**
 * This class holds our A* implementation, which we allow to be generic by passing in any node
 * type N that implements GraphNode and any edge that implements GraphEdge.
 * @param <N> representing the type of Node
 * @param <E> representing the type of Edge
 */
public class Graph<N extends GraphNode<N, E>, E extends GraphEdge<N, E>> {

  private GraphNodeNeighborsCache<N, E> neighborscache;

  /**
   * In the constructor for Graph, we initialize our cache instance variable, which we use
   * during the A* algorithm.
   */
  public Graph() {
    neighborscache = new GraphNodeNeighborsCache<N, E>();
  }

  /**
   * This method contians our A* implementation.
   * @param source representing the starting node.
   * @param end representing the ending node of the path
   * @return the ending node, with an updated from value, which allows us to trace
   * the path backwards.
   */
  public N aStar(N source, N end) {
    PriorityQueue<N> pq = new PriorityQueue<>(new GraphNodeComparator<N, E>());
    //current node should have distance 0, add it to PQ
    source.setCurrentDistance(0);
    pq.add(source);
    //checks for visited nodes so we don't readd them
    HashMap<String, N> visited = new HashMap<>();
    visited.put(source.getName(), source);
    while (!pq.isEmpty()) {
      N curr = pq.poll();
      //check if we have reached the end node
      if (curr.equals(end)) {
        return curr;
      }
      List<N> neighbors;
      //trying to get a list of neighbors from our cache
      try {
        neighbors = neighborscache.getNode(curr);
      } catch (ExecutionException e) {
        neighbors = curr.neighbors();
      }
      for (N next : neighbors) {
        Double dist = curr.getCurrentDistance() + next.getFrom().getWeight();
        Double totalWeight = dist + next.toGoal(end);
        //if we have already visited a Node
        if (!visited.containsKey(next.getName())) {
          next.setCurrentDistance(dist);
          next.setTotalWeight(totalWeight);
          visited.put(next.getName(), next);
          pq.add(next);
        } else {
          //checking if we want to update the weight and distance
          if (visited.get(next.getName()).getTotalWeight() > totalWeight) {
            next.setCurrentDistance(dist);
            next.setTotalWeight(totalWeight);
            visited.put(next.getName(), next);
            pq.add(next);
          }
        }
      }
    }
    if (visited.containsKey(end.getName())) {
      return visited.get(end.getName());
    } else {
      //if no path has been found- we haven't reached the end Node
      return null;
    }
  }
}
