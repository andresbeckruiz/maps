import './App.css';
import {useEffect, useRef} from "react";
import {useState} from "react";
import axios from "axios";
import TextBox from "./TextBox";
import {AwesomeButton} from "react-awesome-button";

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

    const [minBoundLat, setMinBoundLat] = useState(41.82433)
    const [minBoundLon, setMinBoundLon] = useState(-71.40729)
    const [maxBoundLat, setMaxBoundLat] = useState(41.82953)
    const [maxBoundLon, setMaxBoundLon] = useState(-71.39572)

    const [mouseDown , setMouseDown] = useState([])
    const [mouseUp, setMouseUp] = useState([])
    const [firstClick, setFirstClick] = useState(0);
    const [firstMouseX, setFirstMouseX] = useState("");
    const [firstMouseY, setFirstMouseY] = useState("");
    const [secondMouseX, setSecondMouseX] = useState("");
    const [secondMouseY, setSecondMouseY] = useState("");
    const [shortestRoute, setShortestRoute] = useState("");

    const [streetOne, setStreetOne] = useState("")
    const [streetTwo, setStreetTwo] = useState("")
    const [streetThree, setStreetThree] = useState("")
    const [streetFour, setStreetFour] = useState("")

    let info = ""
    let currNode = ""
    const [cache, setCache] = useState({});
    const ROUND_NUM = 0.01

    const drawWays = (context, newMap, route) => {
        if (newMap == 1) {
            console.log("drawing route")
            info = route;
            context.lineWidth = 4
        } else {
            console.log("drawing entire map")
            info = route;
            console.log(typeof info + " info type")
            context.lineWidth = 1
            Object.keys(info).forEach((id) => {
                const curr = info[id]
                //console.log(curr)
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
        let sLon = ""
        let sLat = ""
        let eLon = ""
        let eLat = ""
        let version = 0 // 0 for both clicks, 1 for both routes, 2 for one of each
        if (firstMouseX != "") {
            sLon = firstMouseY
            sLat = firstMouseX
            version = 2
        }
        if (secondMouseX != "") {
            eLon = secondMouseY
            eLat = secondMouseX
            version = 0
        } else {
            if (streetOne != "" && streetTwo != "") {
                sLon = streetOne
                sLat = streetTwo
            } if (streetOne != "" && streetTwo != "") {
                eLon = streetThree
                eLat = streetFour
                version = 1
            }
        }
        const toSend = {
            startLon: sLon,
            startLat: sLat,
            endLon: eLon,
            endLat: eLat,
            version: version
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
//             axios.post(
//                 "http://localhost:4567/shortestRoute",
//                 toSend,
//                 config
//             ).then(response => {
//                 setShortestRoute(response.data["shortestRoute"]);
//                 Object.keys(shortestRoute).forEach((id) => {
//                     const curr = shortestRoute[id]
//                     curr.color = "#b00014";
//                 })
//                 console.log(shortestRoute.valueOf());
//                 drawWays(context, 1, response.data["shortestRoute"], minBoundLon, minBoundLat, maxBoundLon, maxBoundLat);
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
            drawWays(context, 1, response.data["shortestRoute"]);
        })
        .catch(function (error) {
            console.log(error);
        });
//    }
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
            })
            let lonPixels = calcLonPixels(currNode[1])
            let latPixels = calcLatPixels(currNode[0])
            if (firstClick == 2){
                context.fillStyle = "#ffffff";
                context.fillRect(0, 0, canvasWidth, canvasHeight);
                context.beginPath();
                context.lineWidth = 5;
                context.strokeStyle = "#be1212";
                context.arc(lonPixels, latPixels, 10, 0, Math.PI * 4, true);
                context.stroke();
                drawWays(context, 0, canvasMap)
            }
            else {
                context.beginPath();
                context.lineWidth = 5;
                context.strokeStyle = "#be1212";
                context.arc(lonPixels, latPixels, 10, 0, Math.PI * 4, true);
                context.stroke();
            }
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
    //    setMouseUp(x - [mouseDown[0], y - mouseDown[1]])
        setMouseUp([mouseDown[0] - x, y - mouseDown[1]])
        // maybe mouseDown[0] - x instead ...
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
//           console.log("not a click!")
//           //updating bounded box
//           let addedLat = mouseUp[1] * (0.000005)
//           console.log("Added lat" + addedLat)
//           let addedLon = mouseUp[0] * (0.00001)
//           minBoundLat = minBoundLat + addedLat
//           maxBoundLat = maxBoundLat + addedLat
//           console.log("Min bound lat" + minBoundLat)
//           console.log("Max bound lat" + maxBoundLat)
//           minBoundLon = minBoundLon + addedLon
//           maxBoundLon = maxBoundLon + addedLon
//           requestWays()
            console.log("not a click!")
            //updating bounded box
            // let addedLat = mouseUp[1] * (0.000005) // play around with these numbers -- try a little bigger
            // let addedLon = mouseUp[0] * (0.00001)
            // should be related to 1/500 in some way
            let addedLat = mouseUp[1] * (0.00002)
            let addedLon = mouseUp[0] * (0.00002)
            let smallLat = minBoundLat + addedLat
            let bigLat = maxBoundLat + addedLat // look into these calculations (maybe - instead of +)
            let smallLon = minBoundLon + addedLon
            let bigLon = maxBoundLon + addedLon
            setMinBoundLat(smallLat)
            setMaxBoundLat(bigLat)
            setMinBoundLon(smallLon)
            setMaxBoundLon(bigLon)
           // requestWays()
            caching(smallLat, bigLat, smallLon, bigLon)
        } else { //if its a click
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
            drawWays(context, 0, canvasMap)
        }, [props.map]
    )

    function caching(smallLat, bigLat, smallLon, bigLon) {
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
                if (tile in cache) {
                    canvas = canvasRef.current
                    contextRef.current = canvas.getContext('2d')
                    context = contextRef.current
                 //   updatedMap.push(cache[tile])
                    Object.keys(cache[tile]).forEach((id) => {
                        const curr = cache[tile][id]
                           //  console.log(curr)
                        updatedMap.push(curr)
                    })
                    //     console.log((500 * (a - minBoundLon) / (maxBoundLon - minBoundLon)) + "  == " + a)
                    // console.log((500 * (b - maxBoundLat) / (minBoundLat - maxBoundLat)) + " == " + b)
                    //      context.fillRect(calcLatPixels(b), calcLonPixels(a), 5, 5);
                    drawWays(context, 0, cache[tile])
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
                             //      updatedMap.push(cache[tile])
                                Object.keys(cache[tile]).forEach((id) => {
                                    const curr = cache[tile][id]
                                    updatedMap.push(curr)
                                })
                                canvas = canvasRef.current
                                contextRef.current = canvas.getContext('2d')
                                context = contextRef.current
                                //   context.fillRect(calcLatPixels(b), calcLonPixels(a), 5, 5);
                                drawWays(context, 0, cache[tile])
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                }
            }
        }
        canvasMap = updatedMap
    }

    function roundDown(value) {
        let wholeNum = value/ROUND_NUM
        let rounded = Math.floor(wholeNum)
        return (rounded*ROUND_NUM)
    }

    function roundUp(value) {
        let wholeNum = value/ROUND_NUM
        let rounded = Math.ceil(wholeNum)
        return (rounded*ROUND_NUM)
    }

    return <div>
        <AwesomeButton type="primary" onPress={requestRoute}>Show Route</AwesomeButton>
        <h3></h3>
        <h3>Intersection One: </h3>
        <TextBox label={"Street 1 Name: "} change={setStreetOne} value={streetOne}/>
        <TextBox label={"Street 2 Name: "} change={setStreetTwo} value={streetTwo}/>
        <h3>Intersection Two: </h3>
        <TextBox label={"Street 3 Name: "} change={setStreetThree} value={streetThree}/>
        <TextBox label={"Street 4 Name: "} change={setStreetFour} value={streetFour}/>
        <canvas onClick={click} onMouseDown={down} onMouseUp={up} ref={canvasRef}
                style={{border:"2px solid black"}} width="500" height="500" />

        <TextBox label={"Start Longitude: "} change={setFirstMouseY} value={firstMouseY}/>
        <TextBox label={"Start Latitude: "} change={setFirstMouseX} value={firstMouseX}/>
        <TextBox label={"End Longitude: "} change={setSecondMouseY} value={secondMouseY}/>
        <TextBox label={"End Latitude: "} change={setSecondMouseX} value={secondMouseX}/>
    </div>

}
export default Maps;
