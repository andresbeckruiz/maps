import TextBox from "./TextBox";
import './App.css';
import React, {useState} from 'react';
import {AwesomeButton} from "react-awesome-button"
import "react-awesome-button/dist/styles.css"
import axios from "axios";
import Maps from "./Maps"
import {useEffect} from "react";

function Route() {
    const [startLat, setStartLat] = useState(0); // returns a variable (0 here) into startLat
    // returns function setStartLat which I later can use to update startLat
    const [startLon, setStartLon] = useState(0);
    const [endLat, setEndLat] = useState(0);
    const [endLon, setEndLon] = useState(0);
    const [route, setRoute] = useState("");
    const [map, setMap] = useState(""); // updated by drawWays function -- use props to get data
    /**
     * Makes an axios request.
     */
    const requestRoute = () => {
        const toSend = {
            //TODO: Pass in the values for the data. Follow the format the route expects!
            srclat : startLat, // srclat is key, startLat is value
            srclon : startLon,
            destlat : endLat,
            destlon : endLon
    };

        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        //TODO: Fill in 1) location for request 2) your data 3) configuration
        axios.post( /// this is thing I am sending to backend
                "http://localhost:4567/way",
                toSend,
                config
        )
            .then(response => {
                //TODO: Go to the Main.java in the server from the stencil, and find what variable you should put here.
                //Note: It is very important that you understand how this is set up and why it works!
                setRoute(response.data["way"]);
            })

            .catch(function (error) {
                console.log(error);
            });
    }

    const requestMap = () => {
        const toSend = {
            //TODO: Pass in the values for the data. Follow the format the route expects!
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        //Install and import this!
        //TODO: Fill in 1) location for request 2) your data 3) configuration
        axios.post( /// this is thing I am sending to backend
            "http://localhost:4567/map",
            toSend,
            config
        )
            .then(response => {
                setMap(response.data["map"]);
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    useEffect(
        () => {
            requestRoute()
            requestMap()
            //     canvas.addEventListener("mouseDown", (event) => {
            //         const mouseX = event.pageX  // should scale these from pixels to coordinates
            //         const mouseY = event.pageY
            //     })
        }, [requestRoute, requestMap]
    )

    return (
        <div>
            <h1> Maps! </h1>
            <TextBox label={"Source latitude"} change={setStartLat} value={startLat}/>
            <TextBox label={"Source longitude"} change={setStartLon} value={startLon}/>
            <TextBox label={"Destination latitude"} change={setEndLat} value={endLat}/>
            <TextBox label={"Destination longitude"} change={setEndLon} value={endLon}/>
            <AwesomeButton type="primary" onPress={requestRoute}>Submit</AwesomeButton>
            <h2></h2>
            <AwesomeButton type="primary" onPress={requestMap}>Show Ways</AwesomeButton>
            {/*<h2>Ways: {JSON.stringify(route)}</h2>*/}
            <Maps map={map}/>
        </div>
    );
}

export default Route;