package sharetransport.infrastructure.collection;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CollectionsTest {

  @Test
  public void dotMatrixExpectSizeCorrect() {
    // GIVEN
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    // WHEN
    final ArrayList<Object> a = Lists.newArrayList(o1, o2, o3);
    final Map<Object, List<Object>> dotMatrix = Collections.dotJoin(a, a);

    // THEN
    assertThat(dotMatrix.size()).isEqualTo(3);
  }

  @Test
  public void dotJoinCorrect() {
    // GIVEN
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    // WHEN
    final ArrayList<Object> a = Lists.newArrayList(o1, o2, o3);
    final Map<Object, List<Object>> dotMatrix = Collections.dotJoin(a, a);

    // THEN
    assertThat(dotMatrix.get(o1)).containsExactlyInAnyOrder(o2, o3);
    assertThat(dotMatrix.get(o2)).containsExactlyInAnyOrder(o1, o3);
    assertThat(dotMatrix.get(o3)).containsExactlyInAnyOrder(o1, o2);
  }
}
