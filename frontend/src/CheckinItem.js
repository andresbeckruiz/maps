import {useState} from "react";

function CheckinItem(props) {

    const [data, setData] = useState("")

    const getUserInfo = () => {
        console.log(props.id)
        setData(props.id)
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
