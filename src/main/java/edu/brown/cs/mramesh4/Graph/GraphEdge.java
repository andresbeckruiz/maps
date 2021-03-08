package edu.brown.cs.mramesh4.Graph;

/**
 * This interface allows generic edges to be used in our Graph class for the A* search.
 * It defines some common methods that all edges should implement.
 * @param <N> representing the type of node to be used in the Graph class.
 * @param <E> representing the type of edge to be used in the Graph class.
 */
public interface GraphEdge<N extends GraphNode<N, E>, E extends GraphEdge<N, E>> {

  /**
   * This method returns the Node that is at the end of the current edge.
   * @return the Node at the end of the edge.
   */
  N getTo();

  /**
   * This method returns the Node that is at the start of the current edge.
   * @return the Node at the start of the edge.
   */
  N getFrom();

  /**
   * This method returns the weight, or distance, of the current edge.
   * @return a double representing the weight, or distance
   */
  double getWeight();

}
