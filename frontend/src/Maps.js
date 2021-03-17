import './App.css';
import {useEffect, useRef} from "react";
import {useState} from "react";
import axios from "axios";
import TextBox from "./TextBox";
import {AwesomeButton} from "react-awesome-button";


function Maps(props) {
    const canvasRef = useRef(); // allows variables to stay across re-renders
    const contextRef = useRef();
    let canvas = canvasRef.current;
    let context = contextRef.current;
  //  const canvasMap = props.map;
    const [canvasMap, setCanvasMap] = useState(props.map);
    //let firstClick = 0
    const canvasWidth = 500;
    const canvasHeight = 500;
    //gonna have to make these state variables
    const minBoundLat = 41.82953; // make these state variables
    // everytime you reset page, state variables will not change
    const minBoundLon = -71.40729;
    const maxBoundLat = 41.82433;
    const maxBoundLon = -71.39572;
    const [mouseDown , setMouseDown] = useState([])
    const [mouseUp, setMouseUp] = useState([])
    const [firstClick, setFirstClick] = useState(0);
    const [firstMouseX, setFirstMouseX] = useState(0);
    const [firstMouseY, setFirstMouseY] = useState(0);
    const [secondMouseX, setSecondMouseX] = useState(0);
    const [secondMouseY, setSecondMouseY] = useState(0);
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
        const y = canvasWidth * ((lat - minBoundLat) / (maxBoundLat - minBoundLat))
        return y;
    }

    function calcLonCoord(canvas, xClick) {
        let x = xClick - canvas.offsetLeft;
        let ret = ((x*(maxBoundLon - minBoundLon))/canvasHeight) + minBoundLon
        return ret;
    }

    function calcLatCoord(canvas, yClick) {
        let y = yClick - canvas.offsetTop;
        let ret = ((y*(maxBoundLat - minBoundLat))/canvasHeight) + minBoundLat
        return ret;
    }

    const requestShortestRoute = () => {
        const toSend = {
            startLon : firstMouseY,
            startLat : firstMouseX,
            endLon : secondMouseY,
            endLat : secondMouseX,
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
            drawWays(context, 1, response.data["shortestRoute"]);
            })
            .catch(function (error) {
                console.log(error);
            });
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
                console.log("CurrNode 1 IN METHOD" + currNode[1])
            })
            let lonPixels = calcLonPixels(currNode[1])
            let latPixels = calcLatPixels(currNode[0])

            if (firstClick == 2){
                context.fillStyle = "#ffffff";
                context.fillRect(0, 0, canvasWidth, canvasHeight);
                context.beginPath();
                context.lineWidth = 5;
                context.strokeStyle = "#be1212";
                context.arc(200, lonPixels, latPixels, 0, Math.PI * 4, true);
                context.stroke();
                drawWays(context, 0, props.map)
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
        if (mouseDown[0] == 0 && mouseDown[1] == 0){
          console.log("THIS WAS A CLICK!")
        }
        let x = calcLonCoord(canvas, event.pageX)
        let y = calcLatCoord(canvas, event.pageY)
        getNearestNode(y, x)
        if (firstClick == 2) {
            setFirstMouseX(x)
            setFirstMouseY(y)
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

    useEffect(() => {
            canvas = canvasRef.current
            contextRef.current = canvas.getContext('2d')
            context = contextRef.current
            drawWays(context, 0, props.map)
        }, [props.map]
    )

    return <div>
        <AwesomeButton type="primary" onPress={requestShortestRoute}>Show Route</AwesomeButton>
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
