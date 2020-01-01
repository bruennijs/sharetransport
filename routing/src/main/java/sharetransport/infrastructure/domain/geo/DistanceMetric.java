package sharetransport.infrastructure.domain.geo;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import sharetransport.domain.routing.Hop;
import sharetransport.domain.routing.HopDistance;

/**
 * Strategy for calculating distances between two hops, e.g. harvesine algorithm
 * or using database for calculating distances.
 *
 * @author Oliver Brüntje
 */
public class DistanceMetric implements BiFunction<Location, Location, Pair<Duration, Double>> {

  /**
   * Calculates duration and distance in meters for the given coordinates.
   * @param from coordinate from where to get distance for
   * @param to coordinate where to get distamce for.
   * @return
   */
  @Override public Pair<Duration, Double> apply(Location from, Location to) {
    double distInMeters = calculateDistanceInMeters(from, to);
    return ImmutablePair.of(Duration.ZERO, distInMeters);
  }

  private double calculateDistanceInMeters(Location p1, Location p2) {
    double earthRadius = 6371000; //meters
    double dLat = Math.toRadians(p2.getLatitude()-p1.getLatitude());
    double dLng = Math.toRadians(p2.getLongitude()-p1.getLongitude());
    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(p2.getLatitude())) *
            Math.sin(dLng/2) * Math.sin(dLng/2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return (earthRadius * c);
  }
}
