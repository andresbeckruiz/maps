package edu.brown.cs.mramesh4.Graph;

import java.util.Comparator;

/**
 * This class is a custom comparator for our priority queue used in the Graph class.
 * It takes in any type of Node that implements GraphNode.
 * @param <N> representing type of Node we want to compare
 * @param <E> representing type of Edge we want to compare
 */
public class GraphNodeComparator<N extends GraphNode<N, E>, E extends GraphEdge<N, E>>
        implements Comparator<N> {

  @Override
  public int compare(N a, N b) {
    //compare total weight (distance to node + distance to goal)
    return Double.compare(a.getTotalWeight(), b.getTotalWeight());
  }

}


