package edu.brown.cs.mramesh4.Dimensional;

import java.util.Comparator;

/**
 * This is a class to compare two dimensional objects.
 */
public class DimensionalComparator implements Comparator<Dimensional> {
  private int dimension;

  /**.
   * Custom comparator to compare dimensions at a specific coordinate
   * @param dimension represents a dimension to search on.
   */
  public DimensionalComparator(int dimension) {
    this.dimension = dimension;
  }

  /**.
   * A compare function to sort two dimensional objects
   * @param a Dimensional object 1
   * @param b Dimensional object 2
   * @return int representing order to compare
   */
  @Override
  public int compare(Dimensional a, Dimensional b) {
    return Double.compare(a.getCoordinate(dimension), b.getCoordinate(dimension));
  }
}
