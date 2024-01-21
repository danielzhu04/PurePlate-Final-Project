package edu.brown.cs.student.pureplatetests;

import edu.brown.cs.student.pureplate.datasources.DatasourceException;
import edu.brown.cs.student.pureplate.datasources.NutritionDataSource;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/**
 * Testing class for NutritionDataSource.
 */
public class TestNutritionDataSource {

  /**
   * Tests that food data from the nutrients API can be properly stored in NutritionDataSource's
   * foodData variable.
   *
   * @throws DatasourceException if there are any issues with retrieving food data.
   */
  @Test
  public void testGetFoodData() throws DatasourceException {
    NutritionDataSource dataSource = new NutritionDataSource(
        "data/nutrition/daily_requirements.csv");
    dataSource.getFoodDatabase();
    Map<String, Map<String, Double>> foodData = dataSource.getFoodData();
    Assert.assertEquals(foodData.size(), 265);

    // Food descriptions as keys
    Assert.assertTrue(foodData.containsKey("Carrots, baby, raw"));
    Assert.assertTrue(foodData.containsKey("Tomato, roma")); // 104 instead of 105
    Assert.assertTrue(foodData.containsKey("Pork, loin, boneless, raw"));
    Assert.assertFalse(foodData.containsKey("not in database"));
    Assert.assertFalse(foodData.containsKey("carrot")); // not a food description

    // Map w/ nutrient name keys and amount values per food description key
    Map<String, Double> carrotNutrients = foodData.get("Carrots, baby, raw");
    Assert.assertEquals(carrotNutrients.size(), 25);
    Assert.assertEquals(carrotNutrients.get("Iron, Fe"), 0.088);
    Assert.assertEquals(carrotNutrients.get("Magnesium, Mg"), 11.1);

    Map<String, Double> tomatoNutrients = foodData.get("Tomato, roma");
    Assert.assertEquals(tomatoNutrients.size(), 37);
    Assert.assertEquals(tomatoNutrients.get("Phosphorus, P"), 19.1);
    Assert.assertEquals(tomatoNutrients.get("Vitamin C, total ascorbic acid"), 17.8);

    Map<String, Double> porkNutrients = foodData.get("Pork, loin, boneless, raw");
    Assert.assertEquals(porkNutrients.size(), 22);
    Assert.assertEquals(porkNutrients.get("Sodium, Na"), 40.2);
    Assert.assertEquals(porkNutrients.get("Fatty acids, total saturated"), 3.28);

    Assert.assertThrows(NullPointerException.class,
        () -> foodData.get("not in database").get("Sodium, Na"));
    Assert.assertThrows(NullPointerException.class, () -> foodData.get("carrot").get("Iron, Fe"));
  }

