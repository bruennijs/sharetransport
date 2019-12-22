package sharetransport.domain.transport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.net.URI;

import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import sharetransport.infrastructure.persistence.neo4j.Neo4jDriverScope;

/**
 * Test for business logic of finding the routes including one vehicle
 * and n trips (a trip has one origin and one destination for pone person)
 * and
 *
 * @author Oliver Brüntje
 */
public class TransportIT {
  @ClassRule
  public static GenericContainer neo4j = new GenericContainer("neo4j:3.5.14")
      .withEnv("NEO4J_AUTH", "neo4j/Password123")
      //.withFileSystemBind("target/neo4j-testcontainers-procedure.jar", "/plugins/neo4j-testcontainers-procedure.jar", BindMode.READ_ONLY)
      .waitingFor(Wait.forListeningPort())
      .withExposedPorts(7687);

  private static Neo4jDriverScope driverScope;

  @BeforeClass
  public static void beforeClass() {
    final Config config = Config.build().build();
    driverScope = new Neo4jDriverScope(URI.create(getContainerProtocolUri()), AuthTokens.basic("neo4j", "Password123"), config);
  }

  @AfterClass
  public static void afterClass() {
    driverScope.close();
  }

  @Test
  public void shouldAnswerWithOne() {
      try (Session session = driverScope.getDriver().session()) {
        StatementResult result = session.run("RETURN 42 AS value");
        long value = result.single().get("value").asLong();
        assertThat(value).isEqualTo(42L);
      }
  }

  @NotNull private static String getContainerProtocolUri() {
    return "bolt://" + neo4j.getContainerIpAddress() + ":" + neo4j.getMappedPort(7687);
  }
}
