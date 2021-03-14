import './App.css';
import {useEffect, useRef} from "react";


function Maps(props) {
    const canvasRef = useRef(); // allows variables to stay across re-renders
    const contextRef = useRef();
    // want to draw ways in useEffect, set listeners
    const canvas = canvasRef.current;
    const context = contextRef.current;
    const map = props.map;

    const drawWays = context => {
        // look up for each for javascript
        context.fillStyle = "black"
        context.beginPath()
        context.moveTo(parseInt(map[0]), parseInt(map[1]));
        context.lineTo(parseInt(map[2]), parseInt(map[3]));
        context.stroke();
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