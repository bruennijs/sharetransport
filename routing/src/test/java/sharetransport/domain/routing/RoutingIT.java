package sharetransport.domain.routing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Equator;
import org.apache.commons.collections4.ListUtils;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.neo4j.driver.internal.logging.Slf4jLogging;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Session;
import org.neo4j.harness.junit.Neo4jRule;

import sharetransport.domain.hop.Hop;
import sharetransport.infrastructure.persistence.neo4j.Neo4jDriverScope;
import sharetransport.testing.IdentifiableEquator;
import sharetransport.testing.tools.Neo4jTestUnit;

@RunWith(JUnit4.class)
public class RoutingIT {

  // This rule starts a Neo4j instance
  @ClassRule
  public static Neo4jRule neo4j = new Neo4jRule()

      // This is the function we want to test
      .withFunction( HopsInTourOrderUserFunction.class );

  private static Neo4jDriverScope driverScope;

  private static RoutingService sut;

  private Session session;

  @BeforeClass
  public static void beforeClass() {
    final Config config = Config.build()
        .withLogging(new Slf4jLogging())
        .withoutEncryption()
        .toConfig();

    driverScope = new Neo4jDriverScope(neo4j.boltURI(), config);

/*    Driver ogmDriver = new BoltDriver(driverScope.getDriver());
    new SessionFactory(ogmDriver, ...);*/

  }

  @AfterClass
  public static void afterClass() throws Exception {
    driverScope.close();
  }

  @Before
  public void before() {
    session = driverScope.getDriver().session();
    sut = new RoutingService(session);
  }

  @After
  public void tearDown() throws Exception {
    session.close();
  }

  @Test
  public void whenRouteAllHopsExpectShortestPathIsCorrect() throws Throwable
  {
    try (Neo4jTestUnit dbUnit = Neo4jTestUnit.create(session,
        RoutingIT.class.getResourceAsStream("/cypher/two_persons.cypher"))) {

      final List<Hop> allHops = session.run("MATCH (h:Hop) RETURN h as hop")
          .stream()
          .map(r -> Hop.from(r.get("hop").asNode()))
          .collect(Collectors.toList());

      final List<RouteSpecification> routeSpecifications = sut.findRoutesByHops(allHops);

      // THEN
      assertThat(routeSpecifications.get(0).getHops())
          .usingElementComparator(Comparator.comparing(Hop::getId))
          .containsExactly(allHops.get(0), allHops.get(1));
      assertThat(routeSpecifications.get(0).getWeight()).isEqualTo(45);
    }
  }
}
