import './App.css';
import {useEffect, useRef} from "react";
import {useState} from "react";
import axios from "axios";
import TextBox from "./TextBox";
import {AwesomeButton} from "react-awesome-button";



export const drawWays = (context, newMap, route, minBoundLon, minBoundLat,  maxBoundLon, maxBoundLat) => {
    if (newMap == 1) {
        console.log("drawing route")
        context.lineWidth = 4
    } else {
        console.log("drawing entire map")
        context.lineWidth = 1
        Object.keys(route).forEach((id) => {
            const curr = route[id]
            if (curr[4] == 'unclassified' || curr[4] == ''){
                curr.color = "#000000"
            } else {
                curr.color = "#008000"
            }
        })
    }
    Object.keys(route).forEach((id) => {
        const curr = route[id]
        context.strokeStyle = curr.color;
        context.beginPath()
        context.moveTo(500 * (curr[1] - minBoundLon) / (maxBoundLon - minBoundLon),
            500 * (curr[0] - maxBoundLat) / (minBoundLat - maxBoundLat));
        context.lineTo(500 * (curr[3] - minBoundLon) / (maxBoundLon - minBoundLon),
            500 * (curr[2] - maxBoundLat) / (minBoundLat - maxBoundLat));
        context.stroke();
    })
}

//NEED TO FIX BUG WHERE IF YOU SCROLL AND CLICK MAP BOUNDS ARE REDRAWN
function Maps(props) {
    const canvasRef = useRef(); // allows variables to stay across re-renders
    const contextRef = useRef();
    let canvas = canvasRef.current;
    let context = contextRef.current;
    //what ways we want to display
    let canvasMap = props.map;
    //let firstClick = 0
    const canvasWidth = 500;
    const canvasHeight = 500;
    //gonna have to make these state variables
    let minBoundLat = 41.82433; // make these state variables
    // everytime you reset page, state variables will not change
    let minBoundLon = -71.40729;
    let maxBoundLat = 41.82953;
    let maxBoundLon = -71.39572;
    const [mouseDown , setMouseDown] = useState([])
    const [mouseUp, setMouseUp] = useState([])
    const [firstClick, setFirstClick] = useState(0);
    const [firstMouseX, setFirstMouseX] = useState("");
    const [firstMouseY, setFirstMouseY] = useState("");
    const [secondMouseX, setSecondMouseX] = useState("");
    const [secondMouseY, setSecondMouseY] = useState("");
    const [shortestRoute, setShortestRoute] = useState("");
    // set a list of colors - certain type of ID be a color
    // add center point to circle array
    const route = [] // so I can access this everywhere
    // everytime you get a route, you add parsed ways to your route
    let info = ""
    let currNode = ""
    // 1. get ways from backend
    // 2. add everything to route variable
    // for each in js and then element.id, element.type = residential, etc. element.lat
    // 3. change the color for each route within this array
    // loop through route and set everything to red
    // 4. call draw
    // 5. after setting all to red, route will show up in red

    // const drawWays = (context, newMap, route) => {
    //     if (newMap == 1) {
    //         console.log("drawing route")
    //         info = route;
    //         context.lineWidth = 4
    //     } else {
    //         console.log("drawing entire map")
    //         info = route;
    //         console.log(typeof info + " info type")
    //         context.lineWidth = 1
    //         Object.keys(info).forEach((id) => {
    //             const curr = info[id]
    //             if (curr[4] == 'unclassified' || curr[4] == ''){
    //                 curr.color = "#000000"
    //             } else {
    //                 curr.color = "#008000"
    //             }
    //         })
    //     }
    //     Object.keys(info).forEach((id) => {
    //         const curr = info[id]
    //         context.strokeStyle = curr.color;
    //         context.beginPath()
    //         context.moveTo(calcLonPixels(curr[1]), calcLatPixels(curr[0]));
    //         context.lineTo(calcLonPixels(curr[3]), calcLatPixels(curr[2]));
    //         context.stroke();
    //     })
    // }

    function calcLonPixels(lon) {
        const x = canvasHeight * ((lon - minBoundLon) / (maxBoundLon - minBoundLon))
        return x;
    }

    function calcLatPixels(lat) {
        const y = canvasWidth * ((lat - maxBoundLat) / (minBoundLat - maxBoundLat))
        return y;
    }

    function calcLonCoord(canvas, xClick) {
        let x = xClick - canvas.offsetLeft;
        let ret = ((x*(maxBoundLon - minBoundLon))/canvasHeight) + minBoundLon
        return ret;
    }

    function calcLatCoord(canvas, yClick) {
        let y = yClick - canvas.offsetTop;
        let ret = ((y*(minBoundLat - maxBoundLat))/canvasHeight) + maxBoundLat
        return ret;
    }

    const requestRoute = () => {
        if (secondMouseX != "") {
            console.log("Called!!!!")
            const toSend = {
                startLon: firstMouseY,
                startLat: firstMouseX,
                endLon: secondMouseY,
                endLat: secondMouseX,
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
                Object.keys(shortestRoute).forEach((id) => {
                    const curr = shortestRoute[id]
                    curr.color = "#b00014";
                })
                console.log(shortestRoute.valueOf());
                drawWays(context, 1, response.data["shortestRoute"], minBoundLon, minBoundLat, maxBoundLon, maxBoundLat);
            })
                .catch(function (error) {
                    console.log(error);
                });
        }
    }

    const getNearestNode = (nearestLat, nearestLong) => {
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
                // console.log("CurrNode 1 IN METHOD" + currNode[1])
            })
            let lonPixels = calcLonPixels(currNode[1])
            let latPixels = calcLatPixels(currNode[0])
            // console.log(lonPixels + "    xxx")
            // console.log(latPixels + "    yyy")

            if (firstClick == 2){
                context.fillStyle = "#ffffff";
                context.fillRect(0, 0, canvasWidth, canvasHeight);
                context.beginPath();
                context.lineWidth = 5;
                context.strokeStyle = "#be1212";
                // console.log(lonPixels + " lon")
                // console.log(latPixels + " lat")
                context.arc(lonPixels, latPixels, 10, 0, Math.PI * 4, true);
                context.stroke();
                drawWays(context, 0, canvasMap, minBoundLon, minBoundLat,  maxBoundLon, maxBoundLat)
            }
            else {
                context.beginPath();
                context.lineWidth = 5;
                context.strokeStyle = "#be1212";
                // console.log(lonPixels + " lon")
                // console.log(latPixels + " lat")
                context.arc(lonPixels, latPixels, 10, 0, Math.PI * 4, true);
                context.stroke();
            }
        })
            .catch(function (error) {
                console.log(error);
            });
    }

    const requestWays = () => {
        console.log(minBoundLat)
        console.log(minBoundLon)
        const toSend = {
            minLat: minBoundLat, // srclat is key, startLat is value
            minLon: minBoundLon,
            maxLat: maxBoundLat,
            maxLon: maxBoundLon
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        //TODO: Fill in 1) location for request 2) your data 3) configuration
        axios.post( /// this is thing I am sending to backend
            "http://localhost:4567/way",
            toSend,
            config
        ).then(response => {
                canvasMap = response.data["way"];
                console.log("Canvas" + canvasMap)
                canvas = canvasRef.current
                contextRef.current = canvas.getContext('2d')
                context = contextRef.current
                context.fillStyle = "#ffffff";
                context.fillRect(0, 0, canvasWidth, canvasHeight);
                drawWays(context, 0, canvasMap, minBoundLon, minBoundLat,  maxBoundLon, maxBoundLat)
            })
            .catch(function (error) {
                console.log(error);
            });
    }


    // useEffect(() => {
    //         canvas = canvasRef.current
    //         contextRef.current = canvas.getContext('2d')
    //         context = contextRef.current
    //         canvas.addEventListener("mousedown", (event) => {
    //             let x = calcLonCoord(canvas, event.pageX)
    //             let y = calcLatCoord(canvas, event.pageY)
    //             if (firstClick == 2) {
    //                 context.fillStyle = "#ffffff";
    //                 context.fillRect(0, 0, canvasWidth, canvasHeight);
    //
    //                 context.beginPath();
    //                 context.lineWidth = 5;
    //                 context.strokeStyle = "#be1212";
    //                 context.arc(event.pageX - canvas.offsetLeft, event.pageY - canvas.offsetTop, 10, 0, Math.PI * 4, true);
    //                 context.stroke();
    //
    //                 drawWays(context, 0, props.map)
    //                 setFirstMouseX(x)
    //                 setFirstMouseY(y)
    //                 console.log(firstClick  + " third")
    //                 firstClick = 1
    //             } else {
    //                 context.beginPath();
    //                 context.lineWidth = 5;
    //                 context.strokeStyle = "#be1212";
    //                 context.arc(event.pageX - canvas.offsetLeft, event.pageY - canvas.offsetTop, 10, 0, Math.PI * 4, true);
    //                 context.stroke();
    //                 if (firstClick == 1) {
    //                     setSecondMouseX(x)
    //                     setSecondMouseY(y)
    //                     firstClick = 2
    //                     console.log(firstClick  + " 1")
    //                 } else if (firstClick == 0) {
    //                     setFirstMouseX(x)
    //                     setFirstMouseY(y)
    //                     firstClick = 1
    //                     console.log(firstClick  + " 0")
    //                 }
    //             }
    //         })
    //     }, []
    // )

    const up = (event) => {
        canvas = canvasRef.current
        let x = event.pageX - canvas.offsetLeft
        let y = event.pageY - canvas.offsetTop
        setMouseUp([x - mouseDown[0], y - mouseDown[1]])
    }

    const down = (event) => {
        canvas = canvasRef.current
        let x = event.pageX - canvas.offsetLeft
        let y = event.pageY - canvas.offsetTop
        setMouseDown([x,y])
    }

    const click = (event) => {
        canvas = canvasRef.current
        contextRef.current = canvas.getContext('2d')
        context = contextRef.current
        // console.log("Mouse down" + mouseDown[0])
        // console.log("Mouse down" + mouseDown[1])
        console.log("Mouse up" + mouseUp[0])
        console.log("Mouse up " + mouseUp[1])
        if (mouseUp[0] != 0 || mouseUp[1] != 0){
          console.log("not a click!")
          //updating bounded box
          let addedLat = mouseUp[1] * (0.000005)
          console.log("Added lat" + addedLat)
          let addedLon = mouseUp[0] * (0.00001)
          minBoundLat = minBoundLat + addedLat
          maxBoundLat = maxBoundLat + addedLat
          console.log("Min bound lat" + minBoundLat)
          console.log("Max bound lat" + maxBoundLat)
          minBoundLon = minBoundLon + addedLon
          maxBoundLon = maxBoundLon + addedLon
          requestWays()
        } //if its a click
        else {
            let x = calcLonCoord(canvas, event.pageX)
            let y = calcLatCoord(canvas, event.pageY)
            getNearestNode(y, x)
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
    }

    useEffect(() => {
            canvas = canvasRef.current
            contextRef.current = canvas.getContext('2d')
            context = contextRef.current
            canvasMap = props.map
            drawWays(context, 0, canvasMap, minBoundLon, minBoundLat,  maxBoundLon, maxBoundLat)
        }, [props.map]
    )


    return <div>
        <AwesomeButton type="primary" onPress={requestRoute}>Show Route</AwesomeButton>
        <h3></h3>
        <TextBox label={"Start Longitude: "} change={setFirstMouseY} value={firstMouseY}/>
        <TextBox label={"Start Latitude: "} change={setFirstMouseX} value={firstMouseX}/>
        <TextBox label={"End Longitude: "} change={setSecondMouseY} value={secondMouseY}/>
        <TextBox label={"End Latitude: "} change={setSecondMouseX} value={secondMouseX}/>
        <canvas onClick={click} onMouseDown={down} onMouseUp={up} ref={canvasRef}
                style={{border:"2px solid black"}} width="500" height="500" />
    </div>

}
export default Maps;
