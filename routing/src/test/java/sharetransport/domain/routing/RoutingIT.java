package sharetransport.domain.routing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.neo4j.driver.internal.logging.ConsoleLogging;
import org.neo4j.driver.internal.logging.Slf4jLogging;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Logging;
import org.neo4j.driver.v1.Session;
import org.neo4j.harness.junit.Neo4jRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sharetransport.infrastructure.persistence.neo4j.Neo4jDriverScope;
import sharetransport.testing.tools.Neo4jTestUnit;

@RunWith(JUnit4.class)
public class RoutingIT {

  private static Logger LOG = LoggerFactory.getLogger(RoutingIT.class);

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
        .withLogging(Logging.console(Level.FINE))
        //.withLogging(Logging.slf4j())
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

      final Map<Long, Hop> allHops = session.run("MATCH (h:Hop) RETURN h as hop")
          .stream()
          .map(r -> Hop.from(r.get("hop").asNode()))
          .collect(Collectors.toMap(hop -> hop.getId(), val -> val));

      final List<RouteSpecification> routeSpecifications = sut.findRoutesByHops(allHops.values().stream().collect(Collectors.toList()));

      // THEN
      assertThat(routeSpecifications.get(0).getHops())
          .usingElementComparator(Comparator.comparing(Hop::getId))
          .containsExactly(allHops.get(3), allHops.get(4),  allHops.get(6),  allHops.get(7));
      assertThat(routeSpecifications.get(0).getWeight()).isEqualTo(30);
    }
  }
}
