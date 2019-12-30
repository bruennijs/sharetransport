package sharetransport.domain.routing.clustering;

import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import sharetransport.domain.routing.RideCommunitySpecification;

/**
 * Before a optimal route can be calculated the hops to get routes for are clustered
 * so that passengers riding from origins close to each other to destinations close to each other too
 * share the same transport vehicle.
 * So try to cluster origin and destination hops in clusters and relate cluster nodes to them.
 *
 * @author Oliver Brüntje
 */
public class ClusteringService {

  public Set<CommunityCluster> findClusters(RideCommunitySpecification specification) {
    throw new NotImplementedException("niy");
  }
}
