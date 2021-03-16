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
    let firstClick = 0
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

    class Circle {
        draw() {
            console.log("circle")
            console.log(this.x)
            console.log(this.y)
            this.context.beginPath();
            this.context.fillStyle = "#be1212"
            this.context.arc(this.x, this.y, 10, 0, Math.PI * 4, true);
            this.context.stroke();
        }
        clear() {
            this.canvas.remove();
        }
    }

    let firstCircle = new Circle();
  //  let secondCircle = new Circle();


    const drawWays = (context) => {
        context.fillStyle = "#000000"
        context.beginPath()
        Object.keys(canvasMap).forEach((id) => {
            const curr = canvasMap[id]
            context.moveTo(calcLonPixels(curr[1]), calcLatPixels(curr[0]));
            context.lineTo(calcLonPixels(curr[3]), calcLatPixels(curr[2]));
        })
        context.stroke();
    }

    useEffect(() => {
        canvas = canvasRef.current
        contextRef.current = canvas.getContext('2d')
        context = contextRef.current
        drawWays(context)
    }, [drawWays]
    )

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
        let y= yClick - canvas.offsetTop;
        let ret = ((y*(maxBoundLat - minBoundLat))/canvasHeight) + minBoundLat
        return ret;
    }

    const requestShortestRoute  = () => {
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
        )
            .then(response => {
                setShortestRoute(response.data["shortestRoute"]);
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    useEffect(() => {
        canvas = canvasRef.current
        contextRef.current = canvas.getContext('2d')
        context = contextRef.current
        canvas.addEventListener("mousedown", (event) => {
          //  console.log(event.pageX + "  x")
          //  console.log(event.pageY + "  y")
            let x = calcLonCoord(canvas, event.pageX)
            let y = calcLatCoord(canvas, event.pageY)
            if (firstClick == 0) {
                // firstCircle.clear()
                // firstCircle.context = context
                // firstCircle.x = event.pageX - canvas.offsetLeft;
                // firstCircle.y = event.pageY - canvas.offsetTop;
                // firstCircle.draw()
                setFirstMouseX(x)
                setFirstMouseY(y)
                firstClick = 1
            } else if (firstClick == 1) {
               // let secondCircle = new Circle();
                // secondCircle.context = context
                // secondCircle.x = event.pageX - canvas.offsetLeft;
                // secondCircle.y = event.pageY - canvas.offsetTop;
                // secondCircle.draw()
                setSecondMouseX(x)
                setSecondMouseY(y)
                firstClick = 0
            }
        })
    }, []
    )

    return <div>
        <AwesomeButton type="primary" onPress={requestShortestRoute}>Show Route</AwesomeButton>
        <h3></h3>
        <TextBox label={"Start Longitude: "} change={setFirstMouseY} value={firstMouseY}/>
        <TextBox label={"Start Latitude: "} change={setFirstMouseX} value={firstMouseX}/>
        <TextBox label={"End Longitude: "} change={setSecondMouseY} value={secondMouseY}/>
        <TextBox label={"End Latitude: "} change={setSecondMouseX} value={secondMouseX}/>
        <canvas ref = {canvasRef} style = {{border:"2px solid black"}} width = "500" height="500" />
    </div>

}
export default Maps;

// add more event listeners for
// have strictly drawing function in here, etc.