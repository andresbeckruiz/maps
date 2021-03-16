import TextBox from "./TextBox";
import './App.css';
import React, {useEffect, useState} from 'react';
import {AwesomeButton} from "react-awesome-button"
import "react-awesome-button/dist/styles.css"
import axios from "axios";
import Maps from "./Maps"

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
            srclat: startLat, // srclat is key, startLat is value
            srclon: startLon,
            destlat: endLat,
            destlon: endLon
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
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
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
    //allows the map to be loaded in when page loads
    useEffect(() => {
        requestMap()
    },[])

    return (
        <div>
            <h1> Maps! </h1>
            <h2></h2>
            <AwesomeButton type="primary" onPress={requestMap}>Load Map </AwesomeButton>
            <h2></h2>
            {/*<h2>Ways: {JSON.stringify(route)}</h2>*/}
            <Maps map={map}/>
            <TextBox label={"Source latitude"} change={setStartLat} value={startLat}/>
            <TextBox label={"Source longitude"} change={setStartLon} value={startLon}/>
            <TextBox label={"Destination latitude"} change={setEndLat} value={endLat}/>
            <TextBox label={"Destination longitude"} change={setEndLon} value={endLon}/>
            <AwesomeButton type="primary" onPress={requestRoute}>Submit</AwesomeButton>
        </div>
    );
}

export default Route;