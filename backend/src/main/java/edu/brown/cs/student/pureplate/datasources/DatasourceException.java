package edu.brown.cs.student.pureplate.datasources;

/**
 * Exception that is thrown when an error is encountered when getting data from the API.
 */
public class DatasourceException extends Exception {

  /**
   * The exception that is raised when an error is encountered in the while getting data from the
   * API. Gives an exception message.
   *
   * @param message the message that appears when the DatasourceException is encountered.
   */
  public DatasourceException(String message) {
    super(message);
  }
}
