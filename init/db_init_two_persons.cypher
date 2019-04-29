MERGE (cur:Place {name:'Current location', type: 'current'})

MERGE (a:Place {name:'Olli', type: 'orig'})
MERGE (b:Place {name:'Olli\'s Ziel', type: 'dest'})
MERGE (c:Place {name:'Thomas', type: 'orig'})
MERGE (d:Place {name:'Thomas\' Ziel', type: 'dest'})

MERGE (cur)-[:ROUTE {weight: 10}]->(a)
MERGE (cur)-[:ROUTE {weight: 15}]->(c)

MERGE (a)-[:ROUTE {weight:5}]->(b)
MERGE (c)-[:ROUTE {weight:15}]->(d)

MERGE (a)-[:ROUTE {weight:20}]->(c)
MERGE (c)-[:ROUTE {weight:15}]->(a)

MERGE (a)-[:ROUTE {weight:5}]->(d)
MERGE (d)-[:ROUTE {weight:10}]->(a)
MERGE (c)-[:ROUTE {weight:5}]->(b)
MERGE (b)-[:ROUTE {weight:10}]->(c)

MERGE (d)-[:ROUTE {weight:10}]->(b)
MERGE (b)-[:ROUTE {weight:20}]->(d);


//
