function TextBox(props) {
    return (
        <div>
            <label>{props.label}</label>
            <input type="number" onChange={(e) => {
                props.change(e.target.value)
            }} value={props.value}/>
        </div>
    );
}

export default TextBox;