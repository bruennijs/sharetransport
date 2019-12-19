package sharetransport.domain;

import java.util.HashMap;

import org.neo4j.ogm.session.Session;

import sharetransport.infrastructure.persistence.neo4j.Neo4jAbstractRepository;

/**
 * Repository
 *
 * @author Oliver Brüntje
 */
public class RouteRepository extends Neo4jAbstractRepository<Person> {
  /**
   * Ctor
   * @param session
   */
  public RouteRepository(Session session) {
    super(session);
  }

  public void findRouteForPersons(Long personId) {
    //this.getSession().query("", new HashMap<>()).queryResults().
  }

  public Class<Person> getEntityType() {
    return Person.class;
  }
}
