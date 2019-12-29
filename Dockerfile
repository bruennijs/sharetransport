FROM neo4j:3.5.13
COPY ./deployment/cypher /opt/init
# ADD --chown neo4j:neo4j ./cypher /var/lib/neo4j/
# RUN neo4j-admin set-initial-password admin

# copy plugins to /plugins
# ADD --chown=neo4j:neo4j https://github.com/neo4j-contrib/neo4j-apoc-procedures/releases/download/3.5.0.6/apoc-3.5.0.6-all.jar /var/lib/neo4j/plugins/
ADD --chown=neo4j:neo4j ./deployment/lib/apoc-3.5.0.6-all.jar /var/lib/neo4j/plugins/

# Graph algorithms: See https://neo4j.com/docs/graph-algorithms/current/introduction/#_installation
ADD --chown=neo4j:neo4j ./deployment/lib/neo4j-graph-algorithms-3.5.13.0-standalone.jar /var/lib/neo4j/plugins/
ADD --chown=neo4j:neo4j ./neo4j/target/original-neo4j-1.0-SNAPSHOT.jar /var/lib/neo4j/plugins/
