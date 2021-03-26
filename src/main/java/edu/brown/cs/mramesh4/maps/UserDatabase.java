package edu.brown.cs.mramesh4.maps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Database class to create and store table which holds UserCheckin information.
 */
public class UserDatabase {
  private Connection conn;

  /**
   * Instantiates the database, creating tables if necessary.
   * Automatically loads files.
   */
  public UserDatabase() {
    String urlToDB = "jdbc:sqlite:userDatabase";
    try {
      conn = DriverManager.getConnection(urlToDB);
      Statement s = conn.createStatement();
      s.executeUpdate("PRAGMA foreign_keys=ON;");
      PreparedStatement prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "
          + "Users (user_id INTEGER, user_name TEXT, timestamp DOUBLE, "
          + "latitude DOUBLE, longitude DOUBLE)");
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      e.printStackTrace();
      return;
    }
  }

  /**
   * Called to insert a new User checkin into the database.
   * @param id User id
   * @param name Name of checked-in user
   * @param timestamp time of user checkin
   * @param lat latitude where the user checked in
   * @param lon longitude where the user checked in
   */
  public void add(Integer id, String name, Double timestamp, Double lat, Double lon) {
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("INSERT INTO Users (user_id, user_name, timestamp, "
          + "latitude, longitude) VALUES (?, ?, ?, ?, ?)");
      prep.setInt(1, id);
      prep.setString(2, name);
      prep.setDouble(3, timestamp);
      prep.setDouble(4, lat);
      prep.setDouble(5, lon);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      return;
    }
  }

  /**
   * Returns all past checkins of the passed-in user to be displayed on frontend gui.
   * @param id id of user to search.
   * @return List of Double[], each which store a past checkin lat,lon location of passed-in user.
   */
  public ArrayList<Object> getFromDatabase(Integer id) {
    ArrayList<Object> locationsList = new ArrayList<>();
    try {
      PreparedStatement prep = conn.prepareStatement(
          "SELECT latitude, longitude FROM 'Users' WHERE user_id = ?;");
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        Double[] coords = new Double[2];
        coords[0] = rs.getDouble(1);
        coords[1] = rs.getDouble(2);
        locationsList.add(coords);
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
    return locationsList;
  }
}
