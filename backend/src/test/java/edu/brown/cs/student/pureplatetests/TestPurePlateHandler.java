package edu.brown.cs.student.pureplatetests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.pureplate.PurePlateHandler;
import edu.brown.cs.student.pureplate.datasources.NutritionDataSource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * Tests the functionality of the PurePlateHandler class without using mocked data.
 */
public class TestPurePlateHandler {

  private JsonAdapter<Map<String, Object>> adapter;

  /**
   * Establishes the port on which URI operations should be run.
   */
  @BeforeAll
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  /**
   * Sets up the pureplate endpoint.
   */
  @BeforeEach
  public void setup() throws IOException {
    Spark.get("/pureplate",
        new PurePlateHandler(new NutritionDataSource("data/nutrition/daily_requirements.csv")));
    Spark.awaitInitialization();
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    this.adapter = moshi.adapter(mapStringObject);
  }

  /**
   * Stops listening to the pureplate endpoint.
   */
  @AfterEach
  public void tearDown() {
    Spark.unmap("/pureplate");
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to a specific API endpoint. This method is mostly borrowed from
   * the TestSoupAPIHandlers class in the server gear-up program.
   *
   * @param queryParams - the URI for the API endpoint
   * @return the connection for the given URI, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequest(String queryParams) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + queryParams);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Tests that the handler returns expected output when basic URL parameters are provided.
   *
   * @throws IOException if there are problems connecting to the endpoint.
   */
  @Test
  public void testPurePlateBasic() throws IOException {
    // Multiple foods
    HttpURLConnection loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=male&activity=very%20active&growable=no&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseMap =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "recommendations"), responseMap.keySet());
    assertEquals("success", responseMap.get("result"));
    assertNotEquals("[]", responseMap.get("recommendations"));

    // One food
    loadConnection = tryRequest(
        "pureplate?weight=10&height=10&age=10&gender=male&activity=very%20active&growable=yes&foods=Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "recommendations"), responseMap.keySet());
    assertEquals("success", responseMap.get("result"));
    assertNotEquals("[]", responseMap.get("recommendations"));

    loadConnection.disconnect();
  }
}
