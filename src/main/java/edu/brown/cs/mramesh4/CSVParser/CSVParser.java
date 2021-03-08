package edu.brown.cs.mramesh4.CSVParser;
import edu.brown.cs.mramesh4.stars.IllegalArgumentException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
/**
 * This is a class to create a CSVParser.
 *
 */
public class CSVParser {
  private int rows = -1;
  private int cols = -1;
  private String file;
  /**.
   * This is the CSVParser Constructor
   *
   */
  public CSVParser() {

  }
  /**.
   * ParsedCSV takes in the filename supplied to the CSVParser and then
   * creates a list of lists that represent the CSV
   * @param filename A string represent a direct path to the file to parse;
   * @return Returns a List of rows, where each row is represented as a list of its columns
   */
  public ArrayList<List<String>> parseCSV(String filename) {
    //set the filename
    setFileToParse(filename);
    ArrayList<List<String>> csvTable = new ArrayList<>();
    //make sure the file exists
    if (file != null) {
      try {
        //create an buffer that reads the file
        BufferedReader csvReader = new BufferedReader(new FileReader(file));
        String row;
        //read each row/line of the file
        while ((row = csvReader.readLine()) != null) {
        //increment num rows
          rows++;
          String[] data = row.split(",");
        //add the row to the list of rows
          csvTable.add(Arrays.asList(data));
        //set the cols to the maximum cols we have encountered
          cols = Math.max(cols, data.length);
        }
      } catch (FileNotFoundException e) { //catch if the File loaded was non-existent
        System.err.println("ERROR: The file does not exist");
        return null;
      } catch (IOException e) { //catch an IOException in regards to file formatting.
        System.err.println("ERROR: File is not properly formatted");
        return null;
      }
    } else {  //throw an error if the file doesn't exist [ie there is no local file]
      System.err.println("ERROR: No file loaded");
      return null;
    }
    return csvTable;
  }

  /**
   * Sets a file for the parser to parse.
   * @param filename A string representing path a file to parse
   * @throws IllegalArgumentException if file does not exist.
   */
  public void setFileToParse(String filename) throws IllegalArgumentException {
    File csvFile = new File(filename);
    if (csvFile.isFile()) {
      file = filename;
    } else {
      throw new IllegalArgumentException("The file does not exist");
    }
  }
  /**.
   * This method returns if a file exists
   * @param filename
   *          A string represent a direct path to the file to load
   * @return a boolean if the file exists;
   *
   */
  public boolean isValidFile(String filename) {
    File csvFile = new File(filename);
    return csvFile.isFile();
  }

  /**
   * Returns the number of rows.
   * @return int number of rows
   * @throws IllegalArgumentException if file has not been loaded.
   */
  public int getRows() throws IllegalArgumentException {
    if (rows == -1) {
      throw new IllegalArgumentException("ERROR: File has not been loaded");
    }
    return rows;
  }
  /**
   * Returns the number of cols.
   * @return int number of cols
   * @throws IllegalArgumentException if no file has been loaded.
   */
  public int getCols() throws IllegalArgumentException {
    if (cols == -1) {
      throw new IllegalArgumentException("ERROR: File has not been loaded");
    }
    return cols;
  }
}
