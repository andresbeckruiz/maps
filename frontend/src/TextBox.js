/**
 * This component represents a textbox that we can use on our frontend.
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
function TextBox(props) {
    return (
        <div>
            <label>{props.label}</label>
            <input type="text" onChange={(e) => {
                props.change(e.target.value)
            }} value={props.value}/>
        </div>
    );
}

export default TextBox;