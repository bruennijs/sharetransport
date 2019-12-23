package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notNull;
import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;
import java.util.stream.Collectors;

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
      final Value parameters = parameters(
          "hopIds", hops.stream().map(h -> h.getId()).collect(Collectors.toList()),
          "distanceRelationCount", hops.size() - 1);

      final StatementResult result = session.run("MATCH p=(:Hop)-[d:DISTANCE*3]->(:Hop)\n"
          + " WHERE ALL (hopId IN {hopIds} WHERE hopId IN [n IN nodes(p) | id(n)])"
          + "   AND sharetransport.domain.routing.areHopsInOrder(p)\n"
          + " RETURN p as route,\n"
          + "       nodes(p) as hops,\n"
          + "       reduce(sum=0, r IN relationships(p) | r.weight + sum) as sumWeights"
          + " ORDER BY sumWeights ASC", parameters);

      return result
          .stream()
          .map(r -> RouteSpecification.from(r.get("hops").asList(v -> Hop.from(v.asNode()))))
          .collect(Collectors.toList());
  }
}
