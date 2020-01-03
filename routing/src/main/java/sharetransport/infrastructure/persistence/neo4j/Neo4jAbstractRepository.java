package sharetransport.infrastructure.persistence.neo4j;

import static org.apache.commons.lang3.Validate.notNull;

import javax.inject.Inject;

import org.neo4j.ogm.session.Session;

import sharetransport.infrastructure.domain.AbstractRepository;
import sharetransport.infrastructure.domain.Identifiable;

/**
 *
 * @author Oliver Brüntje
 */
public abstract class Neo4jAbstractRepository<T extends Identifiable<Long>> implements AbstractRepository<T> {

  private static final int DEPTH_LIST = 0;
  private static final int DEPTH_ENTITY = 1;  // Persist entity with all its related nodes

  private final Session session;

  @Inject
  protected Neo4jAbstractRepository(Session session) {
    this.session = notNull(session, "session cannot be null");
  }

  protected Session getSession() {
    return session;
  }

  public Iterable<T> findAll() {
    return session.loadAll(getEntityType(), DEPTH_LIST);
  }

  public T find(Long id) {
    return session.load(getEntityType(), id, DEPTH_ENTITY);
  }

  public void delete(Long id) {
    session.delete(session.load(getEntityType(), id));
  }

  public T createOrUpdate(T entity) {
    session.save(entity, DEPTH_ENTITY);
    return find(entity.getId());
  }

  public abstract Class<T> getEntityType();
}
