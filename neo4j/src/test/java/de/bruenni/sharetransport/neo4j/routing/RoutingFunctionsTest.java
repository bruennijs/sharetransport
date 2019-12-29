package de.bruenni.sharetransport.neo4j.routing;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.neo4j.harness.junit.Neo4jRule;

@Ignore
@RunWith(JUnit4.class)
public class RoutingFunctionsTest {
  // This rule starts a Neo4j instance
  @ClassRule
  public static Neo4jRule neo4j = new Neo4jRule()

      // This is the function we want to test
      .withFunction( RoutingFunctions.class );

  @Test
  public void test() {
  }
}
