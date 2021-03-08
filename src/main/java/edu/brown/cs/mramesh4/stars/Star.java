package edu.brown.cs.mramesh4.stars;

import edu.brown.cs.mramesh4.Dimensional.Dimensional;

/**
 * Class representing a Star Object.
 */
public class Star implements Dimensional {
  private String starID;
  private String name;
  private double x;
  private double y;
  private double z;
  //private List<Double> cords = new ArrayList<Double>();

  /**
   * Constructor for star.
   * @param starID String representing ID.
   * @param name  String representing Name.
   * @param x String representing x coordinate.
   * @param y String representing y coordinate.
   * @param z String representing z coordinate.
   * @throws IllegalArgumentException if wrong arguments are supplied to Star
   */
  public Star(String starID, String name, String x,
              String y, String z) throws IllegalArgumentException {
    //makes sure none of the necessary arguments are null
    if (starID == null || x == null || y == null || z == null) {
      throw new
        IllegalArgumentException("ERROR: WRONG ARGUMENTS SUPPLIED TO STAR");
    }
    //saves starID and name
    this.starID = starID;
    this.name = name;
    //handles any case in which x,y,z are not integers
    try {
      this.x = Double.parseDouble(x);
      this.y = Double.parseDouble(y);
      this.z = Double.parseDouble(z);
      //cords.add(0, this.x);
     // cords.add(1, this.y);
      //cords.add(2, this.z);
    } catch (NumberFormatException e) {
      //if we do not have an integer, we throw an illegal argument
      // exception to be handled by the Stars class
      throw new IllegalArgumentException("ERROR: WRONG Arguments supplied");
    }
  }
  /*
  /**
   * Constructor which allows for stars of greater dimensions.
   * @param starID
   * @param name
   * @param elem
   * @throws IllegalArgumentException
  public Star(String starID, String name, List<Double> elem) throws IllegalArgumentException {
    if (starID == null) {
      throw new
        IllegalArgumentException("ERROR: WRONG ARGUMENTS SUPPLIED TO STAR");
    }
    if (elem.size() < 3) {
      throw new IllegalArgumentException("ERROR: Please add at least 3 coords");
    }
    this.starID = starID;
    this.name = name;
    //handles any case in which x,y,z are not integers
    cords = new ArrayList<Double>(elem);
  }
  */

  /**
   * .
   * Accessor method for starID.
   *
   * @return starID, an id that represents a star
   */
  public String getStarID() {
    return starID;
  }

  /**
   * .
   * Accessor method for name.
   *
   * @return name, a variable for name
   */
  public String getName() {
    return name;
  }

  /**
   * .
   * Accessor method for X.
   *
   * @return X, a double representing the x-pos of star
   */
  public double getX() {
    return this.x;
  }

  /**
   * .
   * Accessor method for Y.
   *
   * @return Y, a double representing the y-pos of star
   */
  public double getY() {
    return this.y;
  }

  /**
   * .
   * Accessor method for Z.
   *
   * @return Z, a double representing the z-pos of star
   */
  public double getZ() {
    return this.z;
  }

  /**
   * .
   * Returns the distance between two stars
   *
   * @param compliment A star to compare with
   * @return distance between two stars
   */
  public double calculateDistanceFromStar(Star compliment) {
    return Math.sqrt(Math.pow(Math.abs(compliment.getX() - x), 2)
      + Math.pow(Math.abs(compliment.getY() - y), 2)
      + Math.pow(Math.abs(compliment.getZ() - z), 2));
  }

  /**
   * .
   * This gets the coordinate based on an index to check
   *
   * @param index int representing index
   * @return a double representing a coordinate
   */
  @Override
  public double getCoordinate(int index) {
    if (index == 0) {
      return this.getX();
    } else if (index == 1) {
      return this.getY();
    } else {
      return this.getZ();
    }
    /*else {
      if (index < cords.size()) {
        return cords.get(index);
      } else {
        return -1;
      }
    }
     */
  }

  /**
   * .
   *
   * @return an int representing dimensions
   */
  @Override
  public int numDimensions() {
    return 3;
  }

  /**
   * .
   * Returns a distance between two objects
   *
   * @param a a dimensional object
   * @return distance between the object
   */
  @Override
  public double distanceBetween(Dimensional a) {
    double aX = a.getCoordinate(0);
    double aY = a.getCoordinate(1);
    double aZ = a.getCoordinate(2);
    return Math.sqrt(Math.pow(Math.abs(aX - x), 2)
      + Math.pow(Math.abs(aY - y), 2)
      + Math.pow(Math.abs(aZ - z), 2));
    //return calculateDistanceFromStar((Star) a);
  }
  /**.
   * Compares two stars to determine if they are the same
   * @param b a star to compare
   * @return if the stars are equal
   */
  public boolean equals(Star b) {
    return this.getStarID().equals(b.getStarID()) && this.getName().equals(b.getName())
      && this.calculateDistanceFromStar(b) == 0;
  }
  /**.
   * Takes in a dimension and calculates distances between two stars
   * @param dimension dimension to check
   * @param b another star
   * @return the distance
   */
  public double calculateAxisDistance(int dimension, Star b) {
    return Math.abs(this.getCoordinate(dimension) - b.getCoordinate(dimension));
  }

  /**.
   * Implements the Dimensional interface method to call local method
   * @param dimension Dimension to search on
   * @param a Object to compare
   * @return a double
   */
  @Override
  public double getAxisDistance(int dimension, Dimensional a) {
    return Math.abs(this.getCoordinate(dimension) - a.getCoordinate(dimension));
    //return calculateAxisDistance(dimension, (Star) a);
  }

  /**.
   * Gets info based on a specific element that the argument asks for
   * @param a argument to look for
   * @return value of argument
   */
  @Override
  public String getInfo(String a) {
    if (a.equals("name")) {
      return this.getName();
    } else if (a.equals("id")) {
      return this.getStarID();
    } else {
      return null;
    }
  }
}
