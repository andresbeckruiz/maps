import {useState} from "react";
import axios from "axios";
import PastCheckinsScroll from "./PastCheckinsScroll";
import Maps from "./Maps";

function CheckinItem(props) {

    const [data, setData] = useState([])
    const [pressed, setPressed] = useState(null)

    const getUserInfo = () => {
       // console.log(props.id)
       // setData(props.id)
        let useArray = []
        let id = props.id
        setPressed(1)
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
                console.log(response.data["pastCheckins"]);
                Object.keys(response.data["pastCheckins"]).forEach((id) => {
                    const curr = response.data["pastCheckins"][id]
               //     console.log(curr[0] + ", " + curr[1])
                //    useArray.push(curr[0] + ", " + curr[1])
                    useArray.push(curr[0])
                    useArray.push(", ")
                    useArray.push(curr[1])
                //         updatedMap.push(curr)
                })
            })
            .catch(function (error) {
                console.log(error);
            });
        setData(useArray)
        console.log(useArray)
    }


    return (
        <div onClick={getUserInfo} style = {{borderStyle: "none none solid none"}}>
            <span>
                <h5> {props.name} checked into {props.lat} , {props.lon} at {props.time} </h5>
                {/*<h7>{data}</h7>*/}
            </span>
            {(() => {
                if (pressed != null) {
                    return <PastCheckinsScroll data={data}/>
                }
            })()}
        </div>
    )

}
export default CheckinItem
