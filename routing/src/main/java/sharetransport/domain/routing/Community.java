package sharetransport.domain.routing;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import sharetransport.infrastructure.domain.AbstractIdentifiable;

/**
 * A ride community is a set of passengers provided origin and destination hops
 * that want to ride in a similar span of time so that they can build ride community
 * and share a tour. They DO NOT HAVE TO share the same car cause there can be a lot of them
 * and they must be divided into different vehicles.
 * Next step is to cluster them and to find optimal routes for all of them
 *
 * @author Oliver Brüntje
 */
@NodeEntity(label = "COMMUNITY")
public class Community extends AbstractIdentifiable<Long> {

  private static final String RELATION_CONTAINS_TYPE = "CONTAINS";

  @Relationship(type = RELATION_CONTAINS_TYPE, direction = Relationship.OUTGOING)
  private Set<Hop> containsRelation;

  /**
   * Neo4j OGM
   */
  protected Community() {
    super(null, null);
  }

  public Community(String uid) {
    super(null, uid);
    this.containsRelation = new HashSet<>();
  }

  public Set<Hop> getHops() {
    return Collections.unmodifiableSet(this.containsRelation);
  }

  public void contains(Hop hop) {
    this.containsRelation.add(hop);
  }
}
