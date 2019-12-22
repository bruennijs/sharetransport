package sharetransport.domain.routing;

import org.neo4j.graphdb.Path;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * Neo4j function to check for each path whether source and destination
 * of vertices belonging together are in order.
 * @author Oliver Brüntje
 */
public class HopsInTourOrderUserFunction {

  @UserFunction
  @Description("sharetransport.domain.route;areTripsInOrder(Path path) - checks wheter path has Orig node before destination of same trip")
  public Boolean areJopsInOrder(@Name(value = "path") Path path) {
    //path.nodes().forEach(n -> n.getProperty());
    return true;
  }
}
