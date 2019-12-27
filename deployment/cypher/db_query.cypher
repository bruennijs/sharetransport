MATCH path= (p:Place)-[r:ROUTE*0..4]->(p2:Place)
RETURN p, r;

// Find shortest path
MATCH (start:Place)-[r:ROUTE*]->(dest:Place)
  WHERE start.name = 'a' AND dest.name = 'd'
RETURN start, dest, r, size(r) as numberOfEdges, reduce(total = 0, rel IN r | total + rel.weight) as sumOfWeigths
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
