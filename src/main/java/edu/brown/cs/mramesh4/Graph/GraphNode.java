package edu.brown.cs.mramesh4.Graph;

import java.util.List;

/**
 * This interface allows generic nodes to be used in our Graph class for the A* search.
 * It defines some common methods that all nodes should implement.
 * @param <N> representing the type of node to be used in the Graph class.
 * @param <E> representing the type of edge to be used in the Graph class.
 */
public interface GraphNode<N extends GraphNode<N, E>, E extends GraphEdge<N, E>> {

  /**
   * This method returns the name or ID of the current node.
   * @return String representing ID or name of Node
   */
  String getName();

  /**
   * This method returns the edge that leads to the current node.
   * @return and edge representing the edge that leads to the current node.
   */
  GraphEdge<N, E> getFrom();

  /**
   * This setter method sets the edge that led to the current node.
   * @param e representing the edge to set as the one leading to the node.
   */
  void setFrom(E e);

  /**
   * This getter method returns the current distance it takes to get to the
   * current node.
   * @return a double representing distance.
   */
  double getCurrentDistance();

  /**
   * This setter method sets the current distance it takes to get to the
   * current node.
   * @param d a double representing the distance to set.
   */
  void setCurrentDistance(double d);

  /**
   * This method checks if two Node instances are the same instance.
   * @param node representing a Node to compare with
   * @return a boolean determining whether the Nodes are equal (true if
   * equal)
   */
  boolean equals(N node);

  /**
   * This methods returns a list of neighbors of the current Node.
   * @return a List of type Node representing the neighbors
   */
  List<N> neighbors();

  /**
   * This method returns the total weight, taking into account both the
   * distance it takes to get to the current Node and the distance left
   * to the end Node.
   * @return double representing total weight
   */
  double getTotalWeight();

  /**
   * This setter method sets the total weight of the current node.
   * @param d representing a double representing the total weight.
   */
  void setTotalWeight(double d);

  /**
   * This method returns the distance to the goal node.
   * @param node representing the goal node
   * @return a double representing distance.
   */
  double toGoal(N node);

}
