package edu.brown.cs.student.pureplate.datasources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.simple.parser.ParseException;

/**
 * This class queries the specified API and stores the result of that query.
 */
public class Cache implements Query<List<String>, List<String>> {

  private final Query<List<String>, List<String>> wrappedSearcher;
  private final LoadingCache<List<String>, List<String>> cache;

  /**
   * Constructor for Cache that establishes the cache.
   *
   * @param toWrap        - a Query that retrieves data from the specified API.
   * @param maxEntries    - the maximum number of entries the cache can store at a time
   * @param maxStorageMin - how long entries remain in the cache (in minutes)
   */
  public Cache(Query<List<String>, List<String>> toWrap, int maxEntries, int maxStorageMin) {
    this.wrappedSearcher = toWrap;
    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(maxEntries)
            .expireAfterWrite(maxStorageMin, TimeUnit.MINUTES)
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @Override
                  public List<String> load(List<String> key)
                      throws URISyntaxException, IOException, InterruptedException, ParseException, DatasourceException {
                    // We kept this print statement to aid our caching demo
                    System.out.println("called load for: " + key);
                    return wrappedSearcher.query(key);
                  }
                });
  }

  /**
   * Retrieves the intended data from an API associated with the provided target URI to query to.
   *
   * @param target - the API URI to query to
   * @return the target's corresponding retrieved data.
   */
  @Override
  public List<String> query(List<String> target) {
    List<String> result = this.cache.getUnchecked(target);
    // For debugging and demo (would remove in a "real" version):
    System.out.println(this.cache.stats());
    return result;
  }
}