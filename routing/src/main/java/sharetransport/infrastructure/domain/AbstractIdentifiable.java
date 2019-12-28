package sharetransport.infrastructure.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

/**
 * Implements equals using id.
 *
 * @author Oliver Brüntje
 */
public abstract class AbstractIdentifiable<I> implements Identifiable<I> {

  @Id
  @GeneratedValue
  private I id;

  protected AbstractIdentifiable(I id) {
    this.id = id;
  }

  @Override public I getId() {
    return this.id;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;

    if (!(o instanceof AbstractIdentifiable))
      return false;

    AbstractIdentifiable<?> that = (AbstractIdentifiable<?>)o;

    return new EqualsBuilder()
        .append(id, that.id)
        .isEquals();
  }

  @Override public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(id)
        .toHashCode();
  }
}
