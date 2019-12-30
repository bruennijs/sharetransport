package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.RelationshipEntity;

import sharetransport.infrastructure.domain.AbstractIdentifiable;

/**
 * A hop is a point on land a transport must address to let a load/person
 * hop on or hop off.
 *
 * @author Oliver Brüntje
 */
@NodeEntity(label = "Hop")
public class Hop extends AbstractIdentifiable<Long> {

  public static final String PROPERTY_DESTINATION = "destination";

  public static final String PROPERTY_ORIGIN = "origin";

  private static final String PROPERTY_UID = "uid";

  private String uid;

  private Boolean origin;

  private Boolean destination;

  @Relationship(type = HopDistance.TYPE, direction = Relationship.OUTGOING)
  private Set<HopDistance> distancesTo;

  @Relationship(type = HopDistance.TYPE, direction = Relationship.INCOMING)
  private Set<HopDistance> distancesIncoming;

  /**
   * For OGM
   */
  public Hop() {
    super(null);
  }

  public Hop(String uid, Boolean origin, Boolean destination) {
    this(null, uid, origin, destination);
  }

  public Hop(Long id, String uid, Boolean origin, Boolean destination) {

    super(id);
    this.uid = notEmpty(uid, "String uid cannot be empty");
    this.origin = notNull(origin, "origin cannot be null");
    this.destination = notNull(destination, "destination cannot be null");
    this.distancesTo = new HashSet<>();
    this.distancesIncoming = new HashSet<>();
  }

  public static Hop from(Node node) {
    return new Hop(node.id(),
        node.get(PROPERTY_UID).asString(),
        node.get(PROPERTY_ORIGIN).asBoolean(false),
        node.get(PROPERTY_DESTINATION).asBoolean(false));
  }

  public String getUid() {
    return uid;
  }

  public Boolean getOrigin() {
    return origin;
  }

  public Boolean getDestination() {
    return destination;
  }

  public Set<HopDistance> getDistancesTo() {
    return distancesTo;
  }

  public Set<HopDistance> getDistancesIncoming() {
    return distancesIncoming;
  }

  public enum Relation {
    BOOKED_TO("BOOKED_TO"),
    DISTANCE ("DISTANCE");

    private String value;

    Relation(String value) {
      this.value = value;
    }
  }
}
