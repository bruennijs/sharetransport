// CREATE (p1:Place {name: 'Current location', type: 'current'})
CREATE (t1:Tour {stared: false})

CREATE (v1:Vehicle {name:'vehicle 1'})

CREATE (p1:Passenger {name:'Olli', uid: 'p1'})
CREATE (o1:Hop {origin: true, uid:'o1'})
CREATE (d1:Hop {destination: true, uid:'d1'})
CREATE (o1)-[:DISTANCE {weight: 12}]->(d1)
CREATE (o1)-[:BOOKED_TO]->(d1)

CREATE (p2:Passenger {name:'Tom', uid: 'p2'})
CREATE (o2:Hop {origin: true, uid:'o2'})
CREATE (d2:Hop {destination: true, uid:'d2'})
CREATE (o2)-[:DISTANCE {weight: 17}]->(d2)
CREATE (o2)-[:BOOKED_TO]->(d2)

// same origin cluster, different destination cluster
CREATE (o3:Hop {origin: true, uid:'o3'})
CREATE (d3:Hop {destination: true, uid:'d3'})
CREATE (o3)-[:DISTANCE {weight: 17}]->(d3)
CREATE (o3)-[:BOOKED_TO]->(d3)

CREATE (v1)-[:DISTANCE {weight: 10}]->(o1)
CREATE (v1)-[:DISTANCE {weight: 15}]->(o2)

CREATE (o1)-[:DISTANCE {weight: 3}]->(o2)
CREATE (o2)-[:DISTANCE {weight: 4}]->(o1)

CREATE (o1)-[:DISTANCE {weight: 15}]->(d2)
CREATE (d2)-[:DISTANCE {weight: 15}]->(o1)
CREATE (o2)-[:DISTANCE {weight: 19}]->(d1)
CREATE (d1)-[:DISTANCE {weight: 19}]->(o2)

CREATE (d2)-[:DISTANCE {weight: 4}]->(d1)
CREATE (d1)-[:DISTANCE {weight: 5}]->(d2)

// d3
MERGE (o3)-[:DISTANCE {weight: 3}]-(o1)
MERGE (o3)-[:DISTANCE {weight: 2}]-(o2)
MERGE (d3)-[:DISTANCE {weight: 20}]-(d1)
MERGE (d3)-[:DISTANCE {weight: 24}]-(d2);
