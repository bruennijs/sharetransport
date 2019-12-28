package sharetransport.infrastructure.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Helper methods for projection of collections.
 *
 * @author Oliver Brüntje
 */
public class Collectors {

  /**
   * Reduces all maps of the list to one list.
   * @return
   */
  public static <K, V> Collector<Map<K, V>, Map<K, V>, Map<K, V>> reduceMaps() {
    return new ReduceMapsCollector<K, V>();
  }

  private static class ReduceMapsCollector<K, V> implements Collector<Map<K, V>, Map<K, V>, Map<K, V>> {
    @Override public Supplier<Map<K, V>> supplier() {
      return () -> new HashMap<>();
    }

    @Override public BiConsumer<Map<K, V>, Map<K, V>> accumulator() {
      return (a, b) -> a.putAll(b);
    }

    @Override public BinaryOperator<Map<K, V>> combiner() {
      return (a, b) -> {
        final Supplier<Map<K, V>> newMap = supplier();
        newMap.get().putAll(a);
        newMap.get().putAll(b);
        return newMap.get();
      };
    }

    @Override public Function<Map<K, V>, Map<K, V>> finisher() {
      return a -> a;
    }

    @Override public Set<Characteristics> characteristics() {
      return Collections.EMPTY_SET;
    }
  }
}
