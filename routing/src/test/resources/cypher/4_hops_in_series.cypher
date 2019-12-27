// CREATE (p1:Place {name: 'Current location', type: 'current'})
CREATE (t1:Tour {stared: false})

CREATE (v1:Vehicle {name:'vehicle 1'})

CREATE (p1:Passenger {name:'Olli', uid: 'p1'})
CREATE (o1:Hop {origin: true, uid:'o1'})
CREATE (d1:Hop {destination: true, uid:'d1'})
CREATE (p1)-[:HOPS_ON]->(o1)
CREATE (p1)-[:HOPS_OFF]->(d1)
CREATE (o1)-[:DISTANCE {weight: 5}]->(d1)
CREATE (o1)-[:BOOKED_TO]->(d1)

CREATE (p2:Passenger {name:'Tom', uid: 'p2'})
CREATE (o2:Hop {origin: true, uid:'o2'})
CREATE (d2:Hop {destination: true, uid:'d2'})
CREATE (p2)-[:HOPS_ON]->(o2)
CREATE (p2)-[:HOPS_OFF]->(d2)
CREATE (o2)-[:DISTANCE {weight: 15}]->(d2)
CREATE (o2)-[:BOOKED_TO]->(d2)

CREATE (v1)-[:DISTANCE {weight: 10}]->(o1)
CREATE (v1)-[:DISTANCE {weight: 15}]->(o2)

CREATE (o1)-[:DISTANCE {weight: 20}]->(o2)
CREATE (o2)-[:DISTANCE {weight: 15}]->(o1)

CREATE (o1)-[:DISTANCE {weight: 5}]->(d2)
CREATE (d2)-[:DISTANCE {weight: 10}]->(o1)
CREATE (o2)-[:DISTANCE {weight: 5}]->(d1)
CREATE (d1)-[:DISTANCE {weight: 10}]->(o2)

CREATE (d2)-[:DISTANCE {weight: 10}]->(d1)
CREATE (d1)-[:DISTANCE {weight: 20}]->(d2);
