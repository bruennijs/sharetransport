package sharetransport.domain.hop;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import sharetransport.infrastructure.persistence.Identifiable;

/**
 * Person to be transported.
 *
 * @author Oliver Brüntje
 */
@NodeEntity
public class Person implements Identifiable<Long> {

  @Id
  @GeneratedValue
  private Long id;

  public Long getId() {
    return this.id;
  }
}
