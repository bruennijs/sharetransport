MERGE (cur:Place {name:'Current location', type: 'current'})

MERGE (a:Orig {name:'Olli', tripId: 1})
MERGE (b:Dest {name:'Olli Ziel', tripId: 1})
MERGE (c:Orig {name:'Thomas', tripId: 2})
MERGE (d:Dest {name:'Thomas Ziel', tripId: 2})

MERGE (cur)-[:ROUTE {weight: 10}]->(a)
MERGE (cur)-[:ROUTE {weight: 15}]->(c)

MERGE (a)-[:ROUTE {weight:5, type: 'direct'}]->(b)
MERGE (c)-[:ROUTE {weight:15, type: 'direct'}]->(d)

MERGE (a)-[:ROUTE {weight:20}]->(c)
MERGE (c)-[:ROUTE {weight:15}]->(a)

MERGE (a)-[:ROUTE {weight:5}]->(d)
MERGE (d)-[:ROUTE {weight:10}]->(a)
MERGE (c)-[:ROUTE {weight:5}]->(b)
MERGE (b)-[:ROUTE {weight:10}]->(c)

MERGE (d)-[:ROUTE {weight:10}]->(b)
MERGE (b)-[:ROUTE {weight:20}]->(d);


//
