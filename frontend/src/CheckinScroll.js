import {useState, useEffect} from "react";
import CheckinItem from "./CheckinItem";

function CheckinScroll(props) {

    const [checkIns, setCheckIns] = useState(props.items)

    useEffect(() => {
        setCheckIns(props.items)
    },[props.items])

    return (
          <div style = {{overflow: "scroll", borderStyle: "solid", height: 300, width:700, justifyContent: "center"}}>
            {checkIns.map(item => {
                // console.log("Item" + item["id"])
                //console.log("Item" + item["name"])
                return (
                // <h6> {item["name"]} checked into {item["lat"]} , {item["lon"]} at {item["time"]} </h6>
                <CheckinItem id={item["id"]} name={item["name"]} lat={item["lat"]} lon={item["lon"]}
                             time={item["time"]}/>
                )
            })}
          </div>
    )
}

export default CheckinScroll