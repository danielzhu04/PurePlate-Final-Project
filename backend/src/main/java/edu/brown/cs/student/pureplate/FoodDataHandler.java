package edu.brown.cs.student.pureplate;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.pureplate.datasources.NutritionDataSource;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A handler to view the complete food data (food names and nutrients) on the "data" endpoint.
 */
public class FoodDataHandler implements Route {

  Map<String, Map<String, Double>> dataSource;

  /**
   * Constructor for FoodDataHandler.
   *
   * @param data - a NutritionDataSource to retrieve food data from.
   */
  public FoodDataHandler(NutritionDataSource data) {
    this.dataSource = data.getFoodData();
  }

  /**
   * This method handles when the data endpoint is visited.
   *
   * @param request  The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return a serialized Map that contains the necessary output the user will see.
   */
  @Override
  public Object handle(Request request, Response response) {
    Set<String> params = request.queryParams();
    Map<String, Object> results = new HashMap<>();

    if (!params.isEmpty()) {
      results.put("result", "error_bad_request");
      results.put("message", "unrecognized parameter");
      return this.serialize(results);
    }
    results.put("foods", this.dataSource.keySet());
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
