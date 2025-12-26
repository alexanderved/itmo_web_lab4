"use strict"

import { logOut } from "./auth";
import { drawWebGLCanvas } from "./canvas3d";
import { storePoints } from "./points";

export let rValsGlobal = new Set();
export const rBase = 70;
export const rMul = 2;

export let addPointsToTable;

export const setRValues = (rVals) => rValsGlobal = rVals;
export const setAddPointsFunc = (f) => addPointsToTable = f;

const maxRValue = () => {
    const absRValues = new Set();
    for (const value of rValsGlobal) {
        absRValues.add(Math.abs(value));
    }

    return Math.max(...absRValues);
}

export const addPointExternal = async (point) => {
    if (rValsGlobal.size === 0) {
        return;
    }

    const rValGlobal = maxRValue();

    if (rValGlobal === 0) {
        return;
    }

    const canvas = document.getElementById("graph");
    const ctx = canvas.getContext("2d");

    const width = canvas.width;
    const height = canvas.height;
    const centerX = width / 2;
    const centerY = height / 2;

    const rect = canvas.getBoundingClientRect();

    const radius = 2;
    const x = point.x * rBase * (rMul / rValGlobal);
    const y = point.y * rBase * (rMul / rValGlobal);
    const r = rValGlobal;

    const rPos = Math.abs(r) * rBase * (rMul / rValGlobal);

    let planeX = x / (rPos / Math.abs(r)) * rValGlobal;
    let planeY = y / (rPos / Math.abs(r)) * rValGlobal;

    console.log(point.x, point.y, x, y, planeX, planeY);

    try {
        const p = await storePoints([{
            x: planeX,
            y: planeY,
            rs: [...rValsGlobal]
        }]);

        drawPoints(p);
        addPointsToTable(p);
        drawWebGLCanvas();
    } catch (e) {
        logOut();
    }

    /* const rPos = Math.abs(r) * rBase * (rMul / rValGlobal);

    let planeX = x / (rPos / Math.abs(r));
    let planeY = (centerY - y) / (rPos / Math.abs(r));

    try {
        const p = await storePoints([{
            x: planeX,
            y: planeY,
            rs: [...rValsGlobal]
        }]);

        drawPoints(p);
        addPointsToTable(p);
    } catch (e) {
        logOut();
    } */
}

const addPoint = async (ev) => {
    if (rValsGlobal.size === 0) {
        return;
    }

    const rValGlobal = maxRValue();

    if (rValGlobal === 0) {
        return;
    }

    const canvas = document.getElementById("graph");
    const ctx = canvas.getContext("2d");

    const width = canvas.width;
    const height = canvas.height;
    const centerX = width / 2;
    const centerY = height / 2;

    const rect = canvas.getBoundingClientRect();

    const radius = 2;
    const x = ev.clientX - rect.left - centerX;
    const y = ev.clientY - rect.top;
    const r = rValGlobal;

    const rPos = Math.abs(r) * rBase * (rMul / rValGlobal);

    let planeX = x / (rPos / Math.abs(r));
    let planeY = (centerY - y) / (rPos / Math.abs(r));

    try {
        const p = await storePoints([{
            x: planeX,
            y: planeY,
            rs: [...rValsGlobal]
        }]);

        drawPoints(p);
        addPointsToTable(p);
    } catch (e) {
        logOut();
    }
}

export const initCanvas = () => {
    const canvas = document.getElementById("graph");
    canvas.addEventListener("click", addPoint);
};

