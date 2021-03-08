package REPL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * REPL class which instantiates a BufferedReader to read and evaulate datafile given
 * by user.
 */
public class Reader {
  private BufferedReader reader;
  private ArrayList<Command> commands;


  /**
   * Creates instances of builder class and BufferedReader so that new instances are
   * not made everytime read() method is called in loop in Main.java class.
   * @param c current command
   */
  public Reader(ArrayList<Command> c) {
    commands = c;
    reader = new BufferedReader(new InputStreamReader(System.in));
  }

  /**
   * Initial method called in main class. Method called infinitely until user input is empty.
   * In this case, the method will return false, which will signal to my main class to end
   * the infinite loop.
   * @return boolean to signal if infinite loop should continue or not.
   * @throws IOException due to usage of BufferedReader and readLine() method.
   * @throws SQLException handles exception for SQL command called in Database class
   */
  public boolean read() throws IOException, SQLException {
    String input = reader.readLine();
    if (input == null || input.isEmpty()) {
      return false;
    }
    String[] inputWords = input.split("\\s(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    this.runCommand(inputWords[0], inputWords);
    return true;
  }

  /**
   * Main method for my REPL called from my builder class. This will put the users input
   * into a 2D array where the first dimension is every row of the file and the second
   * dimension is every word within that row.
   * @param filePath Path to file inputted by user used by BufferedReader to create 2D array.
   * @return creates a 2D array to return to interpretWord() method in Build.java class.
   * @throws IOException due to usage of BufferedReader and readLine() method.
   */
  public ArrayList<String[]> splitData(String filePath) throws IOException {
    ArrayList<String[]> returnList = new ArrayList<>();
    Path p = Paths.get(filePath);
    try (BufferedReader fileReader = Files.newBufferedReader(p)) {
      String line = fileReader.readLine();
      int i = 0;
      while (line != null && !line.isEmpty()) {
        String[] newEntry = line.split(",");
        if (i == 0) {
          i++;
        } else {
          returnList.add(newEntry);
        }
        line = fileReader.readLine();
      }
    }
    return returnList;
  }

  /**
   * From Delora's REPL, sets command using command interface.
   * @param command current command inputted by user
   * @param inputs total string array from terminal input
   * @throws IOException handles exception for buffered reader
   * @throws SQLException handles exception for SQL command called by execute()
   */
  public void runCommand(String command, String[] inputs) throws IOException, SQLException {
    boolean found = false;
    for (Command i : commands) {
      if (i.getCommand().equals(command)) {
        found = true;
        i.execute(inputs);
      }
    }
    if (!found) {
      System.out.println("ERROR:");
    }
  }

  /**
   * Used within Build.java class to ensure that file inputted by user is valid. If the file
   * path does not exist, my Build.java class will return to the user that the file they
   * wrote does not exist.
   * @param filePath path to file that user inputted. Checking if this file exists.
   * @return boolean will inform Build.java class if the user's inputted file path is valid or not.
   */
  public boolean isPath(String filePath) {
    Path p = Paths.get(filePath);
    if (Files.exists(p)) {
      return true;
    }
    return false;
  }
}

