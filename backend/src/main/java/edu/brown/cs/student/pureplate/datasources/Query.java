package edu.brown.cs.student.pureplate.datasources;

import java.io.IOException;
import java.net.URISyntaxException;
import org.json.simple.parser.ParseException;

/**
 * An interface used by classes that call (and perhaps cache results from) a specific API.
 *
 * @param <RESULT> the type of the elements returned by the result
 * @param <TARGET> the type of the value being queried for
 */
public interface Query<RESULT, TARGET> {

  /**
   * A method that retrieves the associated result of the user-provided target.
   *
   * @param target - the object to be queried on. In the context of NutritionDataSource, it should
   *               be a list of Strings containing various url-provided parameters.
   * @return the result associated with the target
   * @throws URISyntaxException   if an error occurs with processing the target as a URI
   * @throws IOException          if any broad user input error occurs
   * @throws InterruptedException if a query is interrupted
   */
  RESULT query(TARGET target)
      throws URISyntaxException, IOException, InterruptedException, ParseException, DatasourceException;
}
