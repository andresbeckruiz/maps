import {useState, useEffect} from "react";
import CheckinItem from "./CheckinItem";

/**
 * This component represents the scrollbox that contains all the CheckinItems.
 * @param props
 * @returns {JSX.Element}
 */
function CheckinScroll(props) {

    const [checkIns, setCheckIns] = useState(props.items)

    useEffect(() => {
        setCheckIns(props.items)
    },[props.items])

    return (
          <div style = {{overflow: "scroll", borderStyle: "solid", height: 500, width:700, justifyContent: "center"}}>
            {checkIns.map(item => {
                return (
                <CheckinItem id={item["id"]} name={item["name"]} lat={item["lat"]} lon={item["lon"]}
                             time={item["time"]}/>
                )
            })}
          </div>
    )
}

export default CheckinScroll