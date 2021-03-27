import axios from "axios";
import {useState, useEffect} from "react";
import {AwesomeButton} from "react-awesome-button";
import CheckinScroll from "./CheckinScroll"

/**
 * This class contains the UserCheckin logic, including pulling in updates from the server.
 * @returns {JSX.Element}
 * @constructor
 */
function UserCheckin() {
  const [unixTime, setUnixTime] = useState(Date.now())
  const [checkIns, setCheckIns] = useState([])

    /**
     * This post request gets the checkins from the server and formats the strings to be
     * displayed on the front end.
     */
    const updateUserDict = () => {
        let timestamp = unixTime
        const toSend = {
            currentTime : timestamp,
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        axios.post(
            "http://localhost:4567/userCheckin",
            toSend,
            config
        ).then(response => {
            Object.keys(response.data["userCheckin"]).forEach((id) => {
                const curr = response.data["userCheckin"][id]
                //userDict.push(curr)
                //console.log(curr)
                let formattedTime = convertToDate(curr["ts"])
                console.log(curr["name"] + " checked into " + curr["lat"] + ", " + curr["lon"]
                    + " at " + formattedTime)
                const newCheckin = {
                    id: curr["id"],
                    name: curr["name"],
                    lat: curr["lat"],
                    lon: curr["lon"],
                    time: formattedTime
                }
                let newArray = checkIns
                newArray.push(newCheckin)
                setCheckIns(newArray)
            })
        })
            .catch(function (error) {
                console.log(error);
            });
        let newestTime = Date.now()
        setUnixTime(newestTime)
    }

    /**
     * This function converts unix timestamps to a readable time
     * @param unix_timestamp
     * @returns {string}
     */
    function convertToDate(unix_timestamp) {
        let date = new Date(unix_timestamp * 1000);
        // Hours part from the timestamp
        let hours = date.getHours();
        // Minutes part from the timestamp
        let minutes = "0" + date.getMinutes();
        // Seconds part from the timestamp
        let seconds = "0" + date.getSeconds();
        // Will display time in 10:30:23 format
        let formattedTime = hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
        return formattedTime
    }

    //this is how we continuously pull from the server
    useEffect(() => {
        setInterval(() => {
           updateUserDict()
        }, 3000)
    },[])


    return <div>
        <h1> User Checkins </h1>
        <h4> Click on checkin text to see user's past checkins!</h4>
        <div style = {{display: "flex", alignItems: "center", justifyContent: "center"}}>
        <CheckinScroll items={checkIns}/>
        </div>
    </div>
}

export default UserCheckin
