package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.util.Set;

import sharetransport.infrastructure.domain.AbstractValueObject;

/**
 * A ride community is a set of passengers that needs to order a transport
 * a the same time so multiple passengers will probably use the same transport vehicle.
 *
 * @author Oliver Brüntje
 */
public class RideCommunitySpecification extends AbstractValueObject {

  /**
   * Unique id of set of trips to find routes for.
   */
  private String communityUid;

  private Set<Trip> trips;

  /**
   *
   * @param communityUid unique id of this set of trips
   * @param trips
   */
  public RideCommunitySpecification(String communityUid, Set<Trip> trips) {
    this.communityUid = notEmpty(communityUid, "String uid cannot be empty");
    this.trips = notEmpty(trips, "trips is empty or null");
  }

  public String getCommunityUid() {
    return communityUid;
  }

  public Set<Trip> getTrips() {
    return trips;
  }

  @Override protected Object[] values() {
    return new Object[] { communityUid, trips};
  }
}
