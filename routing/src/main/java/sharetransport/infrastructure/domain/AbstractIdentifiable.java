package sharetransport.infrastructure.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Labels;
import org.neo4j.ogm.annotation.Property;

/**
 * Implements equals using id.
 *
 * @author Oliver Brüntje
 */
public abstract class AbstractIdentifiable<I> implements Identifiable<I> {

  public static final String PROPERTY_UID = "uid";

  @Id
  @GeneratedValue
  private I id;

  /**
   * Naturual uid of this entity.
   * Needed cause to compare entitities before persisted to Neo4j there is no entity id
   * but a Set uses the equals methid to compare items whether they are already existing in the
   * set.
   */
  @Property(name = PROPERTY_UID)
  private String uid;

  protected AbstractIdentifiable(I id, String uid) {
    this.id = id;
    this.uid = uid;
  }

  @Override public I getId() {
    return this.id;
  }

  public String getUid() {
    return uid;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;

    if (!(o instanceof AbstractIdentifiable))
      return false;

    AbstractIdentifiable<?> that = (AbstractIdentifiable<?>)o;

    return new EqualsBuilder()
        .append(uid, that.uid)
        .isEquals();
  }

  @Override public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(uid)
        .toHashCode();
  }
}
