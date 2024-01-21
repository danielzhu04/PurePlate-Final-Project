package edu.brown.cs.student.pureplatetests;

import edu.brown.cs.student.pureplate.datasources.CsvParser;
import edu.brown.cs.student.pureplate.datasources.DatasourceException;
import edu.brown.cs.student.pureplate.datasources.Query;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A class that mocks NutritionDataSource such that mocked data instead of API-queried data
 * populates the instance variable that stores food nutrient data (foodData).
 */
public class MockNutritionDataSource implements Query<List<String>, List<String>> {

  private static Map<String, Map<String, Double>> nutritionalRequirements;
  private Map<String, Double> nutritionNeeds;
  private Map<String, Map<String, Double>> foodData;
  private List<String> visited;
  private final List<String> growable = parseGrowable();
  private boolean onlyGrowable = false;

  /**
   * Constructor for MockNutritionDataSource.
   *
   * @param filename - the filename of a daily nutritional requirements CSV file.
   */
  public MockNutritionDataSource(String filename) {
    try {
      this.foodData = new HashMap<>();
      this.nutritionNeeds = new HashMap<>();
      this.getFoodDatabase();
      CsvParser parser = new CsvParser();
      parser.parse(filename);
      nutritionalRequirements = parser.getTable();
    } catch (DatasourceException e) {
      nutritionalRequirements = new HashMap<>();
    }
  }

  /**
   * Takes input parameters and then calculates nutrition recommendations based off of them.
   *
   * @param target - a list of input parameters, such as age, height, etc.
   * @return a list of food recommendations represented as Strings.
   * @throws DatasourceException if there are parameter-related issues such as a non-numeric age.
   */
  public List<String> query(List<String> target) throws DatasourceException {
    String weight = target.get(0);
    String height = target.get(1);
    String age = target.get(2);
    String gender = target.get(3);
    String activity = target.get(4);
    String growable = target.get(5);
    String foodsString = target.get(6);

    this.visited = new ArrayList<>();
    List<String> foodsList = Arrays.asList(foodsString.split("`"));
    this.visited.addAll(foodsList);
    if (growable.equalsIgnoreCase("yes")) {
      this.onlyGrowable = true;
    } else if (growable.equalsIgnoreCase("no")) {
      this.onlyGrowable = false;
    } else {
      throw new DatasourceException("Unreasonable growable parameter");
    }

    try {
      Double weight_num = Double.parseDouble(weight);
      Double height_num = Double.parseDouble(height);
      Double age_num = Double.parseDouble(age);

      this.calculateRatios(this.calculateCaloricRequirement(weight_num, height_num.intValue(),
          age_num.intValue(), gender, activity), gender);
    } catch (NumberFormatException e) {
      throw new DatasourceException("Unreasonable weight, height, or age parameter");
    }
    try {
      this.calculateDeficiency(this.visited);
    } catch (NullPointerException e) {
      throw new DatasourceException("Unrecognized food");
    }

    return this.getRecommendations();
  }

  /**
   * Populates the instance variable foodData with mocked nutrition data.
   */
  private void getFoodDatabase() {
    this.foodData.put("Carrots, baby, raw", new HashMap<>());
    this.foodData.get("Carrots, baby, raw").put("Water", 89.3);
    this.foodData.get("Carrots, baby, raw").put("Vitamin B-6", 0.115);
    this.foodData.get("Carrots, baby, raw").put("Calcium, Ca", 42.2);

    this.foodData.put("Tomato, roma", new HashMap<>());
    this.foodData.get("Tomato, roma").put("Water", 94.7);
    this.foodData.get("Tomato, roma").put("Carotene, alpha", 0.875);
    this.foodData.get("Tomato, roma").put("Vitamin B-6", 0.0789);

    this.foodData.put("Pork, loin, boneless, raw", new HashMap<>());
    this.foodData.get("Pork, loin, boneless, raw").put("Water", 68.8);
    this.foodData.get("Pork, loin, boneless, raw").put("Fatty acids, total saturated", 3.28);
    this.foodData.get("Pork, loin, boneless, raw").put("Potassium, K", 361.0);

    this.foodData.put("Blackeye pea, dry", new HashMap<>());
    this.foodData.get("Blackeye pea, dry").put("Water", 68.8);
    this.foodData.get("Blackeye pea, dry").put("Fatty acids, total saturated", 3.28);
    this.foodData.get("Blackeye pea, dry").put("Potassium, K", 361.0);
  }

  /**
   * Retrieves the "score" of a food based off of how many relevant nutrients it can fulfill.
   *
   * @return a Map of foods and their scores.
   */
  private Map<String, Double> getScore() {
    Map<String, Double> scoreMap = new HashMap<>();
    for (String foodKey : this.foodData.keySet()) {
      double score = 0.0;
      int counter = 0;
      for (String key : this.nutritionNeeds.keySet()) {
        if (this.nutritionNeeds.get(key) > 0.0) {
          double nutrient = 0;
          for (String foodNutrients : this.foodData.get(foodKey).keySet()) {
            if (foodNutrients.toLowerCase().contains(key.toLowerCase())) {
              nutrient += this.foodData.get(foodKey).get(foodNutrients);
            }
          }
          score -= 1 / (1 + Math.max(0.0,
              this.nutritionNeeds.get(key) - nutrient));
          counter++;
        }
      }
      if (this.onlyGrowable) {
        scoreMap.put(foodKey, counter > 0 ? ((this.visited.contains(foodKey) ? -(counter / score) :
            (!this.growable.contains(foodKey) ? -(counter / score) : score / counter)))
            : 1000.0); // if the food doesn't have the nutrient
      } else {
        scoreMap.put(foodKey,
            counter > 0 ? (this.visited.contains(foodKey) ? -(counter / score) : score / counter)
                : 1000.0);
      }
    }
    return scoreMap;
  }

