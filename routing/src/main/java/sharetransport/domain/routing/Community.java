package sharetransport.domain.routing;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import sharetransport.infrastructure.domain.AbstractIdentifiable;
import sharetransport.infrastructure.domain.geo.DistanceMetric;
import sharetransport.infrastructure.domain.geo.RideMetric;

/**
 * A ride community is a set of passengers provided origin and destination hops
 * that want to ride in a similar span of time so that they can build ride community
 * and share a tour. They DO NOT HAVE TO share the same car cause there can be a lot of them
 * and they must be divided into different vehicles.
 * Next step is to cluster them and to find optimal routes for all of them
 *
 * @author Oliver Brüntje
 */
@NodeEntity(label = "COMMUNITY")
public class Community extends AbstractIdentifiable<Long> {

  private static final String RELATION_CONTAINS_TYPE = "CONTAINS";

  @Relationship(type = RELATION_CONTAINS_TYPE, direction = Relationship.OUTGOING)
  private Set<Hop> hops;

  /**
   * Neo4j OGM
   */
  protected Community() {
    super(null, null);
  }

  public Community(String uid) {
    super(null, uid);
    this.hops = new HashSet<>();
  }

  public Set<Hop> getHops() {
    return Collections.unmodifiableSet(this.hops);
  }

  public void contains(Hop hop) {
    this.hops.add(hop);
  }

  /**
   * Calculates distances between all permutations of hops contained in this community
   * by using the given metric.
   * @param distanceMetric strategy to use for calculating distances between hops
   */
  public void calculateDistances(DistanceMetric distanceMetric) {
    final Map<Hop, List<Hop>> hopsDotProduct = sharetransport.infrastructure.collection.Collections.dotJoin(hops, hops);

    hopsDotProduct.forEach((outerHop, value) -> {
      value.stream().forEach(innerHop -> {
        final Pair<Duration, Double> distances = distanceMetric.apply(outerHop.getLocation(), innerHop.getLocation());

        if (distances.getLeft() != Duration.ZERO) {
          outerHop.durationTo(innerHop, distances.getLeft());
        } else {
          final Duration rideDuration = RideMetric.estimateDuration(distances.getRight());
          outerHop.durationTo(innerHop, rideDuration);
        }
      });
    });
  }

  /**
   * Fidns a contained hop by its uid.
   * @param uid uid of hop.
   * @return Optinal hop ; empptry of not found.
   */
  public Optional<Hop> getHopByUid(String uid) {
    return this.hops.stream().filter(hop -> hop.getUid().equals(uid)).findFirst();
  }
}
