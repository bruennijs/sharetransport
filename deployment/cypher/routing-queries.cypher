
MATCH p=(v:Vehicle)-[DISTANCE]->(h:Hop)-[d:DISTANCE*3]->(h2:Hop)
  WHERE ALL (hopName IN ['o1', 'd1', 'o2', 'd2'] WHERE hopName IN [n IN nodes(p) | n.name])
RETURN p as route,
       extract (var IN nodes(p) | var.name) as names,
       reduce(sum=0, r IN relationships(p) | r.weight + sum) as sumWeights
  ORDER BY sumWeights ASC;

MATCH p=(h1:Hop)-[d:DISTANCE*3]->(h2:Hop)
  WHERE ALL (uid IN ['o1', 'o2', 'd1', 'd2'] WHERE uid IN [n IN nodes(p) | n.uid])
AND de.bruenni.sharetransport.neo4j.routing.areHopsInOrder(p)
RETURN p as route,
       nodes(p) as nodes,
       extract (n IN nodes(p) | n.uid) as uids,
       reduce(sum=0, r IN relationships(p) | r.weight + sum) as sumWeights
  ORDER BY sumWeights ASC;

MATCH p=(root:Hop)-[d:DISTANCE*1..3]->(end:Hop)
  WHERE apoc.coll.isEqualCollection([n IN nodes(p) | n.uid], ['o1', 'd1', 'o2', 'd2'])
  AND de.bruenni.sharetransport.neo4j.routing.areHopsInOrder(p)
WITH p AS path,
      root,
     nodes(p) AS hops,
     filter (n IN nodes(p) WHERE n.origin = true) AS origins
UNWIND origins AS origin
MATCH (origin)-[:BOOKED_TO]->(destination:Hop)
WITH path,
     hops,
     origin,
     de.bruenni.sharetransport.neo4j.routing.weightOf(root, origin, path) AS tripWaitWeight,
     de.bruenni.sharetransport.neo4j.routing.weightOf(origin, destination, path) AS tripWeight
RETURN extract (n IN nodes(path) | n.uid) AS uids,
       apoc.map.mergeList(collect (apoc.map.fromLists([origin.uid], [tripWeight]))) AS tripWeights,
       reduce(sum=0, r IN relationships(path) | r.weight + sum) AS pathWeight
ORDER BY pathWeight ASC;

MATCH p=(h:Hop)-[*..1]->(:Hop)
RETURN h as origin,
  relationships(p) as relations,
  collect(id(h)) as ids;

MATCH (hopOn:Hop)<-[HOPS_ON]-(p:Passenger)-[HOPS_OFF]->(hopOff:Hop)
RETURN hopOff as off, hopOn as on, p as passenger

/*MATCH p=shortestPath
RETURN h as from, h2 as to, p as route, hFrom as hBookedTo*/

MATCH p=(h)
DETACH DELETE h;

MATCH p=(h:Hop)-->() RETURN h as hop, p as path;

