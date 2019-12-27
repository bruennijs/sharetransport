package sharetransport.domain.routing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Logging;
import org.neo4j.driver.v1.Session;
import org.neo4j.harness.junit.Neo4jRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bruenni.sharetransport.neo4j.routing.HopsInTourOrderUserFunction;
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
        RoutingIT.class.getResourceAsStream("/cypher/4_hops_in_series.cypher"))) {

      final Map<String, ImmutablePair<Hop, Hop>> passengerHops = session
          .run("MATCH (hopOn:Hop)<-[:HOPS_ON]-(p:Passenger)-[:HOPS_OFF]->(hopOff:Hop)"
              + " RETURN hopOff as off, hopOn as on, p as passenger")
          .stream()
          .collect(Collectors.toMap(record -> record.get("passenger").get("uid").asString(),
              val -> ImmutablePair.of(Hop.from(val.get("on").asNode()), Hop.from(val.get("off").asNode()))));

      final List<RouteSpecification> routeSpecifications = sut.findRoutesByHops(getNodes(passengerHops));

      // THEN
      assertThat(routeSpecifications.get(0).getHops())
          .usingElementComparator(Comparator.comparing(Hop::getId))
          .containsExactly(passengerHops.get("p1").getLeft(),
              passengerHops.get("p1").getRight(),
              passengerHops.get("p2").getLeft(),
              passengerHops.get("p2").getRight());
      assertThat(routeSpecifications.get(0).getWeight()).isEqualTo(30);
    }
  }

  @Test
  public void whenTwoOriginClusterAndOneDestinationExpectShortestPAthViaAllOrigins() throws Throwable
  {
    try (Neo4jTestUnit dbUnit = Neo4jTestUnit.create(session,
        RoutingIT.class.getResourceAsStream("/cypher/3_hops_by_two_origins_and_one_destination.cypher"))) {

      final Map<String, ImmutablePair<Hop, Hop>> passengerHops = session
          .run("MATCH (hopOn:Hop)<-[:HOPS_ON]-(p:Passenger)-[:HOPS_OFF]->(hopOff:Hop)"
              + " RETURN hopOff as off, hopOn as on, p as passenger")
          .stream()
          .collect(Collectors.toMap(record -> record.get("passenger").get("uid").asString(),
              val -> ImmutablePair.of(Hop.from(val.get("on").asNode()), Hop.from(val.get("off").asNode()))));

      final List<RouteSpecification> routeSpecifications = sut.findRoutesByHops(getNodes(passengerHops));

      // THEN
      assertThat(routeSpecifications.get(0).getHops())
          .usingElementComparator(Comparator.comparing(Hop::getId))
          .containsExactly(passengerHops.get("p2").getLeft(),
              passengerHops.get("p1").getLeft(),
              passengerHops.get("p1").getRight());
      assertThat(routeSpecifications.get(0).getWeight()).isEqualTo(20);
    }
  }

  private List<Hop> getNodes(Map<String, ImmutablePair<Hop, Hop>> passengerHops) {
    return passengerHops
        .values()
        .stream()
        .flatMap(p -> Lists.newArrayList(p.getLeft(), p.getRight()).stream())
        .collect(Collectors.toList());
  }
}
