package edu.brown.cs.mramesh4.KDTree;
import edu.brown.cs.mramesh4.Dimensional.DimensionalComparator;
import edu.brown.cs.mramesh4.Dimensional.Dimensional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**.
 * Class representing a KDTree
 * @param <T> Dimensional object that Tree stores
 */
public class KDTree<T extends Dimensional> {
  private static final double RAND = 0.5;
  private KDNode<T> root;
  private int dimensionType;

  /**.
   * Constructor for the KD Tree
   * @param elements  list of elems to fill the kd tree
   * @param dimensions number of dimensions
   * @throws IllegalArgumentException  if the elements or dimensions are bad
   */
  public KDTree(List<T> elements, int dimensions) throws IllegalArgumentException {
    if (elements == null || elements.size() == 0) {
      throw new IllegalArgumentException("ERROR: Empty list");
    }
    if (dimensions <= 0) {
      throw new IllegalArgumentException("ERROR: Enter correct number of Dimensions");
    }
    dimensionType = dimensions;
    root = buildKDTree(elements, 0, elements.size(), 0);
  }

  /**.
   * Builds the KDTree in memory, recursively
   * @param elements list of elements to insert
   * @param low  low index of List to add
   * @param high high index of List to add
   * @param dimension coordinate plane we are on in this step
   * @return a root to the tree, with pointers to other elems
   */
  public KDNode<T> buildKDTree(List<T> elements, int low, int high, int dimension) {
    if (low >= high) {
      return null;
    } else if (low == (high - 1)) {
     // System.out.println("tree contains" + elements.get(low).getInfo("id"));
      return new KDNode<T>(elements.get(low), null, null, dimensionType, true);
    }
    //otherwise get the list from low --> high
    List<T> subList = elements.subList(low, high);
    int dim;
    if (dimension == 0) {
      dim = 0;
    } else {
      dim = (dimension % dimensionType);
    }
    //sort them by a dimensional comparator, that takes in dimension
    Collections.sort(subList, new DimensionalComparator(dim));
    //get the median and median element
    int median = subList.size() / 2;
    T elem = subList.get(median);
    //System.out.println("tree contains" + elem.getInfo("id"));
    //recursive calls
    KDNode<T> left = buildKDTree(subList, 0, median, dimension + 1);
    KDNode<T> right = buildKDTree(subList, median + 1, subList.size(), dimension + 1);
    //return root
    return new KDNode<T>(elem, left, right, dim,
      (left == null && right == null));
  }

  /**.
   * @return boolean representing if the tree is empty
   */
  public boolean isEmpty() {
    return (root == null);
  }

  /**.
   * Acccessor method for root
   * @return root node
   */
  public KDNode<T> getRoot() {
    return root;
  }

  /**.
   * Returns a list of nearest neighbors
   * @param k int of neighbors to get
   * @param target target node to search from
   * @param name boolean if we should include target
   * @return a list of dimensional neighbors
   */
  public List<T> nearestNeighbors(int k, Dimensional target, boolean name) {
    ArrayList<T> points = new ArrayList<T>();
    recursiveNeighbor(k, target, this.getRoot(), points, name);

    Collections.sort(points, new Comparator<Dimensional>() {
      @Override
      public int compare(Dimensional o1, Dimensional o2) {
        return Double.compare(o1.distanceBetween(target), o2.distanceBetween(target));
      }
    });
    return points;
  }

  /** .
   * Get all the neighbors within a radius
   * @param r int radius
   * @param target object to search from.
   * @param name a boolean representing if we remove the target
   * @return list of neighbors within a radius
   */
  public List<Dimensional> nearestRadius(int r, Dimensional target, boolean name) {
    ArrayList<Dimensional> points = new ArrayList<Dimensional>();
    recursiveRadius(r, target, this.getRoot(), points, name, 0);
    Collections.sort(points, new Comparator<Dimensional>() {
      @Override
      public int compare(Dimensional o1, Dimensional o2) {
        return Double.compare(o1.distanceBetween(target), o2.distanceBetween(target));
      }
    });
    return points;
  }

