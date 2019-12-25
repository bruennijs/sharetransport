FROM neo4j:3.5.14
COPY cypher /opt/init
# ADD --chown neo4j:neo4j ./cypher /var/lib/neo4j/
# RUN neo4j-admin set-initial-password admin

# copy plugins to /plugins
# RUN wget --show-progress -o /var/lib/neo4j/plugins/apoc-all.jar https://github.com/neo4j-contrib/neo4j-apoc-procedures/releases/download/3.5.0.6/apoc-3.5.0.6-all.jar
# ADD --chown=neo4j:neo4j https://github.com/neo4j-contrib/neo4j-apoc-procedures/releases/download/3.5.0.6/apoc-3.5.0.6-all.jar /var/lib/neo4j/plugins/
ADD --chown=neo4j:neo4j ./lib/apoc-3.5.0.6-all.jar /var/lib/neo4j/plugins/
# RUN chmod oug+x /var/lib/neo4j/plugins/apoc-all.jar
