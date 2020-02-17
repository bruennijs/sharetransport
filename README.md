# Introduction
A MaaS - mobility as a service platform/framework.
## Motivation
The future of mobility is changing currently to a more efficient smarter mobility to save the resources of this world and drcrease the emission of CO2. In germany a car is not driving/used 23 hours a day and each car on the road is occupied by 1.1 persons per car. The motivation is to contribute open source software and algorithms to let people see mobility as a service and not to link the hardware of a car and possesion with mobility unconditionally.
Moreover this project shall make me familar with the domain of geospatial and GIS algorithms. Moreover I want to learn more about the usage of machine learning (like clustering, classification, regression) in this context and see whether it can help solve problems coming up, like reduce the network traffic for distance calculation using regression for prediction.  
## Approach
This software shall be the compromise between the concept of a taxi and public transport, which is called 'ride pooling'. 
Multiple passengers having a similar pickup and dropoff location and want to ride at a similar time can share a vehicle and book a ride on demand. The software manages a vehicle fleet and bookings to calculate the optimal route with minimal delay for each passenger.
The overall approach is to increase the passenger count per driven kilometer and to bring people in the same car to the same time with the following benefits/approaches:
* Reduce the costs for each traveler compared to a taxi
* Make use of public transport benefits without the disadvantage of having fixed routes and fixed schedules
* Increase the passengers p
* Give public communities (like small communes) the chance to transport inhabitants without the need of a bus transport and to connect outer landscapes to cities and public trsnaport systems
* Give companies the chance to transport employees instead of car communities

## Potential users/customers of this software
Customers are:
* Communes
* Companies to provide transport services, formerly known as ride communities but now executed by the company  

## Problems to solve
* 

## Differntiation

There are currently several companies and projects trying to create technology for this porpouse, like:
 * wunder.com
 * m-tribes.com
 * moia (VW)
 * clevershuttle.com
This software is a abstract mobiltity as a service platform and moreover a framework with algorithms for routing and path finding where external services (e.g. for calculation of distance vectors or durations betweens two points geo pionts) must be implemented abstract so that a operator must implement by their own (e.g. using google). It si not a product ready for deployment. 
This software does not:
* Implement payment of customers
     
# Licence
MIT

# Concept
## Use cases
TBD 
## Technical architecture
TBD

# Init neo4j database

docker exec  -it sharetransport-db sh -c 'cat /init/db_init.cypher | cypher-shell -u neo4j  -p admin --format verbose --address localhost'

