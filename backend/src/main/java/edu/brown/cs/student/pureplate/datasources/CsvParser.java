package edu.brown.cs.student.pureplate.datasources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * Class that parses the daily requirements CSV.
 */
public class CsvParser {

  private Map<String, Map<String, Double>> table;

  /**
   * Constructor for CsvParser.
   */
  public CsvParser() {
    this.table = new HashMap<>();
  }

  /**
   * Parses the given filename input (intended to be a CSV) into a Map.
   *
   * @param filename - the name of the CSV data to be parsed.
   * @throws DatasourceException if the file can't be found.
   */
  public void parse(String filename) throws DatasourceException {
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
      String currentFileLine = bufferedReader.readLine();
      List<String> headers = Arrays.asList(currentFileLine.split(","));
      currentFileLine = bufferedReader.readLine();
      int genderIndex = headers.indexOf("gender");

      while (currentFileLine != null) {
        List<String> rowValues = Arrays.asList(currentFileLine.split(","));
        String gender = rowValues.get(genderIndex);
        this.table.put(gender, new HashMap<>());
        for (int i = 1; i < headers.size(); i++) {
          this.table.get(gender).put(headers.get(i), Double.parseDouble(rowValues.get(i)));
        }
        currentFileLine = bufferedReader.readLine();
      }
      bufferedReader.close();
    } catch (IOException e) {
      throw new DatasourceException("File can't be found");
    }
  }

  /**
   * Returns a copy of the instance variable table that stores the CSV file contents.
   *
   * @return a copy of the "table" instance variable.
   */
  public Map<String, Map<String, Double>> getTable() {
    return new HashMap<>(this.table);
  }
}
