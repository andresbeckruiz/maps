package edu.brown.cs.mramesh4.maps;

/**
 * class for passing user checkin data.
 */
public class UserCheckin {

  private int id;
  private String name;
  private double ts;
  private double lat;
  private double lon;

  /**
   * User Checkin object. Each represents a new entry in the database.
   * @param userId ID of current user
   * @param username name of current user
   * @param timestamp time that current user checked in
   * @param latitude latitude location where current user checked in
   * @param longitude longitude location where current user checked in
   */
  public UserCheckin(
      int userId,
      String username,
      double timestamp,
      double latitude,
      double longitude) {
    id = userId;
    name = username;
    ts = timestamp;
    lat = latitude;
    lon = longitude;
  }

  /**
   * ID of current user.
   * @return integer ID of user
   */
  public int getId() {
    return id;
  }

  /**
   * Name of current user.
   * @return string name of user
   */
  public String getName() {
    return name;
  }

  /**
   * Time of current user's checkin.
   * @return timestamp number.
   */
  public double getTimestamp() {
    return ts;
  }

  /**
   * Latitude location of current user.
   * @return double latitude coordinate.
   */
  public double getLat() {
    return lat;
  }

  /**
   * Longitude location of current user.
   * @return double longitude coordinate.
   */
  public double getLon() {
    return lon;
  }
}
