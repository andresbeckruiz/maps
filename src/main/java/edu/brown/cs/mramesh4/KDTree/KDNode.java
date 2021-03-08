package edu.brown.cs.mramesh4.KDTree;

import edu.brown.cs.mramesh4.Dimensional.Dimensional;

/**
 * This is a class that represents a Node of a KDTree.
 * @param <T> an element to store in the node.
 */
public class KDNode<T extends Dimensional> {
  private KDNode<T> left;
  private KDNode<T> right;
  private KDNode<T> val;
  private int dimension;
  private T value;
  private boolean isLeaf;

  /**.
   * Constructor for a node
   * @param element takes in an element
   */
  public KDNode(T element) {
    value = element;
  }

  /**.
   * Constructor for a node
   * @param element Element to store
   * @param l pointer to left node
   * @param r pointer to right node
   * @param dimensional dimension of Node
   * @param isL boolean to represent if Leaf
   */
  public KDNode(T element, KDNode<T> l, KDNode<T> r, int dimensional, boolean isL) {
    value = element;
    left = l;
    right = r;
    dimension = dimensional;
    isLeaf = isL;
  }

  /**.
   * Returns if element is contained in node
   * @param element elem to search for
   * @return boolean representing if elem is value of node
   */
  public boolean contains(T element) {
    return value == element;
  }

  /**.
   * Accessor method to left pointer
   * @return left KdNode
   */
  public KDNode<T> getLeft() {
    return left;
  }

  /**.
   * Accessor method to right pointer
   * @return right KDNode
   */
  public KDNode<T> getRight() {
    return right;
  }

  /**.
   * Accessor method to dimension
   * @return dimension of node
   */
  public int getDimension() {
    return dimension;
  }

  /**.
   * Accessor method to element in node
   * @return element stored in node
   */
  public T getElem() {
    return value;
  }
}
