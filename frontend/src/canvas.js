const HEIGHT = 400;
const WIDTH = 400;

let canvas;

$(document).ready(() => {
    canvas.height = HEIGHT;
    canvas.width = WIDTH;
    ctx = canvas.getContext("2d");
    paintToggle();
});

const paintToggle = () => {
    ctx.lineWidth = 2;
    ctx.fillStyle = "#5c22e7"
    ctx.fillRect(0, 0, canvas.width, canvas.height)

    let xVal = 100;
    let yVal = 100;

    ctx.beginPath();
    ctx.moveTo(xVal, yVal);
    ctx.lineTo(xVal + WIDTH, yVal);
    ctx.lineTo(xVal + WIDTH, yVal + HEIGHT);
    ctx.lineTo(xVal, yVal + HEIGHT);
    ctx.lineTo(xVal, yVal);
    ctx.stroke();
    ctx.fillStyle = "#ff6827"
    ctx.fillRect(4*WIDTH, 4*HEIGHT, WIDTH, HEIGHT)
}