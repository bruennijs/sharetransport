package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notNull;
import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;

/**
 * Finds optimal routes between hops
 *
 * @author Oliver Brüntje
 */
public class RoutingService {

  private Session session;

  public RoutingService(Session session) {
    this.session = notNull(session, "session cannot be null");
  }

  /**
   * Finds shortest path visiting all hops. Only routes where destination hops are
   * behind their origin hops are returned.
   * @param hops all hops to navigate to.
   * @return List of possible routes
   */
  public List<RouteSpecification> findRoutesByHops(List<Hop> hops) {

    hops = hops.stream().distinct().collect(Collectors.toList());

    final Value parameters = parameters("hopIds", hops.stream().map(h -> h.getId()).collect(Collectors.toList()));

      final StatementResult result = session.run(String.format("MATCH p=(:Hop)-[d:DISTANCE*%d]->(:Hop)\n"
          + " WHERE ALL (hopId IN {hopIds} WHERE hopId IN [n IN nodes(p) | id(n)])"
          + "   AND sharetransport.domain.routing.areHopsInOrder(p)\n"
          + " RETURN p as route,\n"
          + "       nodes(p) as hops,\n"
          + "       reduce(sum=0, r IN relationships(p) | r.weight + sum) as sumWeights"
          + " ORDER BY sumWeights ASC", numberOfHopEdges(hops)), parameters);

      return result
          .stream()
          .map(record -> RouteSpecification.from(
              record.get("hops").asList(v -> Hop.from(v.asNode())),
              record.get("sumWeights").asInt()))
          .collect(Collectors.toList());
  }

  private Integer numberOfHopEdges(List<Hop> hops) {
    return hops.size() - 1;
  }
}
