package sharetransport.infrastructure.persistence;

/**
 * Abstract repo
 *
 * @author Oliver Brüntje
 */
public interface AbstractRepository<T> {

  Iterable<T> findAll();

  T find(Long id);

  void delete(Long id);

  T createOrUpdate(T object);
}
