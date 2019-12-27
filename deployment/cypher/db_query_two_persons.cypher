

// find shortest path via Thomas to both destinations
MATCH p=(start:Place)-[:ROUTE]->(p1:Place)-[:ROUTE]->(p2:Place)-[:ROUTE*]->(d1:Place)-[:ROUTE*]->(d2:Place)
  WHERE start.type = 'current'
    AND ALL (person IN [p1, p2] WHERE person.name IN ['Olli', 'Thomas'])
    AND ALL (dest IN [d1, d2] WHERE dest.name IN ['OlliZiel', 'ThomasZiel'])
RETURN p,
       start.name,
       reduce(total = 0, rel IN relationships(p) | total + rel.weight) as sumOfWeigths,
       [rel IN relationships(p) | rel.weight] as weightList,
       d1.name,
        d2.name
ORDER BY sumOfWeigths ASCENDING;

// find paths where personX is predessor of destX for all persons
MATCH p=(start:Place{type: 'current'})-[:ROUTE]->(p1:Orig)-[:ROUTE]->(p2:Orig)-[:ROUTE]->(d1:Dest)-[:ROUTE]->(d2:Dest)
  WHERE ALL (person IN [p1, p2] WHERE person.name IN ['Olli', 'Thomas'])
  AND ALL (dest IN [d1, d2] WHERE dest.name IN ['OlliZiel', 'ThomasZiel'])
RETURN p,
       start.name,
       reduce(total = 0, rel IN relationships(p) | total + rel.weight) as sumOfWeigths,
       [rel IN relationships(p) | rel.weight] as weightList,
       d1.name,
       d2.name
  ORDER BY sumOfWeigths ASCENDING;

//
MATCH p=(start:Place{type: 'current'})-[:ROUTE]->(p1:Place)-[:ROUTE*0..1]->(p2:Place)-[:ROUTE]->(d1:Place)-[:ROUTE]->(d2:Place)

// join - cartesian product
MATCH (p:Place {type: 'orig'})
MATCH (d:Place {type: 'dest'})
RETURN p.name, d.name;

CALL algo.list();

MATCH (a:Place {name: "a"})-[r:ROUTE*]->(dest:Place {name: "d"})
RETURN a.name, collect(r.weight) as weightMap, dest.name;

// but reduced to one row only if collect is only item
MATCH (a:Place {name: "a"})-[r:ROUTE]->(b:Place)
RETURN a.name, collect(r.weight) as weightMap
