import './App.css';
import {useEffect, useRef} from "react";
import {useState} from "react";
import axios from "axios";


function Maps(props) {
    const canvasRef = useRef(); // allows variables to stay across re-renders
    const contextRef = useRef();
    let canvas = canvasRef.current;
    let context = contextRef.current;

    const clickRef = useRef();
    let firstClick = clickRef.current

    const canvasMap = props.map;
    const canvasWidth = 500;
    const canvasHeight = 500;
    const minBoundLat = 41.82953;
    const minBoundLon = -71.40729;
    const maxBoundLat = 41.82433;
    const maxBoundLon = -71.39572;
    const [firstMouseX, setFirstMouseX] = useState(0);
    const [firstMouseY, setFirstMouseY] = useState(0);
    const [secondMouseX, setSecondMouseX] = useState(0);
    const [secondMouseY, setSecondMouseY] = useState(0);
    const [shortestRoute, setShortestRoute] = useState("");

    const drawWays = (context) => {
        context.beginPath()
        Object.keys(canvasMap).forEach((id) => {
            const curr = canvasMap[id]
            context.fillStyle = "black"
            context.moveTo(calcLonPixels(curr[1]), calcLatPixels(curr[0]));
            context.lineTo(calcLonPixels(curr[3]), calcLatPixels(curr[2]));
        })
        context.stroke();
    }

    function calcLonPixels(lon) {
        const x = canvasHeight * ((lon - minBoundLon) / (maxBoundLon - minBoundLon))
        return x;
    }

    function calcLatPixels(lat) {
        const y = canvasWidth * ((lat - minBoundLat) / (maxBoundLat - minBoundLat))
        return y;
    }

    useEffect(() => {
        canvas = canvasRef.current
        contextRef.current = canvas.getContext('2d')
        context = contextRef.current

        drawWays(context)
        canvas.addEventListener("mousedown", (event) => {
        console.log(event.pageX + "  x")
        console.log(event.pageY + "  y")
        setFirstMouseX(event.pageX)
        setFirstMouseY(event.pageY)
            /*
            console.log(typeof firstClick)
            if (typeof firstClick == 'undefined') {
                firstClick = 0
                console.log("went to undefined if")
            } else if (firstClick == 0) {
                console.log(firstClick + " on x")
                setFirstMouseX(event.pageX)
                setFirstMouseY(event.pageY)
                firstClick = 1
            } else if (firstClick == 1) {
                console.log(firstClick + "  on y")
                setSecondMouseX(event.pageX)
                setSecondMouseY(event.pageY)
                firstClick = 0
            }
*/

            const toSend = {
                mx : firstMouseX,
                my : firstMouseY,
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
            )
                .then(response => {
                   // console.log(response.data);
                    //TODO: Go to the Main.java in the server from the stencil, and find what variable you should put here.
                    //Note: It is very important that you understand how this is set up and why it works!
                    setShortestRoute(response.data["shortestRoute"]);
                })

                .catch(function (error) {
                    console.log(error);

                });
        })
    }, [drawWays]
    )

    return <div>
        <canvas ref = {canvasRef} style = {{border:"2px solid black"}} width = "500" height="500" />
        <h3> First Coordinate: ({JSON.stringify(firstMouseX)},{JSON.stringify(firstMouseY)})</h3>
        <h3> Second Coordinate: ({JSON.stringify(secondMouseX)},{JSON.stringify(secondMouseY)})</h3>
    </div>

}
export default Maps;
// add more event listeners for
// have strictly drawing function in here, etc.