  /**
   * Retrieves recommended foods.
   *
   * @return a list of Strings containing food recommendations.
   */
  private List<String> getRecommendations() {
    List<String> recommendationList = new ArrayList<>();
    Map<String, Double> scoreMap = this.getScore();
    // populate priority queue using getScore
    PriorityQueue<String> priorityFoods = new PriorityQueue<>(
        Comparator.comparingDouble(scoreMap::get));
    while (!this.nutritionNeeds.entrySet().stream()
        .filter(entry -> !entry.getKey().equals("Calorie Level Assessed"))
        .allMatch(entry -> entry.getValue() <= 0.0)) {
      // add all foods to priority queue and pull the first food out and add to returnlist
      priorityFoods.addAll(this.foodData.keySet());

      // reupdate nutritional needs based off that food
      String highestPriorityFood = priorityFoods.peek();

      // mark the food as already recommended
      this.visited.add(highestPriorityFood);

      // reupdate nutritional needs based off that food
      this.calculateDeficiency(this.visited);

      // update Scores
      scoreMap = this.getScore();

      // re-add everything to priority
      priorityFoods = new PriorityQueue<>(
          Comparator.comparingDouble(scoreMap::get));
      priorityFoods.addAll(this.foodData.keySet());
      recommendationList.add(highestPriorityFood);
    }
    return recommendationList;
  }

  /**
   * Calculates caloric requirements based on inputted values.
   *
   * @param weight_kg - the input weight in kg.
   * @param height_cm - the input height in cm.
   * @param age_years - the input age in years.
   * @param gender    - the input gender.
   * @param activity  - the input activity level.
   * @return a double representing an appropriate caloric requirement.
   * @throws DatasourceException if an unrecognized parameter value is inputted (e.g., negative
   *                             age).
   */
  public double calculateCaloricRequirement(
      double weight_kg, int height_cm, int age_years, String gender, String activity)
      throws DatasourceException {
    if (weight_kg <= 0 || height_cm <= 0 || age_years <= 0) {
      throw new DatasourceException("Unrecognized parameter input");
    }
    double caloricRequirement = 0;
    if (gender.equalsIgnoreCase("male")) {
      caloricRequirement = 10 * weight_kg + 6.25 * height_cm - 5 * age_years + 5;
    } else if (gender.equalsIgnoreCase("female")) {
      caloricRequirement = 10 * weight_kg + 6.25 * height_cm - 5 * age_years - 161;
    } else {
      throw new DatasourceException("Unrecognized parameter input");
    }

    return switch (activity.toLowerCase()) {
      case "sedentary" -> caloricRequirement * 1.2;
      case "lightly active" -> caloricRequirement * 1.375;
      case "moderately active" -> caloricRequirement * 1.55;
      case "very active" -> caloricRequirement * 1.725;
      case "extra active" -> caloricRequirement * 1.9;
      default -> caloricRequirement *= 1.0;
    };
  }

  /**
   * Calculates the need for each nutrient per person based on standardized guidelines.
   *
   * @param caloricRequirements - the caloric requirements this person needs.
   * @param gender              - the person's gender.
   */
  public void calculateRatios(double caloricRequirements, String gender) {
    gender = gender.toLowerCase();

    double caloricRatio = caloricRequirements / this.nutritionalRequirements.get(gender)
        .get("Calorie Level Assessed");

    for (String key : this.nutritionalRequirements.get(gender).keySet()) {
      this.nutritionNeeds.put(key,
          this.nutritionalRequirements.get(gender).get(key) / caloricRatio);
    }
  }

  /**
   * Determines which nutrients a person needs more of in their diet.
   *
   * @param foods - a list of foods (Strings) the person eats.
   */
  public void calculateDeficiency(List<String> foods) {
    Set<String> foodSet = new HashSet<>(foods);

    for (String food : foodSet) {
      for (String key : this.nutritionNeeds.keySet()) {

        // correctly assigns the nutrition value for the food
        double nutritionValue = 0;
        for (String foodNutrients : this.foodData.get(food).keySet()) {
          if (foodNutrients.toLowerCase().contains(key.toLowerCase())) {
            nutritionValue += this.foodData.get(food).get(foodNutrients);
          }
        }
        this.nutritionNeeds.put(key, this.nutritionNeeds.get(key) - nutritionValue);
      }
    }
  }

  /**
   * Parses a file containing only growable foods.
   *
   * @return a list of Strings populated by growable foods.
   */
  public static List<String> parseGrowable() {
    try {
      List<String> list = new ArrayList<>();
      BufferedReader bufferedReader = new BufferedReader(
          new FileReader("data/nutrition/growable.txt"));
      String currentFileLine = bufferedReader.readLine();
      while (currentFileLine != null) {
        list.add(currentFileLine);
        currentFileLine = bufferedReader.readLine();
      }
      bufferedReader.close();
      return list;
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }
}
