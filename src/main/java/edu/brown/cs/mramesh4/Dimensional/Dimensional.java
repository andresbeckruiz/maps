package edu.brown.cs.mramesh4.Dimensional;

/**
 * This is an interface for an object with multiple coordinates.
 */
public interface Dimensional {
  /**
   * Method to get coordinate of a specific dimension.
   * @param index int representing index to search for
   * @return a double representing coordinate at the specified dimension
   */
  double getCoordinate(int index);
  /**.
   * Abstract method to return num Dimensions
   * @return int representing dimensions
   */
  int numDimensions();
  /**.
   * Abstract method to represent distance
   * @param a a dimensional object to compare
   * @return a double distance
   */
  double distanceBetween(Dimensional a);
  /**.
   * Abstract method to get axis distance between two objects
   * @param dimension dimension of axis
   * @param a object to compare with
   * @return double representing difference in axis distance
   */
  double getAxisDistance(int dimension, Dimensional a);
  /**.
   * Returns information about object
   * @param a query string
   * @return String of info
   */
  String getInfo(String a);
}
