package sharetransport.domain.routing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.UUID;
import java.util.logging.Level;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Logging;
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import sharetransport.infrastructure.domain.geo.Location;
import sharetransport.infrastructure.persistence.neo4j.Neo4jDriverScope;
import sharetransport.infrastructure.persistence.neo4j.Neo4jOgmProvider;

/**
 * TEst for Hop repository against neo4j database.
 *
 * @author Oliver Brüntje
 */
public class CommunityRepositoryIT {
  private static final Integer EXPOSED_BOLT_PORT = 7687;

  private static Logger LOG = LoggerFactory.getLogger(CommunityRepositoryIT.class);

  @ClassRule
  public static GenericContainer neo4j = new GenericContainer("bruenni/sharetransport-neo4j-3.5:1.0")
      .withEnv("NEO4J_AUTH", "none")
      //.withFileSystemBind("target/neo4j-testcontainers-procedure.jar", "/plugins/neo4j-testcontainers-procedure.jar", BindMode.READ_ONLY)
      .waitingFor(Wait.forListeningPort())
      .withExposedPorts(EXPOSED_BOLT_PORT);

  public static Neo4jOgmProvider ogmProvider;

  private static CommunityRepository sut;

  private static Neo4jDriverScope driverV1;

  private Session session;

  @BeforeClass
  public static void beforeClass() {
    final Config config = Config.build()
        .withLogging(Logging.console(Level.FINE))
        //.withLogging(Logging.slf4j())
        .withoutEncryption()
        .toConfig();

    driverV1 = new Neo4jDriverScope(URI.create(getBoltUri()), config);
    final BoltDriver boltDriver = new BoltDriver(driverV1.getDriver());
    ogmProvider = new Neo4jOgmProvider(boltDriver);
  }

  private static String getBoltUri() {
    return String.format("bolt://%s:%d", neo4j.getContainerIpAddress(), neo4j.getMappedPort(EXPOSED_BOLT_PORT));
  }

  @AfterClass
  public static void afterClass() throws Exception {
    ogmProvider.close();
    driverV1.close();
  }

  @Before
  public void before() {
    session = ogmProvider.createSession();
    this.session.purgeDatabase();
    sut = new CommunityRepository(session);
  }

  @After
  public void tearDown() throws Exception {
    ogmProvider.onSessionScopeDispose(this.session);
  }

  @Test
  public void whenPersistCommunityWithHopsExpectPersistedToDb() {
    final Hop origin1 = createDefaultHop();
    final Hop destination1 = createDefaultHop();

    final Hop origin2 = createDefaultHop();
    final Hop destination2 = createDefaultHop();

    final Community community = new Community("cuid");
    community.contains(origin1);
    community.contains(origin2);
    community.contains(destination1);
    community.contains(destination2);

    // WHEN
    Community loadedCommunity = sut.createOrUpdate(community);

    // THEN
    assertThat(loadedCommunity.getHops()).containsExactlyInAnyOrder(origin1, origin2, destination1, destination2);
  }

  @NotNull private Hop createDefaultHop() {
    return new Hop(UUID.randomUUID().toString(), false, false, new Location(8.1, 56.3));
  }
}
