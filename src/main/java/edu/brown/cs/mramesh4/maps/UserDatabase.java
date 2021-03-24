package edu.brown.cs.mramesh4.maps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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

      PreparedStatement prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS"
          + "Users (timestamp DOUBLE,"
          + "user_id DOUBLE, user_name TEXT, latitude DOUBLE, longitude DOUBLE)");
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      return;
    }
  }

  public void add(Integer id, String name, Double timestamp, Double lat, Double lon) {
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("INSERT INTO Users (timestamp, user_id, user_name, "
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
}
