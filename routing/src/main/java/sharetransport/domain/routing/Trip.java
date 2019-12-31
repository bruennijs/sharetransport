package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import sharetransport.infrastructure.domain.AbstractValueObject;
import sharetransport.infrastructure.domain.geo.Location;

/**
 * A trip is a connection between a origin geo point and a destination geo point
 * and describes a demand of a passenger to ride from origin to destination.
 *
 * @author Oliver Brüntje
 */
public class Trip extends AbstractValueObject {

  private Trip.Hop origin;

  private Trip.Hop destination;

  public Trip(Hop origin, Hop destination) {
    this.origin = notNull(origin, "origin cannot be null");;
    this.destination = notNull(destination, "destination cannot be null");
  }

  public Hop getOrigin() {
    return origin;
  }

  public Hop getDestination() {
    return destination;
  }

  @Override protected Object[] values() {
    return new Object[] {origin, destination };
  }

  public static class Hop extends AbstractValueObject {
    private final Location point;

    private final String uid;

    public Hop(String uid, Location point) {
      this.uid = notEmpty(uid, "String uid cannot be empty");
      this.point = notNull(point, "point cannot be null");
    }

    public Location getPoint() {
      return point;
    }

    public String getUid() {
      return uid;
    }

    @Override protected Object[] values() {
      return new Object[] {point, uid};
    }
  }
}
