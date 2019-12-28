package de.bruenni.sharetransport.neo4j.routing;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.PathExpanderBuilder;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.kernel.impl.traversal.TraversalBranchWithState;

/**
 * Iterates over path following a given relationship.
 *
 * @author Oliver Brüntje
 */
public class PathTraverser {
  /**
   * Create forward stream of nodes.
   * @param path
   * @return
   */
  static Stream<Node> nodeStream(Path path) {
    return StreamSupport.stream(path.nodes().spliterator(), false);
  }

  public static Stream<Relationship> traverse(Path path, RelationshipType following) {
/*    final PathExpander<Object> expander = PathExpanderBuilder.empty()
        .add(following, Direction.OUTGOING).build();*/

    final Iterable<Relationship> expander = PathExpanders
        .forTypeAndDirection(following, Direction.OUTGOING)
        .expand(path, BranchState.NO_STATE);
    return StreamSupport.stream(expander.spliterator(), false);
  }
}
