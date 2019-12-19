package sharetransport.infrastructure.persistence.neo4j;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

/**
 * https://neo4j.com/docs/ogm-manual/current/tutorial/#tutorial:configuration
 *
 * @author Oliver Brüntje
 */
public class Neo4jSessionFactory {

  private final static Configuration configuration = null; // provide configuration as seen before
  private final static SessionFactory sessionFactory = new SessionFactory(configuration, "de.bruenni.sharetransport.domain");
  private static Neo4jSessionFactory factory = new Neo4jSessionFactory();

  public static Neo4jSessionFactory getInstance() {
    return factory;
  }

  // prevent external instantiation
  private Neo4jSessionFactory() {
  }

  public Session getNeo4jSession() {
    return sessionFactory.openSession();
  }
}
