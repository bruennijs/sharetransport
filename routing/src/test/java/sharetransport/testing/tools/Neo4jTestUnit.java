package sharetransport.testing.tools;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;

/**
 * Scope within single test that creates graph from statements and
 * deletes all nodes of entire db afterwards.
 *
 * @author Oliver Brüntje
 */
public class Neo4jTestUnit implements AutoCloseable {

  private Driver driver;

  public Neo4jTestUnit(Driver driver, String filePath) throws IOException {

    this.driver = notNull(driver, "driver cannot be null");
    initDb(filePath);
  }

  public Neo4jTestUnit(Driver driver, InputStream stream) throws IOException {

    this.driver = notNull(driver, "driver cannot be null");
    initDb(stream);
  }

  /**
   * Creates test unit instance loading statements from file into db using driver object.
   * @param driver driver to use.
   * @param filePath filepath to read from
   * @return Autoclosable instance.
   * @throws IOException
   */
  public static Neo4jTestUnit create(Driver driver, String filePath) throws IOException {
    return new Neo4jTestUnit(driver, filePath);
  }

  @Override public void close() {
    runStatement("MATCH (n)"
        + "DETACH DELETE n");
  }

  private void runStatement(String statement) {
    try (Session session = driver.session(AccessMode.WRITE)) {
      session.run(statement);
    }
  }

  private void initDb(InputStream stream) throws IOException {
    final String statement = IOUtils.toString(stream);
    runStatement(statement);
  }

  private void initDb(String filePath) throws IOException {
    final String statement = readLinesFromFile(filePath);
    runStatement(statement);
  }

  private String readLinesFromFile(String filePath) throws IOException {
    try (final FileInputStream is = new FileInputStream(filePath)) {
      return IOUtils.toString(is);
    }
  }
}
