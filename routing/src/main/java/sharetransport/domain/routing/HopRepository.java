package sharetransport.domain.routing;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;
import java.util.Optional;

import org.neo4j.driver.v1.Value;
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

  /**
   * Find by uid
   * @param uid
   * @return
   */
  public Optional<Hop> findByUid(String uid) {
    final Value params = parameters("uid", uid);

    return Optional.ofNullable(getSession().queryForObject(getEntityType(), "MATCH (h:Hop) WHERE h.uid = {uid} RETURN h as hop", params.asMap()));
  }

  public Class<Hop> getEntityType() {
    return Hop.class;
  }
}
