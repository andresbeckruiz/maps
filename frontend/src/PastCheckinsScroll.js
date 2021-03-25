import {useState, useEffect} from "react";

function PastCheckinsScroll(props) {

    const [userData, setUserData] = useState(props.data)

    useEffect(() => {
        setUserData(props.data)
    },[props.data])
    if (props != null) {
        return (
            <div style={{overflow: "scroll", borderStyle: "solid", height: 100, width: 700, justifyContent: "center"}}>
                <h6>{props.data}</h6>
            </div>
        )
    }
}
export default PastCheckinsScroll