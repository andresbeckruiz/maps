import './App.css';
import {useEffect, useRef} from "react";

function ShortestRoute(props) {
    const canvasRef = useRef(); // allows variables to stay across re-renders
    const contextRef = useRef();
    // want to draw ways in useEffect, set listeners
    const canvas = canvasRef.current;
    const context = contextRef.current;
    const canvasMap = props.map;
    const canvasWidth = 500;
    const canvasHeight = 500;
    const minBoundLat = 41.82953;
    const minBoundLon = -71.40729;
    const maxBoundLat = 41.82433;
    const maxBoundLon = -71.39572;

    const drawWays = (context) => {
        context.fillStyle = "black"
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

    useEffect(
        () => {
            const canvas = canvasRef.current
            const context = canvas.getContext('2d')
            drawWays(context)
            //     canvas.addEventListener("mouseDown", (event) => {
            //         const mouseX = event.pageX  // should scale these from pixels to coordinates
            //         const mouseY = event.pageY
            //     })
        }, [drawWays]
    )
    return <div>
        <canvas ref = {canvasRef} style = {{border:"2px solid black"}} width = "500" height="500" />
    </div>

}

export default ShortestRoute;
