import TextBox from "./TextBox";
import './App.css';
import React, {useState} from 'react';
import axios from "axios";

function App() {
    const [startLat, setStartLat] = useState(0);
    const [route, setRoute] = useState("");
    const requestRoute = () => {
        const toSend = {
            srclat : startLat,
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        axios.post(
            "http://localhost:4567/route",
            toSend,
            config
        )
            .then(response => {
                console.log(response.data);
                setRoute(response.data["route"])
            })

            .catch(function (error) {
                console.log(error);

            });
    }
    return (
        <div className="App">
            <h1> Hello world! </h1>
            <TextBox label={"Source latitude"} change={setStartLat} value={startLat}/>
            <button onClick={requestRoute}>submit</button>
            <h2>Route: {route}</h2>
        </div>
      );
    }

export default App;
