import {useEffect, useRef} from "react";
import {useState} from "react";
import axios from "axios";
import TextBox from "./TextBox";
import {AwesomeButton} from "react-awesome-button";

/**
 * This class represents most of the canvas logic, including showing the route,
 * showing the circles, panning and zooming, and clearing the canvas.
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
function Canvas(props) {
    const canvasRef = useRef();
    const contextRef = useRef();
    const zoomConstant = 0.0015;
    let canvas = canvasRef.current;
    let context = contextRef.current;
    const canvasWidth = 500;
    const canvasHeight = 500;
    let mouseDown = []
    let mouseUp = []
    let info = ""
    let currNode = ""
    let scrolling = false;
    const ROUND_NUM = 0.01
    //what ways we want to display
    const [canvasMap, setCanvasMap] = useState(props.map)
    //other state variables we need
    const [minBoundLat, setMinBoundLat] = useState(41.82433)
    const [minBoundLon, setMinBoundLon] = useState(-71.40729)
    const [maxBoundLat, setMaxBoundLat] = useState(41.82953)
    const [maxBoundLon, setMaxBoundLon] = useState(-71.39572)
    const [firstClick, setFirstClick] = useState(0);
    const [firstMouseX, setFirstMouseX] = useState("");
    const [firstMouseY, setFirstMouseY] = useState("");
    const [secondMouseX, setSecondMouseX] = useState("");
    const [secondMouseY, setSecondMouseY] = useState("");
    const [shortestRoute, setShortestRoute] = useState("");
    const [firstCircle, setFirstCircle, ] = useState([])
    const [secondCircle, setSecondCircle, ] = useState([])
    const [streetOne, setStreetOne] = useState("")
    const [streetTwo, setStreetTwo] = useState("")
    const [streetThree, setStreetThree] = useState("")
    const [streetFour, setStreetFour] = useState("")
    const [cache, setCache] = useState({});

    /**
     * This function draws the ways when we don't care about the bounds being updated
     * asynchronously.
     * @param context
     * @param newMap representing map to draw.
     * @param route representing the route to draw.
     */
    const drawWays = (context, newMap, route) => {
        if (newMap == 1) {
            info = route;
            context.lineWidth = 4
        } else {
            info = route;
            context.lineWidth = 1
            Object.keys(info).forEach((id) => {
                const curr = info[id]
                //setting the color based on type of way
                if (curr[4] == 'unclassified' || curr[4] == ''){
                    curr.color = "#000000"
                } else {
                    curr.color = "#008000"
                }
            })
        }
        Object.keys(info).forEach((id) => {
            const curr = info[id]
            context.strokeStyle = curr.color;
            context.beginPath()
            context.moveTo(calcLonPixels(curr[1]), calcLatPixels(curr[0]));
            context.lineTo(calcLonPixels(curr[3]), calcLatPixels(curr[2]));
            context.stroke();
        })
    }

    /**
     * This is similar to the above function, except we pass in the bounds of the map
     * because they get updated asynchronously and we need to access them synchronously.
     * @param context
     * @param canvasMap map to display
     * @param route route to display
     * @param minLon min lon bound
     * @param maxLon max lon bound
     * @param minLat min lat bound
     * @param maxLat max lat bound
     */
    const drawWaysScrollSync = (context, canvasMap, route, minLon, maxLon, minLat, maxLat) => {
        Object.keys(canvasMap).forEach((id) => {
            const curr = canvasMap[id]
            if (curr[4] == 'unclassified' || curr[4] == ''){
                curr.color = "#000000"
            } else {
                curr.color = "#008000"
            }
        })
        //drawing map
        context.lineWidth = 1
        Object.keys(canvasMap).forEach((id) => {
            const curr = canvasMap[id]
            context.strokeStyle = curr.color;
            context.beginPath()
            context.moveTo(calcLonPixelsSync(curr[1], minLon, maxLon),
                calcLatPixelsSync(curr[0], minLat, maxLat));
            context.lineTo(calcLonPixelsSync(curr[3], minLon, maxLon),
                calcLatPixelsSync(curr[2], minLat, maxLat));
            context.stroke();
        })
        //drawing route if a route exists
        if (shortestRoute != ""){
            context.lineWidth = 4
            Object.keys(route).forEach((id) => {
                const curr = route[id]
                context.strokeStyle = "#be1212";
                context.beginPath()
                context.moveTo(calcLonPixelsSync(curr[1], minLon, maxLon),
                    calcLatPixelsSync(curr[0], minLat, maxLat));
                context.lineTo(calcLonPixelsSync(curr[3], minLon, maxLon),
                    calcLatPixelsSync(curr[2], minLat, maxLat));
                context.stroke();
            })
        }
        //checking if we need to draw circles
        if (firstCircle != [] && secondCircle == [] ){
            let firstLonPixels = calcLonPixels(firstCircle[1], minLon, maxLon)
            let firstLatPixels = calcLatPixels(firstCircle[0], minLat, maxLat)
            context.beginPath();
            context.lineWidth = 5;
            context.strokeStyle = "#be1212";
            context.arc(firstLonPixels, firstLatPixels, 10, 0,
                Math.PI * 4, true);
            context.stroke();
        }
        if (firstCircle != [] && secondCircle != [] ){
            let firstLonPixels = calcLonPixelsSync(firstCircle[1], minLon, maxLon)
            let firstLatPixels = calcLatPixelsSync(firstCircle[0], minLat, maxLat)
            let secondLonPixels = calcLonPixelsSync(secondCircle[1], minLon, maxLon)
            let secondLatPixels = calcLatPixelsSync(secondCircle[0], minLat, maxLat)
            context.beginPath();
            context.lineWidth = 5;
            context.strokeStyle = "#be1212";
            context.arc(firstLonPixels, firstLatPixels, 10, 0,
                Math.PI * 4, true);
            context.stroke();
            context.beginPath();
            context.arc(secondLonPixels, secondLatPixels, 10, 0,
                Math.PI * 4, true);
            context.stroke();
        }
    }

    /**
     * This function converts longitude to pixels.
     * @param lon
     * @returns {number} representing pixels
     */
    function calcLonPixels(lon) {
        const x = canvasHeight * ((lon - minBoundLon) / (maxBoundLon - minBoundLon))
        return x;
    }

    /**
     * This function converts longitude to pixels when we need the bounds synchronously.
     * @param lon
     * @returns {number} representing pixels
     */
    function calcLonPixelsSync(lon, minLon, maxLon) {
        const x = canvasHeight * ((lon - minLon) / (maxLon - minLon))
        return x;
    }

    /**
     * This function converts latitude to pixels.
     * @param lat
     * @returns {number} representing pixels
     */
    function calcLatPixels(lat) {
        const y = canvasWidth * ((lat - maxBoundLat) / (minBoundLat - maxBoundLat))
        return y;
    }

    /**
     * This function converts latitude to pixels when we need the bounds synchronously.
     * @param lon
     * @returns {number} representing pixels
     */
    function calcLatPixelsSync(lat, minLat, maxLat) {
        const y = canvasWidth * ((lat - maxLat) / (minLat - maxLat))
        return y;
    }

    /**
     * This function converts pixels to longitude.
     * @param canvas
     * @param xClick x position of click
     * @returns {number} representing longitude
     */
    function calcLonCoord(canvas, xClick) {
        let x = xClick - canvas.offsetLeft;
        let ret = ((x*(maxBoundLon - minBoundLon))/canvasHeight) + minBoundLon
        return ret;
    }

    /**
     * This function converts pixels to latitude.
     * @param canvas
     * @param yClick y position of click
     * @returns {number} representing latitdue
     */
    function calcLatCoord(canvas, yClick) {
        let y = yClick - canvas.offsetTop;
        let ret = ((y*(minBoundLat - maxBoundLat))/canvasHeight) + maxBoundLat
        return ret;
    }

    /**
     * This method requests the route from the backend.
     */
    const requestRoute = () => {
        let sLon = ""
        let sLat = ""
        let eLon = ""
        let eLat = ""
        if (firstMouseX != "") {
            sLon = firstMouseY
            sLat = firstMouseX
        }
        if (secondMouseX != "") {
            eLon = secondMouseY
            eLat = secondMouseX
        }
        const toSend = {
            startLon: sLon,
            startLat: sLat,
            endLon: eLon,
            endLat: eLat,
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        axios.post(
            "http://localhost:4567/shortestRoute",
            toSend,
            config
        ).then(response => {
            setShortestRoute(response.data["shortestRoute"]);
            Object.keys(response.data["shortestRoute"]).forEach((id) => {
                const curr = response.data["shortestRoute"][id]
                curr.color = "#b00014";
            })
            drawWays(context, 1, response.data["shortestRoute"]);
        })
        .catch(function (error) {
            console.log(error);
        });
    }

    /**
     * Finds intersection of first streets inputted.
     */
    const findIntersectionOne = () => {
        findIntersection(1)
    }

    /**
     * Finds intersection of second streets inputted.
     */
    const findIntersectionTwo = () => {
        findIntersection(2)
    }

    /**
     * This input finds the node of the intersection of the streets typed in.
     * @param num representing which street boxes have been inputted.
     */
    function findIntersection(num) {
        let sLon = ""
        let sLat = ""
        if (num == 1) {
            sLon = streetOne
            sLat = streetTwo
        } else {
            sLon = streetThree
            sLat = streetFour
        }
        const toSend = {
            startLon: sLon,
            startLat: sLat,
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
    axios.post(
        "http://localhost:4567/intersection",
        toSend,
        config
    ).then(response => {
        Object.keys(response.data["intersection"]).forEach((id) => {
            const curr = response.data["intersection"][id]
            getNearestNode(curr[0], curr[1], firstClick)
            setClicks(curr[0], curr[1])
        })
    })
        .catch(function (error) {
            console.log(error);
        });
    }

    /**
     * This function gets the nearest node so that we can draw a circle where the user clicks.
     * @param nearestLat representing nearest lat
     * @param nearestLong  representing nearest long
     * @param clickNum times the mouse has been clicked
     */
    const getNearestNode = (nearestLat, nearestLong, clickNum) => {
        const toSend = {
            nearLat : nearestLat,
            nearLon : nearestLong
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        axios.post(
            "http://localhost:4567/nearest",
            toSend,
            config
        ).then(response => {
            let data = response.data["nearest"]
            Object.keys(data).forEach((id) => {
                currNode = data[id]
            })
            let lonPixels = calcLonPixels(currNode[1])
            let latPixels = calcLatPixels(currNode[0])
            //want to clear other circles and draw the first circle
            if (clickNum == 2) {
                setShortestRoute("")
                setFirstCircle([currNode[0], currNode[1]])
                setSecondCircle([])
                context.fillStyle = "#ffffff";
                context.fillRect(0, 0, canvasWidth, canvasHeight);
                context.beginPath();
                context.lineWidth = 5;
                context.strokeStyle = "#be1212";
                context.arc(lonPixels, latPixels, 10, 0,
                    Math.PI * 4, true);
                context.stroke();
                drawWays(context, 0, canvasMap)
            }
            //want to draw the second circle now
            else if (clickNum == 1) {
                setSecondCircle([currNode[0], currNode[1]])
                context.beginPath();
                context.lineWidth = 5;
                context.strokeStyle = "#be1212";
                context.arc(lonPixels, latPixels, 10, 0,
                    Math.PI * 4, true);
                context.stroke();
                //should only happen when first click is registered
            } else {
                setFirstCircle([currNode[0], currNode[1]])
                context.beginPath();
                context.lineWidth = 5;
                context.strokeStyle = "#be1212";
                context.arc(lonPixels, latPixels, 10, 0,
                    Math.PI * 4, true);
                context.stroke();
            }
        })
            .catch(function (error) {
                console.log(error);
            });
    }

    /**
     * Calculates mouse up position.
     * @param event
     */
    const up = (event) => {
        canvas = canvasRef.current
        let x = event.pageX - canvas.offsetLeft
        let y = event.pageY - canvas.offsetTop
        mouseUp = [mouseDown[0] - x, y - mouseDown[1]]
    }

    /**
     * Calculates mouse down position
     * @param event
     */
    const down = (event) => {
        canvas = canvasRef.current
        let x = event.pageX - canvas.offsetLeft
        let y = event.pageY - canvas.offsetTop
        mouseDown = [x,y]
    }

    /**
     * This function is called when a click is registered on the canvas. If the user
     * has not moved its mouse, we consider it a click and draw a circle. Else, we
     * pan the map.
     * @param event
     */
    const click = (event) => {
        canvas = canvasRef.current
        contextRef.current = canvas.getContext('2d')
        context = contextRef.current
        if (Math.abs(mouseUp[0]) > 5 || Math.abs(mouseUp[1]) > 5 ) {
            //updating bounded box
            let lat1 = calcLatCoord(canvas,mouseUp[1])
            let lat2 = calcLatCoord(canvas,0)
            let addedLat = lat2 - lat1
            let lon1 = calcLatCoord(canvas,mouseUp[0])
            let lon2 = calcLatCoord(canvas,0)
            let addedLon = lon2 - lon1
            let smallLat = minBoundLat + addedLat
            let bigLat = maxBoundLat + addedLat
            let smallLon = minBoundLon + addedLon
            let bigLon = maxBoundLon + addedLon
            setMinBoundLat(smallLat)
            setMaxBoundLat(bigLat)
            setMinBoundLon(smallLon)
            setMaxBoundLon(bigLon)
            caching(smallLat, bigLat, smallLon, bigLon)
            //if its a click
        } else {
            let x = calcLonCoord(canvas, event.pageX)
            let y = calcLatCoord(canvas, event.pageY)
            getNearestNode(y, x, firstClick)
            setClicks(y, x)
        }
    }

    /**
     * This function keeps track of how many times the user has clicked on canvas so that
     * we can redraw circles as needed.
     * @param y y pos of cick
     * @param x x pos of click
     */
    const setClicks = (y, x) => {
        if (firstClick == 2) {
            setFirstMouseX(x)
            setFirstMouseY(y)
            setSecondMouseX("")
            setSecondMouseY("")
            setFirstClick(1)
        } else {
            if (firstClick == 1) {
                setSecondMouseX(x)
                setSecondMouseY(y)
                setFirstClick(2)
            } else {
                setFirstMouseX(x)
                setFirstMouseY(y)
                setFirstClick(1)
            }
        }
    }

    /**
     * This function updates the bounds of the map when a user zooms.
     * @param deltaY representing if we should zoom in or out
     */
    const updateZoomBounds = (deltaY) => {
        let zoomLat = 0.0015
        let zoomLon = 0.0015
        //zoom out
        if (deltaY > 0) {
            let smallLat = minBoundLat - zoomConstant
            let bigLat = maxBoundLat + zoomConstant
            let smallLon = minBoundLon - zoomConstant
            let bigLon = maxBoundLon + zoomConstant
            setMinBoundLat(smallLat)
            setMaxBoundLat(bigLat)
            setMinBoundLon(smallLon)
            setMaxBoundLon(bigLon)
            caching(smallLat, bigLat, smallLon, bigLon)
        }
        //zoom in
        else if (deltaY < 0) {
            let smallLat = minBoundLat + zoomConstant
            let bigLat = maxBoundLat - zoomConstant
            let smallLon = minBoundLon + zoomConstant
            let bigLon = maxBoundLon - zoomConstant
            setMinBoundLat(smallLat)
            setMaxBoundLat(bigLat)
            setMinBoundLon(smallLon)
            setMaxBoundLon(bigLon)
            caching(smallLat, bigLat, smallLon, bigLon)
        }
        //this timeout prevents the zoom function from being called continously
        setTimeout(() => {
            scrolling = false
        }, 2000)
    }

    /**
     * This is our event handler for a wheel event.
     * @param event
     */
    const zoom = (event) => {
        //only want to call this when the user stops wheeling.
        if (!scrolling){
            scrolling = true
            setTimeout(() => {
                updateZoomBounds(event.deltaY)
            },2000)
        }
    }

    /**
     * This function represents our caching logic
     * @param smallLat min lat bound
     * @param bigLat max lat bound
     * @param smallLon min lon bound
     * @param bigLon max lon bound
     */
    function caching(smallLat, bigLat, smallLon, bigLon) {
        //represents updated map
        let updatedMap = []
        let minLon = roundDown(smallLon)
        let minLat = roundDown(smallLat)
        let maxLon = roundUp(bigLon)
        let maxLat = roundUp(bigLat)
        canvas = canvasRef.current
        contextRef.current = canvas.getContext('2d')
        context = contextRef.current
        context.fillStyle = "#ffffff";
        context.fillRect(0, 0, canvasWidth, canvasHeight);
        for (let a = minLon; a<=maxLon; a = roundUp(a + ROUND_NUM)) {
            for (let b = minLat; b <= maxLat; b = roundUp(b + ROUND_NUM)) {
                let tile = a.toString() + b.toString()
                //if the tile already exists
                if (tile in cache) {
                    canvas = canvasRef.current
                    contextRef.current = canvas.getContext('2d')
                    context = contextRef.current
                    Object.keys(cache[tile]).forEach((id) => {
                        const curr = cache[tile][id]
                        updatedMap.push(curr)
                    })
                    //see method above
                    drawWaysScrollSync(context, cache[tile], shortestRoute, smallLon,
                        bigLon, smallLat, bigLat)
                    //if tile doesn't exist we have to request it
                } else {
                    const toSend = {
                        minLat: b,
                        minLon: a,
                        maxLat: roundUp(b + ROUND_NUM),
                        maxLon: roundUp(a + ROUND_NUM)
                    };
                    let config = {
                        headers: {
                            "Content-Type": "application/json",
                            'Access-Control-Allow-Origin': '*',
                        }
                    }
                    axios.post(
                        "http://localhost:4567/way",
                        toSend,
                        config
                    )
                        .then(response => {
                            cache[tile] = response.data["way"];
                            if (cache[tile] != {}) {
                                Object.keys(cache[tile]).forEach((id) => {
                                    const curr = cache[tile][id]
                                    updatedMap.push(curr)
                                })
                                canvas = canvasRef.current
                                contextRef.current = canvas.getContext('2d')
                                context = contextRef.current
                                drawWaysScrollSync(context, cache[tile], shortestRoute, smallLon,
                                    bigLon, smallLat, bigLat)
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                }
            }
        }
        setCanvasMap(updatedMap)
    }

    /**
     * This function rounds down.
     * @param value to round
     * @returns {number} rounded value
     */
    function roundDown(value) {
        let wholeNum = value/ROUND_NUM
        let rounded = Math.floor(wholeNum)
        return (rounded*ROUND_NUM)
    }

    /**
     * This method rounds up
     * @param value to be rounded
     * @returns {number} rounded value
     */
    function roundUp(value) {
        let wholeNum = value/ROUND_NUM
        let rounded = Math.ceil(wholeNum)
        return (rounded*ROUND_NUM)
    }

    /**
     * This method clears the map when the clear button is clicked
     */
    const clear = () => {
      canvas = canvasRef.current
      contextRef.current = canvas.getContext('2d')
      context = contextRef.current
      context.fillStyle = "#ffffff";
      context.fillRect(0, 0, canvasWidth, canvasHeight);
      setFirstCircle([])
      setSecondCircle([])
      setShortestRoute("")
      drawWays(context, 0, canvasMap)
    }

    //we useEffect here so that we can load in the map and draw it if a new database is loaded
    useEffect(() => {
            canvas = canvasRef.current
            contextRef.current = canvas.getContext('2d')
            context = contextRef.current
            //resetting state values
            setFirstClick(0)
            setFirstMouseX("")
            setFirstMouseY("")
            setSecondMouseX("")
            setSecondMouseY("")
            setShortestRoute("")
            setFirstCircle([])
            setSecondCircle([])
            setStreetOne("")
            setStreetTwo("")
            setStreetThree("")
            setStreetFour("")
            setCache({})
            context.fillStyle = "#ffffff";
            context.fillRect(0, 0, canvasWidth, canvasHeight);
            setCanvasMap(props.map)
            drawWays(context, 0, props.map)
        }, [props.map]
    )

    return <div>
        <TextBox label={"Street 1 Name: "} change={setStreetOne} value={streetOne}/>
        <TextBox label={"Street 2 Name: "} change={setStreetTwo} value={streetTwo}/>
        <br/>
        <AwesomeButton type="primary" onPress={findIntersectionOne}>Set Intersection One: </AwesomeButton>
        <br/>
        <br/>
        <TextBox label={"Street 3 Name: "} change={setStreetThree} value={streetThree}/>
        <TextBox label={"Street 4 Name: "} change={setStreetFour} value={streetFour}/>
        <br/>
        <AwesomeButton type="primary" onPress={findIntersectionTwo}>Set Intersection Two: </AwesomeButton>
        <br/>
        <br/>
        <div style={{display: "flex", flexDirection: "row", justifyContent: "space-evenly"}}>
            <AwesomeButton type="primary" onPress={requestRoute}>Show Route</AwesomeButton>
            <AwesomeButton type="primary" onPress={clear}>Clear</AwesomeButton>
        </div>
        <br/>
        <br/>
        <canvas onClick={click} onMouseDown={down} onMouseUp={up} onWheel={zoom} ref={canvasRef}
                style={{border:"2px solid black"}} width="500" height="500" />
    </div>

}

export default Canvas;
