package sharetransport.domain.routing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

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
import org.neo4j.ogm.transaction.Transaction;
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
public class HopRepositoryIT {
  private static final Integer EXPOSED_BOLT_PORT = 7687;

  private static Logger LOG = LoggerFactory.getLogger(HopRepositoryIT.class);

  @ClassRule
  public static GenericContainer neo4j = new GenericContainer("bruenni/sharetransport-neo4j-3.5:1.0")
      .withEnv("NEO4J_AUTH", "none")
      //.withFileSystemBind("target/neo4j-testcontainers-procedure.jar", "/plugins/neo4j-testcontainers-procedure.jar", BindMode.READ_ONLY)
      .waitingFor(Wait.forListeningPort())
      .withExposedPorts(EXPOSED_BOLT_PORT);

  public static Neo4jOgmProvider ogmProvider;

  private static HopRepository sut;

  private static Neo4jDriverScope driverV1;

  private Session session;

  private final Location defaultLocation = new Location(8.1, 55.22);

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
    sut = new HopRepository(session);
  }

  @After
  public void tearDown() throws Exception {
    ogmProvider.onSessionScopeDispose(this.session);
  }

  @Test
  public void whenPersistHopExpectIdGenerated() {
    // GIVEN
    final Hop hop = new Hop("uid", true, false, defaultLocation);

    // WHEN
    final Hop createdHop = sut.createOrUpdate(hop);
    final Hop createdHop2 = sut.createOrUpdate(hop);

    // THEN
    assertThat(createdHop.getId()).isGreaterThanOrEqualTo(0l);
    assertThat(createdHop2.getId()).isGreaterThan(0l);
  }

  @Test
  public void whenPersistExpectPropertiesSet() {
    // GIVEN
    final Hop hop = new Hop("someuid", true, false, defaultLocation);

    // WHEN
    final Hop createdHop = sut.createOrUpdate(hop);

    // THEN
    assertThat(createdHop.getUid()).isEqualTo("someuid");
    assertThat(createdHop.isOrigin()).isEqualTo(true);
    assertThat(createdHop.isDestination()).isEqualTo(false);
  }

  @Test
  public void whenBookedToRelationExpect() {
    final Hop origin = new Hop(UUID.randomUUID().toString(), false, false, defaultLocation);
    final Hop destination = new Hop(UUID.randomUUID().toString(), false, false, defaultLocation);

    origin.bookedTo(destination);

    sut.createOrUpdate(origin);

    // THEN
    final Hop loadedDestination = sut.find(destination.getId());
    assertThat(origin.getBookedTo()).isEqualTo(destination);
    assertThat(origin.isOrigin()).isTrue();

    //assertThat(loadedDestination.getBookedFrom()).isEqualTo(origin);  not yet needed in logic
    assertThat(loadedDestination.isDestination()).isTrue();
  }

  @Test
  public void whenPersistTwoHopsWithOneDistanceRelationExpectOutgoingAndIncomingRelationOnEachHop() {
    final Hop origin = new Hop(UUID.randomUUID().toString(), true, false, defaultLocation);
    final Hop destination = new Hop(UUID.randomUUID().toString(), false, true, defaultLocation);
    final Duration duration = Duration.ofSeconds(20);
    origin.addDistanceTo(destination, duration);

    // WHEN
    sut.createOrUpdate(origin);
    //sut.createOrUpdate(destination);  // depth os 1 so related destination is persisted with origin
    final Hop hopDestinationFound = sut.find(destination.getId());

    // THEN
    assertThat(hopDestinationFound.getDistancesIncoming().size()).isEqualTo(1);
    assertThat(hopDestinationFound.getDistancesIncoming().stream().findFirst().get().getDuration().getSeconds()).isEqualTo(20);
  }

  @Test
  public void whenHopWithLocationExpectPersistedCorrectly() {
    final Location expectedLocation = new Location(8.12, 57.23);
    final Hop origin = new Hop(UUID.randomUUID().toString(), true, false, expectedLocation);

    // WHEN
    final Hop created = sut.createOrUpdate(origin);

    // THEN
    final Hop loaded = sut.find(created.getId());

    assertThat(loaded.getLocation()).isEqualTo(expectedLocation);
  }

  @Test
  public void whenTransactionExpectOK() {
    final String uid = "someuid";
    final Hop hop = new Hop(uid, true, false, defaultLocation);
    try (Transaction tx = session.beginTransaction()) {
      // GIVEN

      // WHEN
      final Hop createdHop = sut.createOrUpdate(hop);

      // THEN
      assertThat(createdHop.getUid()).isEqualTo(uid);
      assertThat(createdHop.isOrigin()).isEqualTo(true);
      assertThat(createdHop.isDestination()).isEqualTo(false);

      final Optional<Hop> hopByUid = sut.findByUid(uid);

      assertThat(hopByUid.isPresent()).isTrue();
      assertThat(hopByUid.get()).isEqualTo(createdHop);
      assertThat(hopByUid.get()).isEqualTo(hop);

      tx.commit();
    }

    // create new session and load committed items
    final Session session = ogmProvider.createSession();
    final HopRepository repository = new HopRepository(session);
    final Optional<Hop> hopByUid = repository.findByUid(uid);

    assertThat(hopByUid.isPresent()).isTrue();
    assertThat(hopByUid.get()).isEqualTo(hop);
  }

  @Test
  public void whenNewSessionExpectHopLoadByIdSucceeds() {
    final HopRepository repository = new HopRepository(session);

    Function<HopRepository, Hop> persistHop = (repo) -> {
      Session session = ogmProvider.createSession();
      try {
        final Hop hop = new Hop(UUID.randomUUID().toString(), true, true, defaultLocation);

        return repo.createOrUpdate(hop);
      } finally {
        session.clear();
      }
    };

    final Hop hop = persistHop.apply(repository);
    final Hop hopFound = repository.find(hop.getId());

    // THEN
    assertThat(hop).isEqualTo(hopFound);
  }
}
