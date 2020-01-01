package sharetransport.domain.routing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import sharetransport.infrastructure.domain.geo.DistanceMetric;
import sharetransport.infrastructure.domain.geo.Location;

@RunWith(MockitoJUnitRunner.class)
public class CommunityTest {

  @Mock
  private DistanceMetric distanceMetric;

  @Test
  public void calculateDistances() {

    // GIVEN
    Duration hop1Tohop2 = Duration.ofSeconds(60);
    Duration hop2Tohop1 = Duration.ofSeconds(99);
    final Location locatipnHop1 = mock(Location.class);
    final Location locatipnHop2 = mock(Location.class);

    final Hop hop1 = createHopMock(locatipnHop1);
    final Hop hop2 = createHopMock(locatipnHop2);

    when(distanceMetric.apply(Matchers.eq(locatipnHop1), Matchers.eq(locatipnHop2))).thenReturn(ImmutablePair.of(hop1Tohop2, 0.0));
    when(distanceMetric.apply(Matchers.eq(locatipnHop2), Matchers.eq(locatipnHop1))).thenReturn(ImmutablePair.of(hop2Tohop1, 0.0));
    final Community community = new Community(UUID.randomUUID().toString());
    community.contains(hop1);
    community.contains(hop2);

    // WHEN
    community.calculateDistances(distanceMetric);

    // THEN
    verify(hop1, times(1)).durationTo(hop2, hop1Tohop2);
    verify(hop2, times(1)).durationTo(hop1, hop2Tohop1);
  }

  private Hop createHopMock(Location location) {
    final Hop hop = mock(Hop.class);
    when(hop.getLocation()).thenReturn(location);
    return hop;
  }
}
