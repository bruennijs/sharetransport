package sharetransport.infrastructure.persistence.neo4j;

import static org.apache.commons.lang3.Validate.notEmpty;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.neo4j.ogm.driver.Driver;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

/**
 * Creates Sessions per Scope
 *
 * @author Oliver Brüntje
 */
public class Neo4jOgmProvider implements AutoCloseable {

  public static final String DOMAIN_ENTITIES_PACKAGES = "sharetransport.domain";

  private SessionFactory sessionFactory;

  protected Neo4jOgmProvider() {
  }

  public Neo4jOgmProvider(String neo4jUri) {
    initSessionFactory(notEmpty(neo4jUri, "String  cannot be empty"));
  }

  private  void initSessionFactory(String uri) {
    final Configuration configuration = new Configuration.Builder()
        .uri(uri)
        //.withBasePackages("sharetransport.domain.routing")
        .connectionPoolSize(15)
        .build();

    this.sessionFactory = new SessionFactory(configuration, DOMAIN_ENTITIES_PACKAGES);
  }

  public Neo4jOgmProvider(Driver driver) {
    this.sessionFactory = new SessionFactory(driver, DOMAIN_ENTITIES_PACKAGES);
  }

  @Produces
  @RequestScoped
  public Session createSession() {
     return this.sessionFactory.openSession();
  }

  public void onSessionScopeDispose(@Disposes Session session) {
    session.clear();
  }

  @Override public void close() throws Exception {
    if (sessionFactory != null) {
      this.sessionFactory.close();
      this.sessionFactory = null;
    }
  }
}
