package sharetransport.domain.transport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;

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
import org.neo4j.driver.v1.types.Node;
import org.neo4j.harness.junit.Neo4jRule;

import sharetransport.domain.routing.HopsInTourOrderUserFunction;
import sharetransport.infrastructure.persistence.neo4j.Neo4jDriverScope;
import sharetransport.testing.tools.Neo4jTestUnit;

@RunWith(JUnit4.class)
public class TripVerticesInOrderUserFunctionIT {

  // This rule starts a Neo4j instance
  @ClassRule
  public static Neo4jRule neo4j = new Neo4jRule()

      // This is the function we want to test
      .withFunction( HopsInTourOrderUserFunction.class );

  private static Neo4jDriverScope driverScope;

  private Session session;

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

  @Before
  public void before() {
    session = driverScope.getDriver().session();
  }

  @After
  public void tearDown() throws Exception {
    session.close();
  }

  @Test
  public void whenTwoPersonsExpect() throws IOException {
  }

  @Test
  public void testDriver() throws Throwable
  {
    try (Neo4jTestUnit dbUnit = Neo4jTestUnit.create(session,
        TripVerticesInOrderUserFunctionIT.class.getResourceAsStream("/cypher/two_persons.cypher"))) {

      // WHEN
      final Node hop = session.run("MATCH (h:Hop {name: 'o1'}) RETURN h as hop")
          .single()
          .get("hop")
          .asNode();

      // THEN
      assertThat(hop).isNotNull();
      assertThat(hop.get("origin").asBoolean()).isEqualTo(true);
    }
  }
}
