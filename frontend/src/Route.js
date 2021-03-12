import TextBox from "./TextBox";
import './App.css';
import React, {useState} from 'react';
import {AwesomeButton} from "react-awesome-button"
import "react-awesome-button/dist/styles.css"
import axios from "axios";
import Maps from "./Maps"

// instantiate maps -- instantiate conpomnent


function Route() {
    const [startLat, setStartLat] = useState(0); // returns a variable (0 here) into startLat
    // returns function setStartLat which I later can use to update startLat
    const [startLon, setStartLon] = useState(0);
    const [endLat, setEndLat] = useState(0);
    const [endLon, setEndLon] = useState(0);
    const [route, setRoute] = useState("");
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

        //Install and import this!
        //TODO: Fill in 1) location for request 2) your data 3) configuration
        axios.post( /// this is thing I am sending to backend
                "http://localhost:4567/way",
                toSend,
                config
        )
            .then(response => {
                console.log(response.data);
                //TODO: Go to the Main.java in the server from the stencil, and find what variable you should put here.
                //Note: It is very important that you understand how this is set up and why it works!
            //    let results = response.data["route"]
                setRoute(response.data["way"]);
                console.log(route);
            })

            .catch(function (error) {
                console.log(error);

            });
    }
    return (
        <div>
            <h1> Maps! </h1>
            <Maps/>
            <TextBox label={"Source latitude"} change={setStartLat} value={startLat}/>
            <TextBox label={"Source longitude"} change={setStartLon} value={startLon}/>
            <TextBox label={"Destination latitude"} change={setEndLat} value={endLat}/>
            <TextBox label={"Destination longitude"} change={setEndLon} value={endLon}/>
            <AwesomeButton type="primary" onPress={requestRoute}>Submit</AwesomeButton>
            <h2>Ways: {JSON.stringify(route)}</h2>
        </div>
    );
}
export default Route;