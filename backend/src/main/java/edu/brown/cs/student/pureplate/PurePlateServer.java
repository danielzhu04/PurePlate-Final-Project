package edu.brown.cs.student.pureplate;

import static spark.Spark.after;

import edu.brown.cs.student.pureplate.datasources.Cache;
import edu.brown.cs.student.pureplate.datasources.NutritionDataSource;
import edu.brown.cs.student.pureplate.datasources.Query;
import java.util.List;
import spark.Spark;

/**
 * Top-level class that contains the main() method which starts Spark and runs the pureplate and
 * data handlers through a PurePlateServer instance.
 */
public class PurePlateServer {

  /**
   * Constructor for the PurePlateServer class.
   */
  public PurePlateServer(Query<List<String>, List<String>> cache, NutritionDataSource dataSource) {
    int port = 3233;

    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
    Spark.get("pureplate", new PurePlateHandler(cache));
    Spark.get("data", new FoodDataHandler(dataSource));
    Spark.init();
    Spark.awaitInitialization();

    // This print statement was kept for easy access to the URL
    System.out.println("Server started at http://localhost:" + port);
  }

  /**
   * The initial method called when execution begins.
   *
   * @param args - an array of program arguments.
   */
  public static void main(String[] args) {
    NutritionDataSource dataSource = new NutritionDataSource(
        "./data/nutrition/daily_requirements.csv");
    new PurePlateServer(new Cache(dataSource, 100, 1000), dataSource);
  }
}
