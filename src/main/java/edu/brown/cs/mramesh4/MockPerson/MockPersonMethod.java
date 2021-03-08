package edu.brown.cs.mramesh4.MockPerson;
import edu.brown.cs.mramesh4.CSVParser.CSVParser;
import edu.brown.cs.mramesh4.stars.ActionMethod;
import edu.brown.cs.mramesh4.stars.IllegalArgumentException;

import java.util.ArrayList;
import java.util.List;

/**.
 * This is a class for processesing MockPeople
 */
public class MockPersonMethod implements ActionMethod<String> {
  private ArrayList<MockPerson> people;

  /**.
   * Declares the MockPerson Method
   */
  public MockPersonMethod() {
    people = new ArrayList<MockPerson>();
  }

  /**.
   * Implements the run interface to process
   * the command
   * @param coms representing a string command
   */
  @Override
  public void run(String[] coms) {
    ArrayList<String> ret = new ArrayList<String>();
    if (coms.length != 2) {
      System.err.println("ERROR: Invalid params");
    } else {
      if (coms[0].equals("mock")) {
        loadFileAndPrint(coms[1]);
      } else {
        System.err.println("ERROR: Please enter a valid command");
      }
    }
  }
  /**.
   * Takes the filename and loads into a CSV
   * to parse and print
   * @param com
   *           A string representing filename
   */
  public void loadFileAndPrint(String com) {
    CSVParser csv = new CSVParser();
    if (!csv.isValidFile(com)) {
      System.err.println("ERROR: FILE NOT FOUND");
      return;
    } else {
      ArrayList<List<String>> parsed = csv.parseCSV(com);
      List<String> heading = parsed.get(0);
      parsed.remove(0);
      if (parsed == null) {
        return;
      }
      if (parsed.size() == 0) {
        System.err.println("ERROR: File is Invalid");
        return;
      }
      if (!(heading.get(0).equals("first_name"))
          || !(heading.get(1).equals("last_name"))
          || !(heading.get(2).equals("email"))
          || !(heading.get(3).equals("date_time"))
          || !(heading.get(4).equals("gender"))
          || !(heading.get(5).equals("street_address"))
          || !(heading.get(6).equals("ip_address"))) {
        System.err.println("ERROR: This file has a malformed header");
        people.clear();
        return;
      }
      people.clear();
      for (List<String> person: parsed) {
        try {
          people.add(new MockPerson(person.get(0), person.get(1),
              person.get(2), person.get(3), person.get(4), person.get(5),
              person.get(6)));
        } catch (IllegalArgumentException e) {
          System.err.println(e.getMessage());
          people.clear();
          return;
        } catch (ArrayIndexOutOfBoundsException e) {
          System.err.println("ERROR: Malformed input");
          people.clear();
          return;
        }
      }
      for (MockPerson person: people) {
        System.out.println(person.toString());
      }
    }
    //people.clear();
  }

  /**.
   * Accessor for arrayList
   * @return an arrayList of People
   */
  public ArrayList<MockPerson> getPeople() {
    return people;
  }
}
