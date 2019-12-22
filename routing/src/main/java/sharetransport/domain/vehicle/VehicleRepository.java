package sharetransport.domain.vehicle;

import org.neo4j.ogm.session.Session;

import sharetransport.infrastructure.persistence.neo4j.Neo4jAbstractRepository;

/**
 * Repository
 *
 * @author Oliver Brüntje
 */
public class VehicleRepository extends Neo4jAbstractRepository<Vehicle> {
  /**
   * Ctor
   * @param session
   */
  public VehicleRepository(Session session) {
    super(session);
  }

  public Class<Vehicle> getEntityType() {
    return Vehicle.class;
  }
}
