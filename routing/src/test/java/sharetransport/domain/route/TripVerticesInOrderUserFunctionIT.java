package sharetransport.domain.route;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.neo4j.driver.internal.logging.Slf4jLogging;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.harness.junit.Neo4jRule;

import sharetransport.infrastructure.persistence.neo4j.Neo4jDriverScope;
import sharetransport.testing.tools.Neo4jTestUnit;

@RunWith(JUnit4.class)
public class TripVerticesInOrderUserFunctionIT {

  // This rule starts a Neo4j instance
  @ClassRule
  public static Neo4jRule neo4j = new Neo4jRule()

      // This is the function we want to test
      .withFunction( TripVerticesInOrderUserFunction.class );

  private static Neo4jDriverScope driverScope;

  @BeforeClass
  public static void beforeClass() {
    final Config config = Config.build()
        .withLogging(new Slf4jLogging())
        .withoutEncryption()
        .toConfig();

    driverScope = new Neo4jDriverScope(neo4j.boltURI(), config);
  }

  @AfterClass
  public static void afterClass() throws Exception {
    driverScope.close();
  }

  @Test
  public void whenDestBeforeOrigOfSameTripIdExpectFalse() throws Throwable
  {
    try (Neo4jTestUnit dbUnit = new Neo4jTestUnit(driverScope.getDriver(),
        TripVerticesInOrderUserFunctionIT.class.getResourceAsStream("/cypher/two_persons.cypher"))) {
      // This is in a try-block, to make sure we close the driver after the test
      // Given

      try (Session session = driverScope.getDriver().session()) {

        // When
        final Node origin = session.run("MATCH (o:Orig {tripId: 1})"
            + "RETURN o as origin").single().get("origin").asNode();

        assertThat(origin).isNotNull();
        assertThat(origin.get("name").asString()).isEqualTo("Olli");
      }
    }
  }
}
