
Maps 3 and 4 README


Project Specifications:

Maps 1: https://cs.brown.edu/courses/cs0320/projects/maps-part-one.html

Maps 2: https://cs.brown.edu/courses/cs0320/projects/maps-part-two.html

Maps 3: https://cs.brown.edu/courses/cs0320/projects/maps-part-three.html

Maps 4: https://cs.brown.edu/courses/cs0320/projects/maps-part-four.html


------------------------------------------------------------------------------------------------------

REPL and Djikstra Used:

We used Andres' Djikstra and Bridget's REPL. For the rest of the maps code, we used Andres'
code from his Maps 1+2 project.

------------------------------------------------------------------------------------------------------

Division of Labor:

Andres did most of the work implementing zooming and scrolling on the map. Andres also did most of the 
work updating SQL commands to make them faster for Maps 3+4. Andres also did most of the work displaying
the checkin data on the front end and coming up with the component design for Maps 4. Andres also did
debugging work making sure that routes and circles stayed on the map properly when scrolling or zooming
happened.

Bridget did most of the work creating the function that draws the ways on the canvas. She also created
the functions that converting lon/lat to pixels and back and figured out the math behind it. Bridget
also implemented the front end caching. Bridget also made the class and functions that store user checkin
data. Bridget also did work surrounding finding nodes based on streets inputted. 

Both partners worked on commenting the code, fixing checkstyle errors, and doing the README and reflection.

------------------------------------------------------------------------------------------------------

Known Bugs: A rare bug, but when scrolling on the map, the canvas will draw multiple ways on top of each other
or will draw multiple routes on top of each other and it will look messy. It should go away if you just 
scroll again.

Another thing is that we did not implement the functionality where if you type in two streets it pans over
to the intersection, as this was giving us bugs as well. 

Lastly, this is not really a bug, but e.preventDefault wasn't working for us, so when you zoom on the map, 
the page scrolls as well.

Note on Testing on Department Machines: We could not figure out
how to test on department machines, due to the fact that the database
was located in a different directory, so we cannot verify that
our code works exactly the same as it does on our local machines

------------------------------------------------------------------------------------------------------

Design Details:

IMPORTANT DESIGN CHOICES: When a user clicks if there are two circles already there, it will erase those
two circles and show the new circle. Furthermore, if a route is displayed it will be erased if a user
clicks on the map. Circles and routes can also be cleared using the "Clear" button on the front end. 
Another choice is in order to show the route (when there are two circles on the map), you have to click
the "Show Route" button to show the route (it is not shown automatically). Also, for user checkins, you
have to click the text of a checkin to get the users' previous checkins to display. 

One major design choice we made for Maps 3 was having one component handle loading in the initial ways
when the database is first loaded in and if any other databases are loaded in (we called this component
LoadHandler). This LoadHandler contains our Canvas component, which holds all of our Canvas logic. 
On the front end, we decided to include buttons for Showing the route, clearing the route and circles,
and a button for loading in a new map. 

Some design choices we made for Maps 4 was having lots of different components that contain one another. 
Our UserCheckin component contains the logic that gets the server checkins and displays them. It contains
a CheckinScroll component which just represents the scroll box where you can scroll to see checkins. Each
individual checkin is its own component, called a CheckinItem, so that each checkin can display user 
information when the text is clicked. When a CheckinItem is clicked, it passes in user data to 
PastCheckinsScroll, which is another scrollbox that appears right under the checkin containing all of the
user past checkins. 

In terms of fitting our code bases together, there were not many changes we had to make. The only changes
we had to make was to Bridget's REPL; all we had to do was change her run command and her interface for 
her REPL slightly to work with my design for MapsLogic. 

------------------------------------------------------------------------------------------------------

Runtime/Space Optimizations:

To improve the runtime of our front end when loading in ways data and drawing it on the canvas, 
we implemented front end caching in our Canvas.js component. This caching function stores way data as 
tiles. When we redraw the canvas, we first check to see if the tile already exists in our cache. 
If it already exists, we don't have to request for the ways needed to draw on the canvas, which 
significantly improves the runtime of how fast the map loads. 

------------------------------------------------------------------------------------------------------

Running Tests: To run our Junit tests for Maps 1+2, just run "mvn package". To run our system tests for 
Maps 1+2, run ./cs32-test test/system/maps1+2/*.test with the flag -t 60 to check for 60 second timeouts.

We expect our maps database to be under the folder data/maps. This is how our tests are structured

For Maps 3+4, we did not write any system tests or unit tests for our front end because it was not 
required. We tested our front end using console logs and playing with it on Google Chrome. 

------------------------------------------------------------------------------------------------------

Building and Running:

IMPORTANT: When running the front end, make sure you load in the maps.sqlite3 database by entering
"map data/maps/maps.sqlite3" before running npm start (this is not done automatically). 
To load new databases in, you can use the map command, wait for the database to load, and press the
"Load Map" button on the front end. 

In order to build our project, you can run "mvn package", and to run it enter "./run".

In order to run our project with the gui, you can run ./run -gui in the root folder,
and then run npm start in the front end folder. In order to run it with the server, make sure you run 
python3 ./cs032_maps_location_tracking_py3 8080 5 -s in the root folder before running the gui and 
starting the front end. 


------------------------------------------------------------------------------------------------------

Browser:

We tested our front end on Google Chrome. 






















