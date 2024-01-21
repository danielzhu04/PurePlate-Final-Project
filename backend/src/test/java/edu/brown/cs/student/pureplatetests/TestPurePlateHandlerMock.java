package edu.brown.cs.student.pureplatetests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.pureplate.PurePlateHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * Tests the PurePlateHandler class using mocked data.
 */
public class TestPurePlateHandlerMock {

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
    Spark.get("/pureplate", new PurePlateHandler(
        new MockNutritionDataSource("data/nutrition/daily_requirements_mocked.csv")));
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

    loadConnection =
        tryRequest(
            "pureplate?weight=20&height=20&age=5&gender=female&activity=extra%20active&growable=Yes&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "recommendations"), responseMap.keySet());
    assertEquals("success", responseMap.get("result"));
    assertNotEquals("[]", responseMap.get("recommendations"));

    // One food
    loadConnection = tryRequest(
        "pureplate?weight=10&height=10&age=10&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "recommendations"), responseMap.keySet());
    assertEquals("success", responseMap.get("result"));
    assertNotEquals("[]", responseMap.get("recommendations"));
    Object oneCarrotRecs = responseMap.get("recommendations");

    // Multiple of the same food (should treat as one food)
    loadConnection = tryRequest(
        "pureplate?weight=10&height=10&age=10&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Carrots,%20baby,%20raw");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "recommendations"), responseMap.keySet());
    assertEquals("success", responseMap.get("result"));
    assertNotEquals("[]", responseMap.get("recommendations"));
    assertEquals(oneCarrotRecs, responseMap.get("recommendations"));

    loadConnection = tryRequest(
        "pureplate?weight=10&height=10&age=10&gender=male&activity=very%20active&growable=yes&foods=Carrots,%20baby,%20raw`Carrots,%20baby,%20raw");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "recommendations"), responseMap.keySet());
    assertEquals("success", responseMap.get("result"));
    assertNotEquals("[]", responseMap.get("recommendations"));
    assertNotEquals(oneCarrotRecs, responseMap.get("recommendations"));

    loadConnection.disconnect();
  }

  /**
   * Fuzz-testing; tests that randomly generating a wide range of numeric values for the weight,
   * height, and age parameters for many iterations doesn't produce unexpected handler output.
   *
   * @throws IOException if there are problems connecting to the endpoint.
   */
  @Test
  public void testVaryNumericalParams() throws IOException {
    Double weight = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    // height and age are technically both ints, but we will use doubles to check for errors
    Double height = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    Double age = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);

    for (int i = 0; i < 10000; i++) {
      HttpURLConnection loadConnection =
          tryRequest("pureplate?weight=" + weight + "&height=" + height + "&age=" + age
              + "&gender=male&activity=very%20active&growable=no&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
      assertEquals(200, loadConnection.getResponseCode());
      Map<String, Object> responseMap =
          this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
      assertEquals(2, responseMap.size());
      if (weight <= 0 || height <= 0 || age <= 0) {
        assertEquals(Set.of("result", "message"), responseMap.keySet());
        assertEquals("error_bad_request", responseMap.get("result"));
      } else {
        assertEquals(Set.of("result", "recommendations"), responseMap.keySet());
        assertEquals("success", responseMap.get("result"));
        assertNotEquals("[]", responseMap.get("recommendations"));
      }
    }
  }

  /**
   * Tests that cases where an "error_bad_request" result should be returned by the handler actually
   * returns that result.
   *
   * @throws IOException if there are problems connecting to the endpoint.
   */
  @Test
  public void testPurePlateBadRequest() throws IOException {
    // Missing parameters
    HttpURLConnection loadConnection =
        tryRequest(
            "pureplate?height=10&age=10&gender=female&activity=very%20active&growable=no&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseMap =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("missing request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&age=10&gender=male&activity=very%20active&growable=yes&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("missing request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&gender=female&activity=very%20active&growable=no&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("missing request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&activity=very%20active&growable=Yes&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("missing request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=female&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("missing request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=male&activity=sedentarygrowable=No");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("missing request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=male&activity=sedentarygrowable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("missing request parameter", responseMap.get("message"));

    // Unrecognized parameter
    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma&random=random");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter", responseMap.get("message"));

    // Unrecognized parameter values (e.g., negative age)
    loadConnection =
        tryRequest(
            "pureplate?weight=0&height=0&age=0&gender=female&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=-10&height=10&age=10&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=-10&age=10&gender=female&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=-10&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=unrecognized&activity=very%20active&growable=no&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    // Empty request parameters
    loadConnection =
        tryRequest(
            "pureplate?weight=&height=10&age=10&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("empty request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=&age=10&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("empty request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("empty request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("empty request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=male&activity=&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("empty request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=male&activity=very%20active&growable=&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("empty request parameter", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=10&gender=male&activity=very%20active&growable=No&foods=");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("empty request parameter", responseMap.get("message"));

    // Non-numerical value for weight, height, and age
    loadConnection =
        tryRequest(
            "pureplate?weight=a&height=10&age=10&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=a&age=10&gender=female&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=a&gender=male&activity=very%20active&growable=No&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    // Random value for growable toggle
    loadConnection =
        tryRequest(
            "pureplate?weight=10&height=10&age=a&gender=male&activity=very%20active&growable=random&foods=Carrots,%20baby,%20raw`Tomato,%20roma");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals(Set.of("result", "message"), responseMap.keySet());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    // Unrecognized foods
    loadConnection = tryRequest(
        "pureplate?weight=10&height=10&age=10&gender=female&activity=very%20active&growable=yes&foods=Carrots,%20baby,%20raw`unrecognized");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap =
        this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    loadConnection = tryRequest(
        "pureplate?weight=10&height=10&age=10&gender=female&activity=very%20active&growable=Yes&foods=unrecognized");
    assertEquals(200, loadConnection.getResponseCode());
    responseMap = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(2, responseMap.size());
    assertEquals("error_bad_request", responseMap.get("result"));
    assertEquals("unrecognized parameter input", responseMap.get("message"));

    loadConnection.disconnect();
  }
}
