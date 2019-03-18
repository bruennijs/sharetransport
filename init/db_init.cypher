:begin
MERGE (a:Place {name:'a'})
MERGE (b:Place {name:'b'})
MERGE (c:Place {name:'c'})
MERGE (d:Place {name:'d'})
MERGE (e:Place {name:'e'})

MERGE (a)-[:ROUTE {weight:5}]->(b)
MERGE (b)-[:ROUTE {weight:20}]->(d)


MERGE (a)-[:ROUTE {weight:10}]->(c)
MERGE (c)-[:ROUTE {weight:5}]->(d);
:commit