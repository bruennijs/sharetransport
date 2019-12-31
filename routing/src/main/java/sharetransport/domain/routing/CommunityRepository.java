package sharetransport.domain.routing;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.Optional;

import org.neo4j.driver.v1.Value;
import org.neo4j.ogm.session.Session;

import sharetransport.infrastructure.persistence.neo4j.Neo4jAbstractRepository;

/**
 * Repository for persisting community entities.
 *
 * @author Oliver Brüntje
 */
public class CommunityRepository extends Neo4jAbstractRepository<Community> {

  /**
   * Ctor
   * @param session
   */
  public CommunityRepository(Session session) {
    super(session);
  }

  public Class<Community> getEntityType() {
    return Community.class;
  }
}
