package sharetransport.domain.transport;

import org.neo4j.ogm.session.Session;

import sharetransport.infrastructure.persistence.neo4j.Neo4jAbstractRepository;

/**
 * Repository
 *
 * @author Oliver Brüntje
 */
public class TourRepository extends Neo4jAbstractRepository<Person> {
  /**
   * Ctor
   * @param session
   */
  public TourRepository(Session session) {
    super(session);
  }

  public void findRouteForPersons(Long personId) {
    //final Result result = this.getSession().query("", new HashMap<>());
  }

  public Class<Person> getEntityType() {
    return Person.class;
  }
}
