package sharetransport.infrastructure.persistence.neo4j;

import java.net.URI;

import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

/**
 * Autocloasable scope for driver object
 *
 * @author Oliver Brüntje
 */
public class Neo4jDriverScope implements AutoCloseable
{
  private final Driver driver;

  public Neo4jDriverScope( String uri, String user, String password )
  {
    driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
  }

  public Neo4jDriverScope(URI uri, Config config) {
    driver = GraphDatabase.driver( uri, config );
  }

  public Neo4jDriverScope(URI uri, AuthToken auth, Config config) {
    driver = GraphDatabase.driver( uri, auth, config );
  }

  public Driver getDriver() {
    return driver;
  }

  @Override
  public void close() {
    driver.close();
  }
}
