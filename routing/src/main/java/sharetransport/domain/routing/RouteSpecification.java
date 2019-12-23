package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

/**
 * Contains a list of hops in an ordered list which is the order of an valid route
 * do a transport between them. the sum of weights is the overall weight if doing a transport
 * along this route.
 * @author Oliver Brüntje
 */
public class RouteSpecification {

  private List<Hop> hops;

  public RouteSpecification(List<Hop> hops) {
    this.hops = notNull(hops, "hops cannot be null");
  }

  public List<Hop> getHops() {
    return hops;
  }

  public static RouteSpecification from(List<Hop> hops) {
    return new RouteSpecification(hops);
  }

  /**
   * Gets the overall weight of all hops along the route.
   * @return
   */
  public Integer getWeight() {
    return 0;
  }
}
