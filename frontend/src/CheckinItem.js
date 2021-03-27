import {useState} from "react";
import axios from "axios";
import PastCheckinsScroll from "./PastCheckinsScroll";

/**
 * This component represents an individual user checkin.
 * @param props
 * @returns {JSX.Element}
 */
function CheckinItem(props) {

    const [data, setData] = useState([])
    const [pressed, setPressed] = useState(null)

    /**
     * This method gets the user info of past checkins using a post request. It gets called
     * when a user clicks the text of this component.
     */
    const getUserInfo = () => {
        //array used to add long and lat data
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
                Object.keys(response.data["pastCheckins"]).forEach((id) => {
                    const curr = response.data["pastCheckins"][id]
                    //parsing string here to display on front end
                    let string1 = `${curr[0]}`
                    let string2 = `${curr[1]}`
                    let combinedString = string1.concat(", ",string2)
                    useArray.push(combinedString)
                })
            })
            .catch(function (error) {
                console.log(error);
            });
        setData(useArray)
    }


    return (
        <div onClick={getUserInfo} style = {{borderStyle: "none none solid none"}}>
            <span>
                <h5> {props.name} checked into {props.lat} , {props.lon} at {props.time} </h5>
            </span>
            {(() => {
                if (pressed != null) {
                    return <PastCheckinsScroll name={props.name} id={props.id} data={data}/>
                }
            })()}
        </div>
    )

}
export default CheckinItem
