package edu.brown.cs.mramesh4.stars;

/**.
 * This is a class for a custom IllegalArgumentException
 *
 */
@SuppressWarnings("serial")
public class IllegalArgumentException extends RuntimeException {
  private String message;
  /**.
   * Creates a new IllegalArgumentException
   * @param messageFill
   *      a String that represents an error message
   *
   */
  public IllegalArgumentException(String messageFill) {
    super(messageFill);
    this.message = messageFill;
  }
  /**.
   * Returns the error message
   * @return message, the error message
   *
   */
  public String getMessage() {
    return this.message;
  }
}
