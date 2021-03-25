import TextBox from "./TextBox";
import './App.css';
import React, {useEffect, useState} from 'react';
import {AwesomeButton} from "react-awesome-button"
import "react-awesome-button/dist/styles.css"
import axios from "axios"
import Maps from "./Maps"
import UserCheckin from "./UserCheckin"

function Route() {
    const [map, setMap] = useState(""); // updated by drawWays function -- use props to get data

    /**
     * Makes an axios request.
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
        requestInitialMap()
    },[])

    return (
        <div>
            <h1> Maps! </h1>
            <h2></h2>
            <AwesomeButton type="primary" onPress={requestInitialMap}>Load Map </AwesomeButton>
            <h2></h2>
            <Maps map={map}/>
            <UserCheckin/>
        </div>
    );
}

export default Route;