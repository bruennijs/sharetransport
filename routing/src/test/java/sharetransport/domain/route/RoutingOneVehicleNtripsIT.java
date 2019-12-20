package sharetransport.domain.route;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.ogm.cypher.compiler.CypherStatementBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Test for business logic of finding the routes including one vehicle
 * and n trips (a trip has one origin and one destination for pone person)
 * and
 *
 * @author Oliver Brüntje
 */
public class RoutingOneVehicleNtripsIT {
  @ClassRule
  public static GenericContainer neo4j = new GenericContainer("neo4j:3.5.14")
      .withEnv("NEO4J_AUTH", "neo4j/Password123")
      //.withFileSystemBind("target/neo4j-testcontainers-procedure.jar", "/plugins/neo4j-testcontainers-procedure.jar", BindMode.READ_ONLY)
      .waitingFor(Wait.forListeningPort())
      .withExposedPorts(7687);

  @Test
  public void shouldAnswerWithOne() {
    String uri = getContainerProtocolUri();

    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "Password123"))) {

      try (Session session = driver.session()) {
        StatementResult result = session.run("RETURN 42 AS value");
        long value = result.single().get("value").asLong();
        assertThat(value).isEqualTo(42L);
      }
    }
  }

  @NotNull private String getContainerProtocolUri() {
    return "bolt://" + neo4j.getContainerIpAddress() + ":" + neo4j.getMappedPort(7687);
  }
}
