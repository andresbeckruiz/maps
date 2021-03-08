package edu.brown.cs.mramesh4.REPL;

import edu.brown.cs.mramesh4.stars.ActionMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * REPL class which instantiates a BufferedReader to read and evaulate datafile given
 * by user.
 */
public class Reader {
  private BufferedReader reader;
  private HashMap<String, ActionMethod<?>> actions;


  /**
   * Creates instances of builder class and BufferedReader so that new instances are
   * not made everytime read() method is called in loop in Main.java class.
   * @param acts current command
   */
  public Reader(HashMap<String, ActionMethod<?>> acts) {
    actions = acts;
    reader = new BufferedReader(new InputStreamReader(System.in));
  }

  /**
   * Initial method called in main class. Method called infinitely until user input is empty.
   * In this case, the method will return false, which will signal to my main class to end
   * the infinite loop.
   * @return boolean to signal if infinite loop should continue or not.
   */
  public boolean read() {
    try {
      String input = reader.readLine();
      if (input == null || input.isEmpty()) {
        return false;
      }
      String[] inputWords = input.split("\\s(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
      this.runCommand(inputWords[0], inputWords);
    } catch (IOException e) {
      System.err.println("ERROR: IOException in REPL");
    }
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
   */
  public void runCommand(String command, String[] inputs) {
    boolean found = false;
    if (actions.containsKey(command)) {
      found = true;
      actions.get(command).run(inputs);
    }
    if (!found) {
      System.out.println("ERROR:");
    }
  }
}

