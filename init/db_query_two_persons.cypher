
// Find shortest path to all dests via all origins first
MATCH p = (start:Place)-[firstGuest:ROUTE]->(orig1:Place{type: 'orig'})-[:ROUTE]->(orig2:Place{type: 'orig'})-[:ROUTE*]->(dest1:Place{type: 'dest'})-[:ROUTE*]->(dest2:Place{type: 'dest'})
  WHERE start.type = 'current'
RETURN orig1.name, orig2.name, dest1.name, dest2.name, size(relationships(p)) as numberOfEdges,
       reduce(total = 0, rel IN relationships(p) | total + rel.weight) as sumOfWeigths
ORDER BY sumOfWeigths ASCENDING;
// LIMIT 1;

// find shortest path via one hop
MATCH (start:Place)-[via:ROUTE*2]->(dest:Place)
  WHERE start.name = 'a' AND dest.name = 'd'
RETURN start,
       [rel IN via | rel.weight] as weightList,
       reduce(total = 0, rel IN via | total + rel.weight) as sumOfWeigths,
       dest
ORDER BY sumOfWeigths ASCENDING;


//
MATCH (a:Place {name: "a"})-[r:ROUTE*]->(dest:Place {name: "d"})
RETURN a.name, collect(r.weight) as weightMap, dest.name;

// but reduced to one row only if collect is only item
MATCH (a:Place {name: "a"})-[r:ROUTE]->(b:Place)
RETURN b.name, collect(r.weight) as weightMap
