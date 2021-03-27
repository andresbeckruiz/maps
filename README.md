## TODO: Update readme with information about your Canvas 1 + 2 project! ##

Canvas 1 and 2 README

------------------------------------------------------------------------------------------------------

Property Based Testing Response:

Property based testing on this assignment could be useful for comparing our Djikstra's algorithm
implementation with our A* algorithm implementation. We probably feel more confident about 
Djikstra's, since A* is Djikstra's with an added hueristic, so if their outputs matched we could
be confident that both algorithms produce correct outputs. For PBT using the route command, 
a property we would check for in comparing the routes is total distance of the routes. 
We could calculate this by adding up the distances of each edge, or Way, in the path, 
and we could compare these distances for Djikstra's and A* and they should be the same. 
Since our route command also uses the nearest command to find the nearest neighbor to the target 
latitude and longitude inputted, we could check the distance of the Node returned from the target 
as a property for testing our nearest command. 

------------------------------------------------------------------------------------------------------

Division of Labor:

Andres did most of the work outlining and creating the necessary classes and 
interfaces for the project, including WayNodes, Ways, Graph, GraphEdge, and GraphNode. Andres also 
defined the methods in the GraphNode and GraphEdge interfaces needed to run djikstras. Andres also
made edits to Djikstra's method to create the A* method. Andres also fixed checkstyle errors and 
commented the code.

Mit did most of the caching implementation, including looking up the proper documentation for 
GoogleGuavaCache. He also used the DB browser to play with SQl commands until we got 
the correct queries we wanted. Mit also implemented the nearest and ways commands for MapsLogic, 
including defining helper methods in MapsLogic that were needed to carry out these commands. 

We both wrote system and Junit tests to test our implementation. We also both helped write 
the route and maps command and Djikstra's search method. 

------------------------------------------------------------------------------------------------------

Known Bugs: None

Note on Testing on Department Machines: We could not figure out 
how to test on department machines, due to the fact that the database
was located in a different directory, so we cannot verify that 
our code works exactly the same as it does on our local machines

------------------------------------------------------------------------------------------------------

Design Details:

One significant design choice that we made was creating GraphNode and GraphEdge interfaces 
that contained methods that are required for any Nodes or Edges in a graph input into 
the A* algorithm. This allows our A* algorithm to work on a graph with any type of node and edges,
as long as it implements all the required methods. We also created a custom comparator to compare
nodes in the graph for the A* algorithm. This custom comparator compares the total weight of the nodes,
which is the distance it took to get to that node plus the distance left to reach the goal. 

Another choice we made was creating classes that represented Nodes and Ways in the database, named
WayNodes and Way, respectively. These classes implemented GraphNode and GraphEdge, respectively, so 
that they could be used in our A* algorithm. 

In our MapsLogic class, we handle parsing and checking the validity of all the maps commands and
other Canvas logic, including the definition of the map, ways, and nearest commands. We also handle
the formatting of strings to print results to the terminal. 

In terms of code reuse, Andres used his REPL code from Stars and Mit used his
KDTree code from Stars. We copied Andres' REPL code into Mit's old Stars repository, and most of it 
fit pretty well into the repository. All we had to change was that Mit's run method for MapsLogic 
and StarsLogic took in a String instead of an array of strings, so we just edited Mit's run method 
to take in an array of Strings and made small changes elsewhere to fix errors that arose from this.

------------------------------------------------------------------------------------------------------

Runtime/Space Optimizations:

To improve the runtime of our route command, we implemented a GraphNodeNeighborsCache. This cache
maps nodes to a list of their neighbors to reduce the amount of queries we have to make. If
the node already exists in our cache, we can just get a list of its neighbors. We also
implemented a WayNodeCache, that maps IDS to their WayNodes. If the ID already exists in our cache, 
we can just get the WayNode instead of querying the database.

------------------------------------------------------------------------------------------------------

Running Tests: To run our Junit tests, just run "mvn package". To run our system tests, run
./cs32-test test/system/maps1+2/*.test with the flag -t 60 to check for 60 second timeouts. 

We expect our maps database to be under the folder data/maps. This is how our tests are structured
------------------------------------------------------------------------------------------------------


Tests Wrote/Tried by Hand: 

In order to test our route command on the big database, since it was not feasible to find a shortest path
by hand given the size of the database, we first looked at a map of providence to find 
routes for the route command that took an intersection and generated a few test examples. 
We also compared our raw terminal outputs with other students (David Han and Ming-May Hu) to further 
test our validity.


Some of the things we tested for included:
    Being able to load both maps
    Being able to load multiple maps and ensuring that caching works
    Error checking invalid map arguments
    Testing the validity of our nearest/ways/route method
    Error checking our nearest/ways/route method
    Testing that a database was loaded before we ran our nearest/ways/route method
    We also did extensive testing on route caching to make sure that we could run 
    multiple large queries
    We tested for route timeouts by taking our output from terminal (regardless
    of validity) and checking how long it took. 

We wrote our Junit tests and system tests using the smaller database and
to check for error catching and edge cases in our implementations. 

IMPORTANT NOTE ABOUT TESTING: 
We tested our aStar algorithm in the Graph class in GraphTest.java. However, it does not seem
to increase our Jacoco score for the Graph class (it says that we have not tested the aStar method at
all). We are not sure why this is happening, but we have tested that aStar produces a correct output. 

------------------------------------------------------------------------------------------------------

Building and Running:

In order to build our project, you can run "mvn package", and to run it enter "./run".



