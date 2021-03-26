import {useState, useEffect} from "react";
import CheckinItem from "./CheckinItem";

function PastCheckinsScroll(props) {

    const [userData, setUserData] = useState(props.data)

    useEffect(() => {
        setUserData(props.data)
    },[props.data])

    if (props != null) {
        return (
            <div style={{overflow: "scroll", borderStyle: "solid", height: 200, width: 700, justifyContent: "center"}}>
                <h5>Name: {props.name}, ID: {props.id} </h5>
                {userData.map(item => {
                    return (
                        <h6>{item}</h6>
                    )
                })}
            </div>
        )
    }
}
export default PastCheckinsScroll