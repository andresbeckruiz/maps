import {useState} from "react";
import axios from "axios";

function CheckinItem(props) {

    const [data, setData] = useState("")

    const getUserInfo = () => {
        console.log(props.id)
        setData(props.id)
        let id = props.id
        const toSend = {
            id: id
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        axios.post(
            "http://localhost:4567/pastCheckins",
            toSend,
            config
        )
            .then(response => {
                // response.data["pastCheckins"];
                // if (cache[tile] != {}) {
                //     Object.keys(cache[tile]).forEach((id) => {
                //         const curr = cache[tile][id]
                //         updatedMap.push(curr)
                //     })
                //     canvas = canvasRef.current
                //     contextRef.current = canvas.getContext('2d')
                 //   context = contextRef.current
                    // drawWaysScroll(context, cache[tile], shortestRoute)
            })
            .catch(function (error) {
                console.log(error);
            });
    }


    return (
        <div onClick={getUserInfo} style = {{borderStyle: "none none solid none"}}>
            <span>
                <h5> {props.name} checked into {props.lat} , {props.lon} at {props.time} </h5>
                <h7>{data}</h7>
            </span>
        </div>
    )
    // have return here of component

}
export default CheckinItem