  /**.
   * This is a recursive function that is used to find the nearest neighbors
   * @param k   An integer representing capacity of nearest neighbors
   * @param target  The point we are searching from
   * @param node  The curr node in the recursion stack
   * @param list  List we are appending to
   * @param name boolean if name should be removed
   */
  private void recursiveNeighbor(int k, Dimensional target, KDNode<T> node,
                                 ArrayList<T> list, boolean name) {
    //base case
    if (node == null) {
      return;
    }
    //get the elements from the node
    T curr = node.getElem();
    boolean isTarget = false;
    if (name && curr.getInfo("name").equals(target.getInfo("name"))) {
      isTarget = true;
    }
    int dimension = node.getDimension();
    //sort the list by distance
    Collections.sort(list, new Comparator<Dimensional>() {
      @Override
      public int compare(Dimensional o1, Dimensional o2) {
        return Double.compare(o2.distanceBetween(target), o1.distanceBetween(target));
      }
    });
    //get the furthest away neighbor's distance
    double biggestNeighborDist = -1;
    if (!list.isEmpty()) {
      biggestNeighborDist = list.get(0).distanceBetween(target);
    }
    //get the curr node's distance
    double currDist = curr.distanceBetween(target);
    //get the axis Distance for the curr node
    double currAxis = curr.getAxisDistance(dimension, target);
    double currDim = curr.getCoordinate(dimension);
    //if the list isn't big enough, add the curr node
    if (list.size() < k) {
      if (!isTarget) {
        list.add(curr);
      }
      recursiveNeighbor(k, target, node.getLeft(), list, name);
      recursiveNeighbor(k, target, node.getRight(), list, name);
    } else {
      if (biggestNeighborDist > currDist) {
        if (!isTarget) {
          list.remove(0);
          list.add(curr);
        }
      } else if (biggestNeighborDist == currDist) {
        double rand = Math.random();
        int index = 0;
        while (index < list.size() - 1
          && list.get(index).distanceBetween(target)
          == biggestNeighborDist) {
          index++;
        }
        index = (int) (Math.random() * index);
        if (rand >= RAND && !isTarget) {
          list.remove(index);
          list.add(curr);
        }
      }
      if (biggestNeighborDist > currAxis) {
        recursiveNeighbor(k, target, node.getLeft(), list, name);
        recursiveNeighbor(k, target, node.getRight(), list, name);
      } else {
        double cord = target.getCoordinate(dimension);
        if (currDim < cord) {
          recursiveNeighbor(k, target, node.getRight(), list, name);
        } else if (currDim > cord) {
          recursiveNeighbor(k, target, node.getLeft(), list, name);
        } else {
          recursiveNeighbor(k, target, node.getRight(), list, name);
          recursiveNeighbor(k, target, node.getLeft(), list, name);
        }
      }
    }
  }

  /**.
   * This is a recursive function to find all neighbors within a radius
   * @param r int r representing a radius
   * @param target target to search for
   * @param node node to recursive search from
   * @param list list of dimensional of objects
   */
  private void recursiveRadius(int r, Dimensional target,
                               KDNode<T> node, ArrayList<Dimensional> list, boolean name,
                               int depth) {
    //base case
    if (node == null) {
      return;
    }
    //get the curr elem
    T curr = node.getElem();
    boolean add = true;
    if (name && curr.getInfo("id").equals(target.getInfo("id"))) {
      add = false;
    }
    if (add && curr.distanceBetween(target) <= r) {
      list.add(curr);
    }
   // int dimension = node.getDimension();
    int dimension = depth % 3;
    double currAxis = curr.getAxisDistance(dimension, target);
    double currDim = curr.getCoordinate(dimension);
    double cord = target.getCoordinate(dimension);
    //if the radius is still bigger recurse on both
    if (r >= currAxis || r == 0) {
      recursiveRadius(r, target, node.getLeft(), list, name, depth + 1);
      recursiveRadius(r, target, node.getRight(), list, name, depth + 1);
    } else if (currDim < cord) {
      recursiveRadius(r, target, node.getRight(), list, name, depth + 1);
    } else if (currDim > cord) {
      recursiveRadius(r, target, node.getLeft(), list, name, depth + 1);
    } else {
      recursiveRadius(r, target, node.getRight(), list, name, depth + 1);
      recursiveRadius(r, target, node.getLeft(), list, name, depth + 1);
    }
  }


}


