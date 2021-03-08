package edu.brown.cs.mramesh4.MockPerson;
import edu.brown.cs.mramesh4.stars.IllegalArgumentException;

import java.util.ArrayList;

/**.
 * This is a class for MockPerson
 */
public class MockPerson {
  private String firstName;
  private String lastName;
  private String dateTime;
  private String email;
  private String gender;
  private String street;
  private String ip;
  private static final int MONTH = 13;
  private static final int DAY = 32;
  /**.
   * This is the constructor for MockPerson
   * @param firstName string representing first-name
   * @param lastName string representing last-name
   * @param dateTime string represening date-time
   * @param email string representing email
   * @param gender string representing gender
   * @param street string representing street address
   * @param ip string representing ip address
   * @throws IllegalArgumentException if input is malformed
   */
  public MockPerson(String firstName, String lastName, String email, String dateTime,
             String gender, String street, String ip) throws
            IllegalArgumentException {
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateTime = dateTime;
    this.email = email;
    this.gender = gender;
    this.street = street;
    this.ip = ip;

    ArrayList<String> inv = getInvalidItems();
    if (inv.size() > 0) {
      throw new IllegalArgumentException("ERROR: MALFORMED INPUT");
    }
  }

  /**.
   * Returns a stringified version of the inputs
   * @return a string to print of the MockPerson
   */
  public String toString() {
    String ret = "Name: " + firstName + " " + lastName
        + ","  + "Address:" + " " +  street + ","
        + "Gender:" + " " +  gender + ","
        + "E-mail:" + " " +  email + ","
        + "Date:" + " " +  dateTime + ","
        + "Ip-Address:" + " " +  ip;
    return ret;
  }

  /**.
   * Figures out if a user's name is invalid
   * @param fn
   *          String representing first name
   * @param ln
   *          String representing last name
   * @return boolean if field is valid
   */
  public boolean isValidName(String fn, String ln) {
    if (fn == null || ln == null) {
      return false;
    }
    if (fn.equals("") || ln.equals("")) {
      return false;
    }
    return true;
  }

  /**.
   * Checking if the gender field is valid
   * @param gen
   *           String representing gender
   * @return boolean
   */
  public boolean isValidGender(String gen) {
    if (gen == null) {
      return false;
    }
    if (gen.equals("")) {
      return false;
    }
    return true;
  }
  /**.
   * Checking if the email field is valid
   * @param mail
   *           String representing email
   * @return boolean
   */
  public boolean isValidEmail(String mail) {
    if (mail == null) {
      return false;
    }
    String charStr = "@";
    if (mail.indexOf(charStr.charAt(0)) < 0 || mail.indexOf(charStr.charAt(0)) >= mail.length()) {
      return false;
    }
    if (mail.equals("")) {
      return false;
    }
    return true;
  }
  /**.
   * Checking if the date field is valid
   * @param date
   *           String representing date-time
   * @return boolean
   */
  public boolean isValidDate(String date) {
    if (date == null) {
      return false;
    }
    if (date.equals("")) {
      return false;
    }
    if (date.lastIndexOf("/") != date.length() - 3 && date.lastIndexOf("/") != date.length() - 5) {
      return false;
    }
    if (date.indexOf("/") != 1 && date.indexOf("/") != 2) {
      return false;
    }
    String[] dateSpl = date.split("/");
    try {
      if (Integer.parseInt(dateSpl[0]) > MONTH) {
        //System.out.println("There's an issue w month" + Integer.parseInt(dateSpl[0]));
        return false;
      } else if (Integer.parseInt(dateSpl[1]) > DAY) {
        //System.out.println("There's an issue w day" + Integer.parseInt(dateSpl[1]));
        return false;
      }
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  /**.
   * Returns if the ip is valid
   * @param ipaddress
   *            String of Ip address
   * @return boolean
   */
  public boolean isValidIp(String ipaddress) {
    if (ipaddress == null) {
      return false;
    }
    if (ipaddress.equals("")) {
      return false;
    }
    String[] ipSplit = ipaddress.split(".");
    for (String piece: ipSplit) {
      try {
        double c = Double.parseDouble(piece);
      } catch (NumberFormatException e) {
        return false;
      }
    }
    return true;
  }
  /**.
   * Calculates all the invalid items
   * @return a list of invalid items
   */
  public ArrayList<String> getInvalidItems() {
    ArrayList<String> invalid =  new ArrayList<String>();
    if (!isValidGender(this.gender)) {
      invalid.add("Gender");
    }
    if (!isValidName(this.firstName, this.lastName)) {
      invalid.add("Name");
    }
    if (!isValidEmail(this.email)) {
      invalid.add("Email");
    }
    if (!isValidDate(this.dateTime)) {
      invalid.add("Date");
    }
    if (!isValidIp(this.ip)) {
      invalid.add("Ip");
    }
    return invalid;
  }
}
