# Init neo4j database

docker exec  -it sharetransport-db sh -c 'cat /init/db_init.cypher | cypher-shell -u neo4j  -p admin --format verbose --address localhost'

