import {useState, useEffect} from "react";

/**
 * This component represents the scrollbox that contains the old users data.
 * @param props
 * @returns {JSX.Element}
 */
function PastCheckinsScroll(props) {

    const [userData, setUserData] = useState(props.data)

    useEffect(() => {
        setUserData(props.data)
    },[props.data])

    //only want to display this if user data has been requested by a click
    if (props != null) {
        return (
            <div style={{overflow: "scroll", borderStyle: "solid", height: 200, width: 700,
                justifyContent: "center"}}>
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

export default PastCheckinsScroll;