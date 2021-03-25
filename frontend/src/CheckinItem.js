import {useState} from "react";
import axios from "axios";

function CheckinItem(props) {

    const [data, setData] = useState([])

    const getUserInfo = () => {
       // console.log(props.id)
       // setData(props.id)
        let useArray = []
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
                console.log(response.data["pastCheckins"]);
                Object.keys(response.data["pastCheckins"]).forEach((id) => {
                    const curr = response.data["pastCheckins"][id]
               //     console.log(curr[0] + ", " + curr[1])
                    useArray.push(curr[0] + ", " + curr[1])
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
                <h7>{data}</h7>
            </span>
        </div>
    )
    // have return here of component

}
export default CheckinItem
