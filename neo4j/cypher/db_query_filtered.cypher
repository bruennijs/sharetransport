MATCH p=(pl:Place), (o:Orig), (d:Dest)
RETURN p, pl, o, d, relationships(p);

MATCH p=(pl:Place)--(o:Orig)--(d:Dest)
RETURN p, pl, o, d, relationships(p);

// find shortest path via Thomas to both destinations
MATCH p=(start:Place)-[:ROUTE]->(o:Orig)-[:ROUTE*3]->(d)
  WHERE start.type = 'current'
AND sharetransport.domain.route.areLegsInOrder(p)
RETURN p,
       start.name,
       reduce(total = 0, rel IN relationships(p) | total + rel.weight) as sumOfWeigths,
       [rel IN relationships(p) | rel.weight] as weightList,
       o.name,
        d.name
ORDER BY sumOfWeigths ASCENDING;

