package sharetransport.domain.routing.path;

import static org.apache.commons.lang3.Validate.notNull;
import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;

import sharetransport.domain.routing.Hop;
import sharetransport.infrastructure.domain.AbstractIdentifiable;
import sharetransport.infrastructure.domain.geo.Location;

/**
 * Finds optimal routes between hops
 *
 * @author Oliver Brüntje
 */
public class PathService {

  private Session session;

  public PathService(Session session) {
    this.session = notNull(session, "session cannot be null");
  }

  /**
   * Finds shortest path visiting all hops. Only routes where destination hops are
   * behind their origin hops are returned.
   * @param hops all hops to navigate to.
   * @return List of possible routes
   */
  public List<Path> findPathByHops(List<Hop> hops) {

    hops = hops.stream().distinct().collect(java.util.stream.Collectors.toList());

    final Value parameters = parameters("hopUids", hops.stream().map(h -> h.getUid()).collect(java.util.stream.Collectors.toList()));

      final StatementResult result = session.run(String.format("MATCH p=(root:Hop)-[d:DISTANCE*1..%d]->(end:Hop)\n"
          + "  WHERE apoc.coll.isEqualCollection([n IN nodes(p) | n.uid], {hopUids})\n"
          + "  AND de.bruenni.sharetransport.neo4j.routing.areHopsInOrder(p)\n"
          + "WITH p AS path,\n"
          + "      root,\n"
          + "     nodes(p) AS hops,\n"
          + "     filter (n IN nodes(p) WHERE n.origin = true) AS origins\n"
          + "UNWIND origins AS origin\n"
          + "MATCH (origin)-[:BOOKED_TO]->(destination:Hop)\n"
          + "WITH path,\n"
          + "     hops,\n"
          + "     origin,\n"
          + "     de.bruenni.sharetransport.neo4j.routing.weightOf(root, origin, path) AS tripWaitWeight,\n"
          + "     de.bruenni.sharetransport.neo4j.routing.weightOf(origin, destination, path) AS tripWeight\n"
          + "RETURN hops,\n"
          + "       apoc.map.mergeList ( collect (apoc.map.fromLists([origin.uid], [tripWeight]))) AS tripWeights,\n"
          + "       reduce(sum=0, r IN relationships(path) | r.weight + sum) AS pathWeight\n"
          + "ORDER BY pathWeight ASC;", numberOfHopEdges(hops)), parameters);

      return result
          .stream()
          .map(PathService::fromRecord)
          .collect(java.util.stream.Collectors.toList());
  }

  private Integer numberOfHopEdges(List<Hop> hops) {
    return hops.size() - 1;
  }

  private static Path fromRecord(Record record) {
    final List<Hop> hops = record.get("hops").asList(hop -> createHopFromNode(hop.asNode()));
    final Map<String, Integer> tripWeights = record.get("tripWeights").asMap(value -> value.asInt());

    return Path.from(
        hops,
        record.get("pathWeight").asInt(),
        tripWeights);
  }

  private static Hop createHopFromNode(Node node) {
    return new TestHop(node.id(),
        node.get(AbstractIdentifiable.PROPERTY_UID).asString(),
        node.get(Hop.PROPERTY_ORIGIN).asBoolean(false),
        node.get(Hop.PROPERTY_DESTINATION).asBoolean(false),
        new Location(8.1, 57.1));
  }
  private static class TestHop extends Hop {
    public TestHop(long id, String uid, boolean asBoolean, boolean asBoolean1, Location location) {
      super(id, uid, asBoolean, asBoolean1, location);
    }

  }
}
