package edu.brown.cs.student.pureplatetests;

import edu.brown.cs.student.pureplate.datasources.CsvParser;
import edu.brown.cs.student.pureplate.datasources.DatasourceException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/**
 * Tests the functionality of the CsvParser class.
 */
public class TestCsvParser {

  /**
   * Tests the functionality of the parse method.
   *
   * @throws DatasourceException if there are any issues with parsing a CSV file.
   */
  @Test
  public void testParse() throws DatasourceException {
    CsvParser parser = new CsvParser();

    // File doesn't exist
    Assert.assertThrows(DatasourceException.class, () -> parser.parse("notafilename.csv"));

    // The file we want to parse (daily_requirements.csv) can be parsed
    parser.parse("data/nutrition/daily_requirements.csv");
    Map<String, Map<String, Double>> requirementsTable = parser.getTable();
    Assert.assertEquals(requirementsTable.keySet().size(), 2);
    Assert.assertTrue(requirementsTable.containsKey("male"));
    Assert.assertTrue(requirementsTable.containsKey("female"));

    Map<String, Double> maleRequirements = requirementsTable.get("male");
    Map<String, Double> femaleRequirements = requirementsTable.get("female");
    Assert.assertEquals(maleRequirements.size(), 23);
    Assert.assertEquals(femaleRequirements.size(), 23);

    Assert.assertEquals(maleRequirements.get("Calorie Level Assessed"), 2000);
    Assert.assertEquals(femaleRequirements.get("Calorie Level Assessed"), 1600);

    Assert.assertEquals(maleRequirements.get("Protein"), 56);
    Assert.assertEquals(femaleRequirements.get("Protein"), 46);

    Assert.assertEquals(maleRequirements.get("Fiber"), 28);
    Assert.assertEquals(femaleRequirements.get("Fiber"), 22);

    Assert.assertEquals(maleRequirements.get("Vitamin K"), 120);
    Assert.assertEquals(femaleRequirements.get("Vitamin K"), 90);

    Assert.assertEquals(maleRequirements.get("Folate"), 400);
    Assert.assertEquals(femaleRequirements.get("Folate"), 400);
  }

}
