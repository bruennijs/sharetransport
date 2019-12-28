package sharetransport.infrastructure.domain;

/**
 * Provides identifiable behavior for an entity.
 */
public interface Identifiable<I> {

  /**
   * @return The primary key, or ID of this entity
   */
  I getId();
}

