CALL algo.louvain.stream('Hop', 'DISTANCE', {
  weightProperty: 'weight',
  //levels: 3,
  direction: 'BOTH'
}) YIELD nodeId, community, communities
RETURN algo.asNode(nodeId).uid, community, communities;

CALL algo.scc.stream('Hop', 'DISTANCE', {
  weightProperty: 'weight',
//levels: 3,
  direction: 'BOTH'
}) YIELD nodeId, partition
RETURN algo.asNode(nodeId).uid, partition

CALL algo.wcc.stream('Hop', 'DISTANCE', {
  weightProperty: 'weight',
  levels: 3,
  direction: 'BOTH'
}) YIELD nodeId, setId
RETURN algo.asNode(nodeId).uid, setId

MATCH (h1:Hop), (h2:Hop)
WHERE h1 <> h2
RETURN h1.uid, h2.uid;

/*MATCH p=shortestPath
RETURN h as from, h2 as to, p as route, hFrom as hBookedTo*/

MATCH p=(h)
DETACH DELETE h;

MATCH p=(h:Hop)-->() RETURN h as hop, p as path;

