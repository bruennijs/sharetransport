package sharetransport.infrastructure.persistence.neo4j;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.neo4j.ogm.session.Session;

/**
 * Creates Sessions per Scope
 *
 * @author Oliver Brüntje
 */
public class Neo4jProvider {

  @Produces
  @RequestScoped
  public Session createSession() {
     return Neo4jSessionFactory.getInstance().getNeo4jSession();
  }

  public void onSessionScopeDispose(@Disposes Session session) {
  }
}
