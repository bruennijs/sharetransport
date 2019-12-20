package sharetransport.domain.route;

import java.util.HashMap;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;

import sharetransport.domain.Person;
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
    //final Result result = this.getSession().query("", new HashMap<>());
  }

  public Class<Person> getEntityType() {
    return Person.class;
  }
}
