package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import sharetransport.infrastructure.domain.AbstractIdentifiable;
import sharetransport.infrastructure.domain.geo.Location;
import sharetransport.infrastructure.persistence.neo4j.converter.LocationConverter;

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

  public static final String PROPERTY_LATITUDE = "latitude";

  public static final String PROPERTY_LONGITUDE = "longitude";

  private static final String RELATION_BOOKEDTO_TYPE = "BOOKED_TO";

  @Convert(LocationConverter.class)
  private Location location;

  private Boolean origin;

  private Boolean destination;

  @Relationship(type = HopDistance.TYPE, direction = Relationship.OUTGOING)
  private Set<HopDistance> distancesTo;

  @Relationship(type = HopDistance.TYPE, direction = Relationship.INCOMING)
  private Set<HopDistance> distancesIncoming;

  @Relationship(type = RELATION_BOOKEDTO_TYPE, direction = Relationship.OUTGOING)
  private Hop bookedTo;

  /**
   * For OGM
   */
  protected Hop() {
    super(null, null);
  }

  /**
   * Ctor for domain logic.
   * @param uid
   * @param origin
   * @param destination
   * @param location
   */
  public Hop(String uid, Boolean origin, Boolean destination, Location location) {
    this(null, uid, origin, destination, location);
  }

  protected Hop(Long id, String uid, Boolean origin, Boolean destination, Location location) {
    super(id, uid);
    this.origin = notNull(origin, "origin cannot be null");
    this.destination = notNull(destination, "destination cannot be null");
    this.location = notNull(location, "location cannot be null");
    this.distancesTo = new HashSet<>();
    this.distancesIncoming = new HashSet<>();
  }

  public Boolean isOrigin() {
    return origin;
  }

  public Boolean isDestination() {
    return destination;
  }

  public Set<HopDistance> getDistancesTo() {
    return Collections.unmodifiableSet(this.distancesTo);
  }

  public Set<HopDistance> getDistancesIncoming() {
    return Collections.unmodifiableSet(this.distancesIncoming);
  }

  public Hop getBookedTo() {
    return this.bookedTo;
  }

  /**
   * Adds distance to relation.
   * @param to from this to to hop
   * @param duration duration
   */
  public void addDistanceTo(Hop to, Duration duration) {
    this.distancesTo.add(new HopDistance(this, to, Long.valueOf(duration.getSeconds()).intValue()));
  }

  public Location getLocation() {
    return this.location;
  }

  /**
   * Creates booked to relation
   * @param destination destination hop
   */
  public void bookedTo(Hop destination) {
    this.bookedTo = destination;
    this.origin = true;
    destination.destination = true;
  }
}
