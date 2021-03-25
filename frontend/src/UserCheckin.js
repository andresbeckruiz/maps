import './App.css';
import axios from "axios";
import {useState, useEffect} from "react";
import {AwesomeButton} from "react-awesome-button";
import TextBox from "./TextBox";

function UserCheckin() {
  const [unixTime, setUnixTime] = useState(Date.now())
  const userDict = {}

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
        //yeah it should be the above URL
        axios.post(
            "http://localhost:4567/userCheckin", // maybe this URL? http://localhost:8080?last=timestamp
            toSend,
            config
        ).then(response => {
            Object.keys(response.data["userCheckins"]).forEach((id) => {
                const curr = response.data["userCheckins"][id]
                userDict.add(curr)
                console.log(curr)
            })
        })
            .catch(function (error) {
                console.log(error);
            });
        let newestTime = Date.now()
        setUnixTime(newestTime)
    }

  // const requestUserData = () => {
  //   // GET Request
  //
  //   fetch("http://localhost:8080?last=timestamp")
  //       .then(response => {
  //           console.log(response.data)
  //           return response.json()
  //       })
  //       .then(data => console.log(data))
  //   let newestTime = Date.now()
  //   setUnixTime(newestTime)
  // }

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

    //this is how we continuously pull from
    // useEffect(() => {
    //     setInterval(() => {
    //        updateUserDict()
    //     }, 3000)
    // })


    return <div>
        <AwesomeButton type="primary" onPress={updateUserDict}>Users!</AwesomeButton>
        {/*<h3></h3>*/}
        <TextBox label={"User Checkins"}/>
        <h6></h6>
    </div>
}
export default UserCheckin
