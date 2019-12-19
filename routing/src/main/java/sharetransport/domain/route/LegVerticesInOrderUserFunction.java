package sharetransport.domain.route;

import org.neo4j.graphdb.Path;
import org.neo4j.procedure.UserFunction;

/**
 * Neo4j function to check for each path whether source and destination
 * of vertices belonging together are in order.
 * @author Oliver Brüntje
 */
public class LegVerticesInOrderUserFunction {

  @UserFunction
  public boolean areLegsInOrder(Path path) {
    return false;
  }
}
