package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notNull;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import sharetransport.infrastructure.domain.AbstractIdentifiable;

/**
 * A gop distance is a edge between to hops containing the distance to another hop
 * containing a weight.
 *
 * @author Oliver Brüntje
 */
@RelationshipEntity(type = HopDistance.TYPE)
public class HopDistance extends AbstractIdentifiable<Long> {

  public static final String TYPE = "DISTANCE";

  @StartNode private Hop origin;

  @EndNode private Hop destination;

  private Integer weight;

  public HopDistance() {
    super(null);
  }

  public HopDistance(Hop origin, Hop destination, Integer weight) {
    super(null);
    this.origin = notNull(origin, "origin cannot be null");
    this.destination = notNull(destination, "destination cannot be null");
    this.weight = notNull(weight, "weight cannot be null");
  }

  public Hop getOrigin() {
    return origin;
  }

  public Hop getDestination() {
    return destination;
  }

  public Integer getWeight() {
    return weight;
  }
}
