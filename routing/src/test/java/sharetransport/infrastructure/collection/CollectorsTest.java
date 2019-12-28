package sharetransport.infrastructure.collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CollectorsTest {

  @Test
  public void reduceMaps() {
    // GIVEN
    final Map map1 = new HashMap();
    map1.put("k1", "v1");
    final Map map2 = new HashMap();
    map1.put("k2", "v2");

    final List<Map<String, String>> listOfMaps = Lists.newArrayList(map1, map2);

    // WHEN
    final Map<String, String> reduced = listOfMaps.stream().collect(Collectors.reduceMaps());

    // THEN
    assertThat(reduced.size()).isEqualTo(2);
    assertThat(reduced.get("k1")).isEqualTo("v1");
    assertThat(reduced.get("k2")).isEqualTo("v2");
  }
}