  /**
   * Tests the functionality of the calculateCaloricRequirement method.
   */
  @Test
  public void testCalculateCaloricRequirement() throws DatasourceException {
    NutritionDataSource dataSource = new NutritionDataSource(
        "data/nutrition/daily_requirements.csv");

    // Negative weight, height, and age
    Assert.assertThrows(DatasourceException.class,
        () -> dataSource.calculateCaloricRequirement(-80, 175, 25, "male", "unknown"));
    Assert.assertThrows(DatasourceException.class,
        () -> dataSource.calculateCaloricRequirement(80, -175, 25, "male", "unknown"));
    Assert.assertThrows(DatasourceException.class,
        () -> dataSource.calculateCaloricRequirement(80, 175, -25, "male", "unknown"));

    // Vary weight
    double caloricRequirement = dataSource.calculateCaloricRequirement(80, 175, 25, "male",
        "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 80 + 6.25 * 175 - 5 * 25 + 5);

    caloricRequirement = dataSource.calculateCaloricRequirement(60, 175, 25, "male", "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 60 + 6.25 * 175 - 5 * 25 + 5);

    // Vary height
    caloricRequirement = dataSource.calculateCaloricRequirement(70, 160, 25, "male", "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 70 + 6.25 * 160 - 5 * 25 + 5);

    caloricRequirement = dataSource.calculateCaloricRequirement(70, 180, 25, "male", "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 70 + 6.25 * 180 - 5 * 25 + 5);

    // Vary age
    caloricRequirement = dataSource.calculateCaloricRequirement(70, 170, 15, "male", "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 70 + 6.25 * 170 - 5 * 15 + 5);

    caloricRequirement = dataSource.calculateCaloricRequirement(70, 170, 57, "male", "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 70 + 6.25 * 170 - 5 * 57 + 5);

    // Vary gender
    caloricRequirement = dataSource.calculateCaloricRequirement(70, 175, 25, "male", "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 70 + 6.25 * 175 - 5 * 25 + 5);

    caloricRequirement = dataSource.calculateCaloricRequirement(70, 175, 25, "Male", "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 70 + 6.25 * 175 - 5 * 25 + 5);

    caloricRequirement = dataSource.calculateCaloricRequirement(70, 175, 25, "female", "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 70 + 6.25 * 175 - 5 * 25 - 161);

    caloricRequirement = dataSource.calculateCaloricRequirement(70, 175, 25, "Female", "unknown");
    Assert.assertEquals(caloricRequirement, 10 * 70 + 6.25 * 175 - 5 * 25 - 161);

    // Specific activity levels
    caloricRequirement = dataSource.calculateCaloricRequirement(50, 160, 30, "female", "Sedentary");
    Assert.assertEquals(caloricRequirement, (10 * 50 + 6.25 * 160 - 5 * 30 - 161) * 1.2);

    caloricRequirement = dataSource.calculateCaloricRequirement(50, 160, 30, "female",
        "Lightly Active");
    Assert.assertEquals(caloricRequirement, (10 * 50 + 6.25 * 160 - 5 * 30 - 161) * 1.375);

    caloricRequirement = dataSource.calculateCaloricRequirement(50, 160, 30, "female",
        "Moderately Active");
    Assert.assertEquals(caloricRequirement, (10 * 50 + 6.25 * 160 - 5 * 30 - 161) * 1.55);

    caloricRequirement = dataSource.calculateCaloricRequirement(50, 160, 30, "female",
        "Very Active");
    Assert.assertEquals(caloricRequirement, (10 * 50 + 6.25 * 160 - 5 * 30 - 161) * 1.725);

    caloricRequirement = dataSource.calculateCaloricRequirement(50, 160, 30, "female",
        "Extra Active");
    Assert.assertEquals(caloricRequirement, (10 * 50 + 6.25 * 160 - 5 * 30 - 161) * 1.9);

    caloricRequirement = dataSource.calculateCaloricRequirement(50, 160, 30, "female", "asdfghjkl");
    Assert.assertEquals(caloricRequirement, 10 * 50 + 6.25 * 160 - 5 * 30 - 161);
  }

  /**
   * Tests the functionality of the query method.
   *
   * @throws DatasourceException if there are any issues with processing the query.
   */
  @Test
  public void testQuery() throws DatasourceException {
    NutritionDataSource dataSource = new NutritionDataSource(
        "data/nutrition/daily_requirements.csv");

    // basic cases
    List<String> queryInput = List.of("10", "10", "10", "male", "very%20active", "No",
        "Carrots, baby, raw`Tomato, roma");
    List<String> queryResult = dataSource.query(queryInput);
    Assert.assertFalse(queryResult.isEmpty());

    queryInput = List.of("10", "10", "10", "male", "very%20active", "yes", "Tomato, roma");
    queryResult = dataSource.query(queryInput);
    Assert.assertFalse(queryResult.isEmpty());

    queryInput = List.of("40", "40", "15", "female", "extra%20active", "yes", "Tomato, roma");
    queryResult = dataSource.query(queryInput);
    Assert.assertFalse(queryResult.isEmpty());

    // other than yes/no growable parameter
    Assert.assertThrows(DatasourceException.class, () -> dataSource.query(
        List.of("10", "10", "10", "female", "very%20active", "other", "Tomato, roma")));

    // non-numeric height, age, weight
    Assert.assertThrows(DatasourceException.class, () -> dataSource.query(
        List.of("a", "10", "10", "female", "sedentary", "no", "Tomato, roma")));
    Assert.assertThrows(DatasourceException.class, () -> dataSource.query(
        List.of("10", "a", "10", "female", "sedentary", "no", "Tomato, roma")));
    Assert.assertThrows(DatasourceException.class, () -> dataSource.query(
        List.of("10", "10", "a", "female", "sedentary", "no", "Tomato, roma")));
  }
}
