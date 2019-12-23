package sharetransport.domain.routing;

import org.neo4j.ogm.session.Session;

import sharetransport.domain.routing.Hop;
import sharetransport.infrastructure.persistence.neo4j.Neo4jAbstractRepository;

/**
 * Repository
 *
 * @author Oliver Brüntje
 */
public class HopRepository extends Neo4jAbstractRepository<Hop> {
  /**
   * Ctor
   * @param session
   */
  public HopRepository(Session session) {
    super(session);
  }

  public Class<Hop> getEntityType() {
    return Hop.class;
  }
}
