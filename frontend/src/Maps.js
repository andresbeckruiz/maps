
const canvasRef = useRef(); // allows variables to stay across re-renders
const contextRef = useRef();

    function Maps() {
    // want to draw ways in useEffect, set listeners
    const canvas = canvasRef.current;
    const context = contextRef.current;
    useEffect(
        () => {
            canvas.addEventListener("mouseDown", (event) => {
                const mouseX = event.pageX  // should scale these from pixels to coordinates
                const mouseY = event.pageY
            })
        }
    )
}
// add more event listeners for

// have strictly drawing function in here, etc.
// this should be able to access canvasRef and contextRef