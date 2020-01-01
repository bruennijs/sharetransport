package sharetransport.infrastructure.collection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;

import sharetransport.domain.routing.Hop;

/**
 * Collections utils
 *
 * @author Oliver Brüntje
 */
public class Collections {
  /**
   * dot product of two collections where ai != bi
   * @param a collection of R
   * @param b collection of S
   * @param <R>
   * @param <S>
   * @return Dot product matrix
   */
  public static <R, S> Map<R, List<S>> dotJoin(Collection<R> a, Collection<S> b) {
    return a.stream()
        .map(ai -> ImmutablePair.of(ai, b.stream().filter(bi -> bi != ai).collect(Collectors.toList())))
        .collect(Collectors.toMap(pair -> pair.getLeft(), pair2 -> pair2.getRight()));
  }
}
