import axios from "axios";
import {useState} from "react/cjs/react.production.min";

function UserCheckin() {
  const [unixTime, setUnixTime] = useState(Date.now())

  const requestUserData = () => {
    // GET Request
    let timestamp = unixTime
    fetch("http://localhost:8080?last=timestamp")
        .then(response => {
            console.log(response.data)
            return response.json()
        })
        .then(data => console.log(data))
    let newestTime = Date.now()
    setUnixTime(newestTime)
  }

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

    const updateUserList = () => {
      const toSend = {
          currentTime : currTime,
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
          Object.keys(response.data["newUserCheckins"]).forEach((id) => {
              const curr = response.data["newUserCheckins"][id]
              console.log("xxx")
          })
      })
          .catch(function (error) {
              console.log(error);
          });
  }
}