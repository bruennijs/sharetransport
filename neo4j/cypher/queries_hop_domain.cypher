
MATCH p=(v:Vehicle)-[DISTANCE]->(h:Hop)-[d:DISTANCE*3]->(h2:Hop)
  WHERE ALL (hopName IN ['o1', 'd1', 'o2', 'd2'] WHERE hopName IN [n IN nodes(p) | n.name])
RETURN p as route,
       extract (var IN nodes(p) | var.name) as names,
       reduce(sum=0, r IN relationships(p) | r.weight + sum) as sumWeights
  ORDER BY sumWeights ASC;

MATCH p=(:Hop)-[d:DISTANCE*3]->(:Hop)
WHERE ALL (hopId IN [11,12,14,15] WHERE hopId IN [n IN nodes(p) | id(n)])
RETURN p as route,
      nodes(p) as hops,
      reduce(sum=0, r IN relationships(p) | r.weight + sum) as sumWeights;

MATCH p=(h:Hop)
RETURN collect(id(h)) as ids;

/*MATCH p=shortestPath
RETURN h as from, h2 as to, p as route, hFrom as hBookedTo*/

MATCH p=(h)
DETACH DELETE h;

MATCH (h:Hop {name: 'o1'}) RETURN h as hop;
