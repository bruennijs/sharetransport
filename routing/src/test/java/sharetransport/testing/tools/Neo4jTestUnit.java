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

  private Session session;

  private Neo4jTestUnit(Session session, String filePath) throws IOException {

    this.session = notNull(session, "session cannot be null");
    initDb(filePath);
  }

  private Neo4jTestUnit(Session session, InputStream stream) throws IOException {

    this.session = notNull(session, "session cannot be null");
    deleteAllNodes();
    initDb(stream);
  }

  /**
   * Creates test unit instance loading statements from file into db using driver object.
   * @param session Session to use.
   * @param stream stream to read statements from.
   * @return Autoclosable instance.
   * @throws IOException
   */
  public static Neo4jTestUnit create(Session session, InputStream stream) throws IOException {
    return new Neo4jTestUnit(session, stream);
  }

  @Override public void close() {
  }

  private void runStatement(String statement) {
    session.run(statement);
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

  private void deleteAllNodes() {
    runStatement("MATCH (n)"
        + "DETACH DELETE n");
  }
}
