package sharetransport.domain.routing.path;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Map;

import sharetransport.domain.routing.Hop;

/**
 * Contains a list of hops in an ordered list which is the order of an valid route
 * do a transport between them. the sum of weights is the overall weight if doing a transport
 * along this route.
 * @author Oliver Brüntje
 */
public class Path {

  private final Integer duration;

  private final List<Hop> route;

  private final Map<String, Integer> tripWeights;

  public List<Hop> getRoute() {
    return route;
  }

  public Path(List<Hop> hops, Integer duration, final Map<String, Integer> tripWeights) {
    this.route = notNull(hops, "hops cannot be null");
    this.duration = notNull(duration, "duration cannot be null");
    this.tripWeights = notNull(tripWeights, "tripWeights cannot be null");
  }

  public static Path from(List<Hop> hops, Integer duration, Map<String, Integer> tripWeights) {
    return new Path(hops, duration, tripWeights);
  }

  /**
   * Gets the overall duration of all hops along the route.
   * @return
   */
  public Integer   getDuration() {
    return duration;
  }

  public Map<String, Integer> getTripWeights() {
    return tripWeights;
  }
}
