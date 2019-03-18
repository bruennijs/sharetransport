// find shortest path via one hop
MATCH (start:Place {name:"a"})-[r:ROUTE]->(via:Place)-[r2:ROUTE]->(stop:Place)
RETURN start,r,via,r2,stop,(r.weight + r2.weight) as sum
ORDER BY sum
LIMIT 1;