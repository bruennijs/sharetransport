package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

import org.neo4j.driver.v1.types.Path;

/**
 * Contains a list of hops in an ordered list which is the order of an valid route
 * do a transport between them. the sum of weights is the overall weight if doing a transport
 * along this route.
 * @author Oliver Brüntje
 */
public class RouteSpecification {

  private final Integer sumWeights;

  private List<Hop> hops;

  public RouteSpecification(List<Hop> hops, Integer sumWeights) {
    this.hops = notNull(hops, "hops cannot be null");
    this.sumWeights = notNull(sumWeights, " cannot be null");
  }

  public List<Hop> getHops() {
    return hops;
  }

  public static RouteSpecification from(List<Hop> hops, Integer sumWeights, Path path) {
    return new RouteSpecification(hops, sumWeights);
  }

  /**
   * Gets the overall weight of all hops along the route.
   * @return
   */
  public Integer getWeight() {
    return sumWeights;
  }
}
