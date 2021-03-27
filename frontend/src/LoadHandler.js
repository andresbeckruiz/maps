import React, {useEffect, useState} from 'react';
import {AwesomeButton} from "react-awesome-button"
import "react-awesome-button/dist/styles.css"
import axios from "axios"
import Canvas from "./Canvas"
import UserCheckin from "./UserCheckin"

/**
 * This component is responsible for loading our initial map onto the canvas and
 * handles loading additional maps if a new database is loaded in.
 * @returns {JSX.Element}
 * @constructor
 */
function LoadHandler() {
    const [map, setMap] = useState("");

    /**
     * Makes an axios request to backend to get initial map or another map loaded in.
     */
    const requestInitialMap = () => {
        const toSend = {
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        // sending this information to backend
        axios.post(
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
        requestInitialMap()
    },[])

    return (
        <div>
            <h1> Maps! </h1>
            <h2></h2>
            <AwesomeButton type="primary" onPress={requestInitialMap}>Load Map </AwesomeButton>
            <h2></h2>
            <Canvas map={map}/>
            <UserCheckin/>
        </div>
    );
}

export default LoadHandler;