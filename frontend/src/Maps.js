import './App.css';
import {useEffect, useRef} from "react";

function Maps(props) {
    const canvasRef = useRef(); // allows variables to stay across re-renders
    const contextRef = useRef();
    // want to draw ways in useEffect, set listeners
    const canvas = canvasRef.current;
    const context = contextRef.current;
    const canvasMap = props.map;
    const canvasWidth = 800;
    const canvasHeight = 500;
    const minBoundLat = 42;
    const minBoundLon = -72;
    const maxBoundLat = 41.8;
    const maxBoundLon = -71.3;

    const drawWays = (context) => {
        context.fillStyle = "black"
        Object.keys(canvasMap).forEach((id) => {
            const curr = canvasMap[id]
            context.fillStyle = "black"
            context.beginPath()
            // context.moveTo(parseInt(curr[0]), parseInt(curr[1]));
            // context.lineTo(parseInt(curr[2]), parseInt(curr[3]));
            context.moveTo(calcLatPixels(curr[0]), calcLonPixels(curr[1]));
            context.lineTo(calcLatPixels(curr[2]), calcLonPixels(curr[3]));
            context.stroke();
        })
    }
    function calcLatPixels(lat) {
        const x = canvasWidth * ((lat - minBoundLat) / (maxBoundLat - minBoundLat))
    //    console.log(x + " x");
        return x;
    }

    function calcLonPixels(lon) {
        const y = canvasHeight * ((lon - minBoundLon) / (maxBoundLon - minBoundLon))
    //    console.log(y + " y");
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
        <canvas ref = {canvasRef} style = {{border:"2px solid black"}} width = "800" height="500" />
    </div>

}

    export default Maps;
// add more event listeners for
// have strictly drawing function in here, etc.