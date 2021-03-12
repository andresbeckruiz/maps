import './App.css';
import {useEffect, useRef} from "react";


    function Maps() {
        const canvasRef = useRef(); // allows variables to stay across re-renders
        const contextRef = useRef();
    // want to draw ways in useEffect, set listeners
    const canvas = canvasRef.current;
    const context = contextRef.current;

    useEffect(
         () => {
             const canvas = canvasRef.current
             const context = canvas.getContext('2d')
             //Our first draw
             context.fillStyle = '#9515c3'
             context.fillRect(0, 0, context.canvas.width, context.canvas.height)
        //     canvas.addEventListener("mouseDown", (event) => {
        //         const mouseX = event.pageX  // should scale these from pixels to coordinates
        //         const mouseY = event.pageY
        //     })
        }
    )
    return <div>
        <canvas ref = {canvasRef} style = {{border:"2px solid black"}}/>
    </div>
}

export default Maps;
// add more event listeners for

// have strictly drawing function in here, etc.
// this should be able to access canvasRef and contextRef