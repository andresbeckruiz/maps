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
   // const canvasMap = props.map;
    const [canvasMap, setCanvasMap] = useState(props.map);
    const canvasWidth = 500;
    const canvasHeight = 500;
    //gonna have to make these state variables
    const minBoundLat = 41.82953; // make these state variables
        // everytime you reset page, state variables will not change
    const minBoundLon = -71.40729;
    const maxBoundLat = 41.82433;
    const maxBoundLon = -71.39572;
    const [firstMouseX, setFirstMouseX] = useState(0);
    const [firstMouseY, setFirstMouseY] = useState(0);
    const [secondMouseX, setSecondMouseX] = useState(0);
    const [secondMouseY, setSecondMouseY] = useState(0);
    const [shortestRoute, setShortestRoute] = useState("");
    const [circle, setCircle] = useState([]);
    // set a list of colors - certain type of ID be a color
  //  let circle = [] // to clear, reset this circle to an empty array
            // add center point to circle array
    const route = [] // so I can access this everywhere
        // everytime you get a route, you add parsed ways to your route
    let info = ""
    // 1. get ways from backend
    // 2. add everything to route variable
                // for each in js and then element.id, element.type = residential, etc. element.lat
    // 3. change the color for each route within this array
        // loop through route and set everything to red
    // 4. call draw
    // 5. after setting all to red, route will show up in red

    class Circle {
        draw() {
            // console.log("circle")
            // console.log(this.x)
            // console.log(this.y)
            context.beginPath();
            context.lineWidth = 5;
            context.strokeStyle = "#be1212";
            context.arc(this.x, this.y, 10, 0, Math.PI * 4, true);
            context.stroke();
        }
    }

  //  let firstCircle = new Circle();
  //  let secondCircle = new Circle();


    const drawWays = (context, newMap, route) => {
       if (newMap == 1) {
           console.log("noway")
           info = route;
           context.lineWidth = 4
       } else {
           console.log("hi")
           info = canvasMap;
           console.log(info.length)
           context.lineWidth = 1
           Object.keys(canvasMap).forEach((id) => {
               const curr = canvasMap[id]
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

    useEffect(() => {
        canvas = canvasRef.current
        contextRef.current = canvas.getContext('2d')
        context = contextRef.current
        drawWays(context, 0, "")
        console.log("running")
    }, [canvasMap]
    )

      //  })
   //     }, [drawWays]
        //}, [drawWays]
 //   )
    // useEffect(() => {
    //         canvas = canvasRef.current
    //         contextRef.current = canvas.getContext('2d')
    //         context = contextRef.current
    //         drawWays(context, 0, "")
    //     }, [callEffect]
    // )

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
              //  console.log(circle.length + " len 1")
              //  circle.pop()
              //  circle.pop()
            //    circle = []
             //   console.log(circle.length + " len 2")
                setShortestRoute(response.data["shortestRoute"]);
                Object.keys(shortestRoute).forEach((id) => {
                    const curr = shortestRoute[id]
                    curr.color = "#b00014";
                })
                drawWays(context, 1, shortestRoute);

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
                let firstCircle = new Circle();
                firstCircle.context = context
                firstCircle.x = event.pageX - canvas.offsetLeft;
                firstCircle.y = event.pageY - canvas.offsetTop;
                circle.push(firstCircle)
                firstCircle.draw()
                setFirstMouseX(x)
                setFirstMouseY(y)
                firstClick = 1
                console.log(firstClick  + " first")
            } else if (firstClick == 1) {
                let secondCircle = new Circle();
                secondCircle.context = context
                secondCircle.x = event.pageX - canvas.offsetLeft;
                secondCircle.y = event.pageY - canvas.offsetTop;
                circle.push(secondCircle)
                secondCircle.draw()
                setSecondMouseX(x)
                setSecondMouseY(y)
                firstClick = 2
                console.log(firstClick  + " second")
            } else {
                 context.fillStyle = "#ffffff";
                 context.fillRect(0, 0, canvasWidth, canvasHeight);

                setCircle([])
                let firstCircle = new Circle();
                firstCircle.context = context
                firstCircle.x = event.pageX - canvas.offsetLeft;
                firstCircle.y = event.pageY - canvas.offsetTop;
                circle.push(firstCircle)
                firstCircle.draw()
                drawWays(context, canvasMap, "")
                 setFirstMouseX(x)
                 setFirstMouseY(y)
                 console.log(firstClick  + " third")
                firstClick = 1

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