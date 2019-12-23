package sharetransport.domain.routing;

import java.util.Arrays;
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

  @Context
  public GraphDatabaseService db;

  @Context
  public Log log;

  @UserFunction
  @Description("sharetransport.domain.routing.areHopsInOrder(Path path) - checks wheter path has Orig node before destination of same trip")
  public Boolean areHopsInOrder(@Name(value = "path") Path path) {
    //path.nodes().forEach(n -> n.getProperty());
    log.info("HELLO WORLD");
    final List<Boolean> collect = nodeStream(path)
        .filter(n -> isHopADestination(n))
        .map(n -> isOriginHopBeforeDestinationHopInPath(n, path))
        .collect(Collectors.toList());

    //collect.stream().forEach(n -> log.info("n.relation degree=" + n.getDegree()));
    return collect.stream().allMatch(b -> b);
  }

  private Boolean isHopADestination(Node n) {
    if (n.hasProperty(Hop.PROPERTY_DESTINATION)) {
      return (Boolean)n.getProperty(Hop.PROPERTY_DESTINATION);
    }

    return false;
  }

  private Boolean isOriginHopBeforeDestinationHopInPath(Node hopDestination, Path path) {
    final Optional<Node> hopOrigin = findOriginHop(hopDestination);

    if (hopOrigin.isPresent()) {
      return isOriginBeforeDestination(hopOrigin.get(), hopDestination, path);
    }

    return false;
  }

  private Boolean isOriginBeforeDestination(Node hopOrigin, Node hopDestination, Path path) {
    final List<Node> nodelist = nodeStream(path).collect(Collectors.toList());
    return nodelist.indexOf(hopOrigin) < nodelist.indexOf(hopDestination);
  }

  private Stream<Node> nodeStream(Path path) {
    return StreamSupport.stream(path.nodes().spliterator(), false);
  }

  private Optional<Node> findOriginHop(Node n) {
    final Relationship bookedTo = n.getSingleRelationship(() -> Hop.Relation.BOOKED_TO.name(), Direction.INCOMING);
    if (bookedTo != null) {
      return Optional.of(bookedTo.getStartNode());
    }
    return Optional.empty();
  }
}
