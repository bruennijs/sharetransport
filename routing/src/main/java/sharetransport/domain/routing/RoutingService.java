package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sharetransport.domain.routing.path.Path;

/**
 * 1. Calculates distance weights between many hops of one ride community
 * 1.1 Persisting to neo4j
 * 2. Find clusters of hops that are close to each other
 * 3. Find paths with high passenger density / workload in relation to short mean trip time for each passenger
 *
 * @author Oliver Brüntje
 */
public class RoutingService {

  private HopRepository repository;

  public RoutingService(HopRepository repository) {
    this.repository = notNull(repository, "repository cannot be null");
  }

  /**
   * Finds a set of ordered lists of paths. Each set entry is a list of distinct paths. The list of paths is ordered by its duration.
   * Each list contains paths of alternative path to connect all hops of that path. The hops of one list cannot be part of any other list.
   * @param specification
   * @return Set of ordered lists of path.
   */
  public Set<List<Path>> findDistinctPaths(RideCommunitySpecification specification) {
    specification.getTrips().stream()
        .flatMap(RoutingService::createHopsFromTrip)
        .map(this::persistHops)
        .collect(Collectors.toList());

    return Collections.singleton(Collections.EMPTY_LIST);
  }

  private Hop persistHops(Hop hop) {
    return this.repository.createOrUpdate(hop);
  }

  private static Stream<Hop> createHopsFromTrip(Trip trip) {
    final Hop origin = new Hop(trip.getOrigin().getUid(), true, false, trip.getOrigin().getPoint());
    final Hop destination = new Hop(trip.getDestination().getUid(), false, true, trip.getDestination().getPoint());

    return Stream.of(origin, destination);
  }
}
