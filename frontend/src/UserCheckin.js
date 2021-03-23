import axios from "axios";
import {useState} from "react/cjs/react.production.min";

function UserCheckin() {
    const [currTime, setCurrTime] = useState(0)

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