// find shortest path via one hop
MATCH (start:Place {name:"a"})-[r:ROUTE]->(via:Place)-[r2:ROUTE]->(stop:Place)
RETURN start.name,r.weight,via.name,r2.weight,stop.name,(r.weight + r2.weight) as sumPlus, sum(collect(r.weight)) as sumFunc
ORDER BY sum
LIMIT 2;

// serach trasitive relation Route with cardinality
MATCH (start:Place {name:"a"})-[r:ROUTE*]->(stop:Place {name: "d"})
RETURN r, size(r) as numberOfEdges, reduce(total = 0, rel IN r | total + rel.weight) as sumOfWeigths
ORDER BY sumOfWeigths ASCENDING
LIMIT 1;

MATCH (start:Place {name:"a"})-[r:ROUTE*]->(p:Place)
RETURN start.name, collect(p.namerr    ), count(*);

MATCH (start:Place {name:"a"})-->(stop:Place {name: "d"})
RETURN count(*);

//
MATCH (a:Place {name: "a"})-[r:ROUTE]->(b:Place)
RETURN a.name, collect(r.weight) as weightMap, count(r) as relCount, b.name;

// but reduced to one row only if collect is only item
MATCH (a:Place {name: "a"})-[r:ROUTE]->(b:Place)
RETURN b.name, collect(r.weight) as weightMap
