package edu.brown.cs.student.pureplate;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.pureplate.datasources.DatasourceException;
import edu.brown.cs.student.pureplate.datasources.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A class that depicts nutritional data based on url input on the "pureplate" endpoint.
 */
public class PurePlateHandler implements Route {

  private static Query<List<String>, List<String>> cache;

  /**
   * Constructor for PurePlateHandler.
   */
  public PurePlateHandler(Query<List<String>, List<String>> myCache) {
    cache = myCache;
  }

  /**
   * This method handles when the pureplate endpoint is visited.
   *
   * @param request  The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return a serialized Map that contains the necessary output the user will see.
   */
  @Override
  public Object handle(Request request, Response response) {
    Set<String> params = request.queryParams();
    Map<String, Object> results = new HashMap<>();
    String weight = request.queryParams("weight");
    String height = request.queryParams("height");
    String age = request.queryParams("age");
    String gender = request.queryParams("gender");
    String activity = request.queryParams("activity");
    String growable = request.queryParams("growable");
    String foods = request.queryParams("foods");

    if (weight == null || height == null || age == null || gender == null || activity == null
        || growable == null || foods == null) {
      results.put("result", "error_bad_request");
      results.put("message", "missing request parameter");
      return this.serialize(results);
    } else if (weight.equals("") || height.equals("") || age.equals("") || gender.equals("")
        || activity.equals("") || growable.equals("") || foods.equals("")) {
      results.put("result", "error_bad_request");
      results.put("message", "empty request parameter");
      return this.serialize(results);
    }

    if (params.size() > 7) {
      results.put("result", "error_bad_request");
      results.put("message", "unrecognized parameter");
      return this.serialize(results);
    }

    try {
      results.put("recommendations",
          cache.query(List.of(weight, height, age, gender, activity, growable, foods)));
      results.put("result", "success");
    } catch (DatasourceException e) {
      results.put("result", "error_bad_request");
      results.put("message", "unrecognized parameter input");
    } catch (Exception e) {
      results.put("result", "error_bad_datasource");
      results.put("message", "recommendations could not be retrieved");
      e.printStackTrace();
    }

    return this.serialize(results);
  }

  /**
   * Serializes an inputted Map into json format.
   *
   * @param contents - the to-be-serialized Map.
   * @return the serialized Map.
   */
  private String serialize(Map<String, Object> contents) {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));
    return adapter.toJson(contents);
  }
}
