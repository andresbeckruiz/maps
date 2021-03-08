package REPL;

import java.io.IOException;
import java.sql.SQLException;

/**.
 * Command interface
 */
public interface Command {

  /**
   * Gets current command to be run.
   * @return command value
   */
  String getCommand();

  /**
   * Method to run command.
   * @param inputs array list of input from terminal
   * @return boolean if input valid
   * @throws IOException buffered reader handling
   * @throws SQLException handles exception for SQL command called by execute()
   *    in Command classes
   */
  boolean execute(String[] inputs) throws IOException, SQLException;

  /**
   * Output from execution of command.
   * @return string of output from command
   */
  String getResult();
}
