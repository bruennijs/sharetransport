package de.bruenni.sharetransport.neo4j.routing;

import java.net.SocketOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * Neo4j function to check for each path whether source and destination
 * of vertices belonging together are in order.
 * @author Oliver Brüntje
 */
public class HopsInTourOrderUserFunction {

  private static final String PROPERTY_DESTINATION = "destination";

  private static final String RELATIONSHIP_BOOKED_TO = "BOOKED_TO";

  @Context
  public GraphDatabaseService db;

  @Context
  public Log log;

  @UserFunction
  @Description("sharetransport.domain.routing.areHopsInOrder(Path path) - checks wheter path has Orig node before destination of same trip")
  public Boolean areHopsInOrder(@Name(value = "path") Path path) {
    final List<Boolean> collect = nodeStream(path)
        .filter(n -> isHopADestination(n))
        .map(n -> isOriginHopBeforeDestinationHopInPath(n, path))
        .collect(Collectors.toList());

    return collect.stream().allMatch(b -> b);
  }

  private Boolean isHopADestination(Node n) {
    if (n.hasProperty(PROPERTY_DESTINATION)) {
      return (Boolean)n.getProperty(PROPERTY_DESTINATION);
    }

    return false;
  }

  private Boolean isOriginHopBeforeDestinationHopInPath(Node hopDestination, Path path) {
    final List<Node> originHops = findOriginHops(hopDestination);

    return originHops.stream().allMatch(origin -> {
      final List<Node> hopsOfPath = nodeStream(path).collect(Collectors.toList());
      return isOriginBeforeDestination(origin, hopDestination, hopsOfPath);
    });
  }

  private Boolean isOriginBeforeDestination(Node hopOrigin, Node hopDestination, List<Node> nodesOfPath) {
    return nodesOfPath.indexOf(hopOrigin) < nodesOfPath.indexOf(hopDestination);
  }

  private Stream<Node> nodeStream(Path path) {
    return StreamSupport.stream(path.nodes().spliterator(), false);
  }

  private List<Node> findOriginHops(Node destination) {
    final Iterable<Relationship> originHops = destination.getRelationships(() -> RELATIONSHIP_BOOKED_TO, Direction.INCOMING);
    return StreamSupport.stream(originHops.spliterator(), false)
        .map(relationship -> relationship.getStartNode())
        .collect(Collectors.toList());
  }
}
