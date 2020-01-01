package sharetransport.infrastructure.domain.geo;

import static org.apache.commons.lang3.Validate.notNull;

import java.time.Duration;

/**
 * Metrics for a vehicle ride
 */
public class RideMetric {

  public RideMetric() {}

  /**
   * Estimates duration by distance using velocity of 50 km/h as average.
   * @param distanceInMeters meters of the ride
   * @return Duration needed for given ride length.
   */
  public static Duration estimateDuration(Double distanceInMeters) {
    final Double durationInSeconds = distanceInMeters / 13.89;
    return Duration.ofSeconds(durationInSeconds.longValue());
  }
}
