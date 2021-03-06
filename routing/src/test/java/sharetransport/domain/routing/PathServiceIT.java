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
import org.neo4j.driver.v1.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import sharetransport.domain.routing.path.Path;
import sharetransport.domain.routing.path.PathService;
import sharetransport.infrastructure.domain.AbstractIdentifiable;
import sharetransport.infrastructure.domain.geo.Location;
import sharetransport.infrastructure.persistence.neo4j.Neo4jDriverScope;
import sharetransport.testing.tools.Neo4jTestUnit;

@RunWith(JUnit4.class)
public class PathServiceIT {

  private static final Integer EXPOSED_BOLT_PORT = 7687;

  private static Logger LOG = LoggerFactory.getLogger(PathServiceIT.class);

  @ClassRule
  public static GenericContainer neo4j = new GenericContainer("bruenni/sharetransport-neo4j-3.5:1.0")
      .withEnv("NEO4J_AUTH", "none")
      //.withFileSystemBind("target/neo4j-testcontainers-procedure.jar", "/plugins/neo4j-testcontainers-procedure.jar", BindMode.READ_ONLY)
      .waitingFor(Wait.forListeningPort())
      .withExposedPorts(EXPOSED_BOLT_PORT);

  private static Neo4jDriverScope driverScope;

  private static PathService sut;

  private Session session;

  @BeforeClass
  public static void beforeClass() {
    final Config config = Config.build()
        .withLogging(Logging.console(Level.FINE))
        //.withLogging(Logging.slf4j())
        .withoutEncryption()
        .toConfig();

    driverScope = new Neo4jDriverScope(getBoltUri(), config);
  }

  private static URI getBoltUri() {
    return URI.create(String.format("bolt://%s:%d", neo4j.getContainerIpAddress(), neo4j.getMappedPort(EXPOSED_BOLT_PORT)));
  }

  @AfterClass
  public static void afterClass() throws Exception {
    driverScope.close();
  }

  @Before
  public void before() {
    session = driverScope.getDriver().session();
    sut = new PathService(session);
  }

  @After
  public void tearDown() throws Exception {
    session.close();
  }

  @Test
  public void whenRouteAllHopsExpectShortestPathIsCorrect() throws Throwable
  {
    try (Neo4jTestUnit dbUnit = Neo4jTestUnit.create(session,
        PathServiceIT.class.getResourceAsStream("/cypher/routing/4_hops_in_series.cypher"))) {

      final Map<String, ImmutablePair<Hop, Hop>> passengerHops = session
          .run("MATCH (hopOn:Hop)<-[:HOPS_ON]-(p:Passenger)-[:HOPS_OFF]->(hopOff:Hop)"
              + " RETURN hopOff as off, hopOn as on, p as passenger")
          .stream()
          .collect(Collectors.toMap(record -> record.get("passenger").get("uid").asString(),
              val -> ImmutablePair.of(createHopFromNode(val.get("on").asNode()), createHopFromNode(val.get("off").asNode()))));

      final List<Path> routeSpecifications = sut.findPathByHops(getNodes(passengerHops));

      // THEN
      assertThat(routeSpecifications.get(0).getRoute())
          .usingElementComparator(Comparator.comparing(Hop::getId))
          .containsExactly(passengerHops.get("p1").getLeft(),
              passengerHops.get("p1").getRight(),
              passengerHops.get("p2").getLeft(),
              passengerHops.get("p2").getRight());
      assertThat(routeSpecifications.get(0).getDuration()).isEqualTo(30);
    }
  }

  @Test
  public void whenTwoOriginClusterAndOneDestinationExpectShortestPAthViaAllOrigins() throws Throwable
  {
    try (Neo4jTestUnit dbUnit = Neo4jTestUnit.create(session,
        PathServiceIT.class.getResourceAsStream("/cypher/routing/3_hops_by_two_origins_and_one_destination.cypher"))) {

      final Map<String, ImmutablePair<Hop, Hop>> passengerHops = session
          .run("MATCH (hopOn:Hop)<-[:HOPS_ON]-(p:Passenger)-[:HOPS_OFF]->(hopOff:Hop)"
              + " RETURN hopOff as off, hopOn as on, p as passenger")
          .stream()
          .collect(Collectors.toMap(record -> record.get("passenger").get("uid").asString(),
              val -> ImmutablePair.of(createHopFromNode(val.get("on").asNode()), createHopFromNode(val.get("off").asNode()))));

      final List<Path> routeSpecifications = sut.findPathByHops(getNodes(passengerHops));

      // THEN
      assertThat(routeSpecifications.get(0).getRoute())
          .usingElementComparator(Comparator.comparing(Hop::getId))
          .containsExactly(passengerHops.get("p2").getLeft(),
              passengerHops.get("p1").getLeft(),
              passengerHops.get("p1").getRight());
      assertThat(routeSpecifications.get(0).getDuration()).isEqualTo(20);
    }
  }

  @Test
  public void testWeightOfTrips() throws Throwable
  {
    try (Neo4jTestUnit dbUnit = Neo4jTestUnit.create(session,
        PathServiceIT.class.getResourceAsStream("/cypher/routing/4_hops_in_series.cypher"))) {

      // GIVEN
      final Map<String, Hop> hops = session
          .run("MATCH (h:Hop)"
              + " RETURN h as hop")
          .stream()
          .map(record -> createHopFromNode(record.get("hop").asNode()))
          .collect(Collectors.toMap(hop -> hop.getUid(), val -> val));

      // WHEN
      final List<Path> routeSpecifications = sut.findPathByHops(hops.values().stream().collect(Collectors.toList()));

      // THEN
      assertThat(routeSpecifications.get(0).getTripWeights().keySet())
          .containsExactly("o1", "o2");
      assertThat(routeSpecifications.get(0).getTripWeights().get("o1")).isEqualTo(5);
      assertThat(routeSpecifications.get(0).getTripWeights().get("o2")).isEqualTo(15);
    }
  }

  private List<Hop> getNodes(Map<String, ImmutablePair<Hop, Hop>> passengerHops) {
    return passengerHops
        .values()
        .stream()
        .flatMap(p -> Lists.newArrayList(p.getLeft(), p.getRight()).stream())
        .collect(Collectors.toList());
  }

  private static Hop createHopFromNode(Node node) {
    return new TestHop(node.id(),
        node.get(AbstractIdentifiable.PROPERTY_UID).asString(),
        node.get(Hop.PROPERTY_ORIGIN).asBoolean(false),
        node.get(Hop.PROPERTY_DESTINATION).asBoolean(false),
        new Location(8.1, 57.1));
  }

  private static class TestHop extends Hop {
    public TestHop(long id, String uid, boolean asBoolean, boolean asBoolean1, Location location) {
      super(id, uid, asBoolean, asBoolean1, location);
    }
  }
}
