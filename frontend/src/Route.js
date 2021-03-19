import TextBox from "./TextBox";
import './App.css';
import React, {useEffect, useState} from 'react';
import {AwesomeButton} from "react-awesome-button"
import "react-awesome-button/dist/styles.css"
import axios from "axios";
import Maps, {drawWays} from "./Maps"

export const requestWayss = (context, canvas, canvasRef, contextRef, minBoundLat, minBoundLon, maxBoundLat, maxBoundLon) => {
    const toSend = {
        minLat: minBoundLat,
        minLon: minBoundLon,
        maxLat: maxBoundLat,
        maxLon: maxBoundLon
    };
    let config = {
        headers: {
            "Content-Type": "application/json",
            'Access-Control-Allow-Origin': '*',
        }
    }
    axios.post(
        "http://localhost:4567/way",
        toSend,
        config
    ).then(response => {
     //   canvasMap = response.data["way"];
        //  console.log("Canvas" + canvasMap)
        let canvasMapReturn = response.data["way"];
        let canvasReturn = canvasRef.current
        contextRef.current = canvasReturn.getContext('2d')
        let contextReturn = contextRef.current
        context.fillStyle = "#ffffff";
        context.fillRect(0, 0, 500, 500);
        drawWays(context, 0, canvasMapReturn, minBoundLon, minBoundLat,  maxBoundLon, maxBoundLat)
       // console.log(canvasMapReturn)
        let array = [canvasMapReturn, canvasReturn, contextReturn]
        return array
    })
        .catch(function (error) {
            console.log(error);
        });
}

function Route() {
    const [startLat, setStartLat] = useState(0); // returns a variable (0 here) into startLat
    // returns function setStartLat which I later can use to update startLat
    const [startLon, setStartLon] = useState(0);
    const [endLat, setEndLat] = useState(0);
    const [endLon, setEndLon] = useState(0);
    const [route, setRoute] = useState("");
    const [cache, setCache] = useState({});
    const [map, setMap] = useState(""); // updated by drawWays function -- use props to get data
    const [minBLon, setMinBLon] = useState(0);
    const [minBLat, setMinBLat] = useState(0);
    const [maxBLon, setMaxBLon] = useState(0);
    const [maxBLat, setMaxBLat] = useState(0);

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

    function caching() {
        let minLon = roundDown(minBLon)
        let minLat = roundDown(minBLat)
        let maxLon = roundUp(maxBLon)
        let maxLat = roundUp(maxBLat)
        let bounds = (minLon, minLat,  maxLon, maxLat)
        let lonDiff = maxLon - minLon
        let latDiff = maxLat - minLat

        for (let a = 1; a<lonDiff; a++) {
            for (let b = 1; b<latDiff; b++) {
                let tile = (minLon, minLat,  minLon + a, minLat + b)
                if (tile in cache) {
                    return cache[tile]
                } else {
                    cache[tile] = requestMapWithCache(minLon, minLat,  minLon + a, minLat + b)
                    drawWays()
                }
            }
        }
    }

    function roundDown(value) {
        let wholeNum = value*1000
        Math.floor(wholeNum)
        return (wholeNum/1000)
    }

    function roundUp(value) {
        let wholeNum = value*1000
        Math.ceil(wholeNum)
        return (wholeNum/1000)
    }

    const requestMapWithCache = (minLon, minLat,  maxLon, maxLat) => {
        const toSend = {
            minBoundLat : minLat,
            minBoundLon : minLon,
            maxBoundLat : maxLat,
            maxBoundLon : maxLon,
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
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


    return (
        <div>
            <h1> Maps! </h1>
            <h2></h2>
            <AwesomeButton type="primary" onPress={requestInitialMap}>Load Map </AwesomeButton>
            <h2></h2>
            <Maps map={map}/>
            {/*<h2>Ways: {JSON.stringify(route)}</h2>*/}
            {/*<TextBox label={"Source latitude"} change={setStartLat} value={startLat}/>*/}
            {/*<AwesomeButton type="primary" onPress={requestRoute}>Submit</AwesomeButton>*/}
        </div>
    );
}

export default Route;