export const drawCanvas = () => {
    const canvas = document.getElementById("graph");
    const ctx = canvas.getContext("2d");

    let rVal = maxRValue();

    const width = canvas.width;
    const height = canvas.height;
    const R = rBase * rMul;
    const centerX = width / 2;
    const centerY = height / 2;

    let r;
    let halfR;
    if (rValsGlobal.size !== 0) {
        r = rVal;
        halfR = (Math.abs(rVal) / 2).toString();

        canvas.classList.remove("disabled-graph");
    } else {
        rVal = 1;
        r = "R";
        halfR = "R/2"

        canvas.classList.add("disabled-graph");
    }

    // Background
    ctx.fillStyle = r === "R" ? "rgb(170, 170, 170)" : "white";
    ctx.fillRect(0, 0, width, height);

    if (rVal === 0) {
        ctx.beginPath();
        ctx.arc(centerX, centerY, 2, 0, 2 * Math.PI);
        ctx.fillStyle = "black";
        ctx.fill();

        ctx.strokeText(halfR, centerX - 12, centerY + 12);

        return;
    }

    // Areas
    const drawAreas = (ri) => {
        const Ri = Math.abs(ri) * rBase * (rMul / rVal);
        const posi = ri >= 0;

        if (!posi) {
            ctx.save();
            ctx.scale(-1, -1);
            ctx.translate(-canvas.width, -canvas.height);
        }
    
        ctx.fillStyle = `hsla(${250 + ri * 30}, 100%, ${60 - ri * 5}%, 0.75)`;

        ctx.beginPath();
        ctx.rect(centerX - Ri, centerY - Ri / 2, Ri, Ri / 2);
        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(centerX - Ri, centerY);
        ctx.lineTo(centerX, centerY);
        ctx.lineTo(centerX, centerY + Ri);
        ctx.closePath();
        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.arc(centerX, centerY, Ri, 3 * Math.PI / 2, 2 * Math.PI, false);
        ctx.lineTo(centerX, centerY);
        ctx.fill();

        if (!posi){
            ctx.restore();
        }
    }

    [...rValsGlobal]
        .filter(v => v < 0)
        .toSorted()
        .toReversed()
        .forEach(drawAreas);

    [...rValsGlobal]
        .filter(v => v > 0)
        .toSorted()
        .toReversed()
        .forEach(drawAreas);


    if (rValsGlobal.size === 0) {
        drawAreas(1);
    }


    // Axes
    ctx.beginPath();
    ctx.moveTo(0, centerY);
    ctx.lineTo(width, centerY);
    ctx.moveTo(centerX, 0);
    ctx.lineTo(centerX, height);
    ctx.strokeStyle = "black";
    ctx.stroke();

    // Labels
    ctx.font = "12px monospace";

    ctx.strokeText(halfR, centerX + R / 2 - 6, centerY - 6);
    ctx.strokeText(r, centerX + R - 6, centerY - 6);

    ctx.strokeText("-" + halfR, centerX - R / 2 - 18, centerY - 6);
    ctx.strokeText("-" + r, centerX - R - 6, centerY - 6);

    ctx.strokeText(halfR, centerX + 6, centerY - R / 2 + 6);
    ctx.strokeText(r, centerX + 6, centerY - R + 6);

    ctx.strokeText("-" + halfR, centerX + 6, centerY + R / 2 + 6);
    ctx.strokeText("-" + r, centerX + 6, centerY + R + 6);

    // Ticks
    ctx.beginPath();

    ctx.moveTo(centerX - R, centerY + 3);
    ctx.lineTo(centerX - R, centerY - 3);
    ctx.moveTo(centerX - R / 2, centerY + 3);
    ctx.lineTo(centerX - R / 2, centerY - 3);
    ctx.moveTo(centerX + R, centerY + 3);
    ctx.lineTo(centerX + R, centerY - 3);
    ctx.moveTo(centerX + R / 2, centerY + 3);
    ctx.lineTo(centerX + R / 2, centerY - 3);

    ctx.moveTo(centerX + 3, centerY - R);
    ctx.lineTo(centerX - 3, centerY - R);
    ctx.moveTo(centerX + 3, centerY - R / 2);
    ctx.lineTo(centerX - 3, centerY - R / 2);
    ctx.moveTo(centerX + 3, centerY + R);
    ctx.lineTo(centerX - 3, centerY + R);
    ctx.moveTo(centerX + 3, centerY + R / 2);
    ctx.lineTo(centerX - 3, centerY + R / 2);

    ctx.strokeStyle = "black";
    ctx.stroke();
};

const drawPoint = (p) => {
    const rVal = maxRValue();
    const radius = 2;

    const canvas = document.getElementById("graph");
    const ctx = canvas.getContext("2d");

    if (p.isHit && new Set([...p.rs]).difference(new Set(rValsGlobal)).size !== 0) {
        return;
    }

    if (!p.isHit && new Set(rValsGlobal).symmetricDifference(new Set([...p.rs])).size !== 0) {
        return;
    }

    const dx = parseFloat(p.x) * Math.sign(rVal);
    const dy = parseFloat(p.y) * Math.sign(rVal);

    const px = canvas.width / 2 + (dx * rBase * rMul / rVal);
    const py = canvas.height / 2 - (dy * rBase * rMul / rVal);

    ctx.beginPath();
    ctx.arc(px, py, radius + 1, 0, 2 * Math.PI);
    ctx.fillStyle = "black";
    ctx.fill();

    ctx.beginPath();
    ctx.arc(px, py, radius, 0, 2 * Math.PI);
    ctx.fillStyle = p.isHit ? "rgb(0, 150, 0)" : "red";
    ctx.fill();
};

export const drawPoints = (data) => {
    for (const p of data) {
        drawPoint(p);
    }
};