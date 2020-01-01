package sharetransport.domain.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

import org.assertj.core.api.AssertionsForClassTypes;
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

  private final Location defaultLocation = mock(Location.class);

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

  @Test
  public void whenHopWithLocationExpectPersistedCorrectly() {
    final Location expectedLocation = new Location(8.12, 57.23);
    final Hop origin = new Hop(UUID.randomUUID().toString(), true, false, expectedLocation);
    final Community community = new Community("cuid");
    community.contains(origin);

    // WHEN
    final Community loadedCommunity = sut.createOrUpdate(community);

    // THEN
    AssertionsForClassTypes.assertThat(loadedCommunity.getHopByUid(origin.getUid()).get().getLocation()).isEqualTo(expectedLocation);
  }

  @Test
  public void whenPersistCommunityExpectIdGenerated() {
    // GIVEN
    final Hop hop = new Hop("uid", true, false, defaultLocation);
    final Community community = new Community("cuid");
    community.contains(hop);

    // WHEN
    final Community createdCommunity = sut.createOrUpdate(community);

    // THEN
    AssertionsForClassTypes.assertThat(createdCommunity.getId()).isGreaterThanOrEqualTo(0l);
    AssertionsForClassTypes.assertThat(createdCommunity.getHopByUid(hop.getUid()).get().getId()).isGreaterThanOrEqualTo(0l);
  }

  @Test
  public void whenPersistExpectPropertiesSet() {
    // GIVEN
    final Hop hop = new Hop("someuid", true, false, defaultLocation);
    final Community community = new Community("cuid");
    community.contains(hop);

    // WHEN
    final Community createdCommunity = sut.createOrUpdate(community);

    // THEN
    final Hop createdHop = createdCommunity.getHopByUid(hop.getUid()).get();
    AssertionsForClassTypes.assertThat(createdHop.getUid()).isEqualTo("someuid");
    AssertionsForClassTypes.assertThat(createdHop.isOrigin()).isEqualTo(true);
    AssertionsForClassTypes.assertThat(createdHop.isDestination()).isEqualTo(false);
  }

  @Test
  public void whenBookedToRelationExpect() {
    final Hop origin = new Hop(UUID.randomUUID().toString(), false, false, defaultLocation);
    final Hop destination = new Hop(UUID.randomUUID().toString(), false, false, defaultLocation);

    origin.bookedTo(destination);

    final Community community = new Community("cuid");
    community.contains(origin);
    community.contains(destination);

    // WHEN
    final Community createdCommunity = sut.createOrUpdate(community);

    // THEN
    final Hop createdOrigin = createdCommunity.getHopByUid(origin.getUid()).get();
    final Hop createdDestination = createdCommunity.getHopByUid(destination.getUid()).get();
    AssertionsForClassTypes.assertThat(createdOrigin.getBookedTo().isPresent()).isTrue();
    AssertionsForClassTypes.assertThat(createdOrigin.getBookedTo().get()).isEqualTo(destination);
    AssertionsForClassTypes.assertThat(createdOrigin.isOrigin()).isTrue();

    //assertThat(loadedDestination.getBookedFrom()).isEqualTo(origin);  not yet needed in logic
    AssertionsForClassTypes.assertThat(createdDestination.isDestination()).isTrue();
  }

  @Test
  public void whenTransactionExpectOK() {
    final String uid = "someuid";
    final Community community = new Community(uid);
    try (Transaction tx = session.beginTransaction()) {
      // GIVEN

      // WHEN
      final Community createdCommunity = sut.createOrUpdate(community);

      // THEN
      AssertionsForClassTypes.assertThat(createdCommunity.getUid()).isEqualTo(uid);

      final Optional<Community> hopByUid = sut.findByUid(uid);

      AssertionsForClassTypes.assertThat(hopByUid.isPresent()).isTrue();
      AssertionsForClassTypes.assertThat(hopByUid.get()).isEqualTo(createdCommunity);
      AssertionsForClassTypes.assertThat(hopByUid.get()).isEqualTo(community);

      tx.commit();
    }

    // create new session and load committed items
    final Session session = ogmProvider.createSession();
    final CommunityRepository repository = new CommunityRepository(session);
    final Optional<Community> hopByUid = repository.findByUid(uid);

    AssertionsForClassTypes.assertThat(hopByUid.isPresent()).isTrue();
    AssertionsForClassTypes.assertThat(hopByUid.get()).isEqualTo(community);
  }

  @Test
  public void whenNewSessionExpectHopLoadByIdSucceeds() {
    final CommunityRepository repository = new CommunityRepository(session);

    Function<CommunityRepository, Community> persistHop = (repo) -> {
      Session session = ogmProvider.createSession();
      try {
        final Community community = new Community(UUID.randomUUID().toString());

        return repo.createOrUpdate(community);
      } finally {
        session.clear();
      }
    };

    final Community hop = persistHop.apply(repository);
    final Community hopFound = repository.find(hop.getId());

    // THEN
    AssertionsForClassTypes.assertThat(hop).isEqualTo(hopFound);
  }

  @NotNull private Hop createDefaultHop() {
    return new Hop(UUID.randomUUID().toString(), false, false, new Location(8.1, 56.3));
  }
}
