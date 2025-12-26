"use strict"


import { requestAuthorized } from "./request"
import { notifyUpdate } from "./update";


export const storePoints = async (points) => {
    const res = await requestAuthorized("/points/store-points/", "POST",
        { "Content-Type": "application/json" },
        JSON.stringify(points));

    if (res.ok) {
        await notifyUpdate();

        return await res.json();
    } else if (res.status === 401) {
        throw new Error("Unauthorized request");
    } else {
        throw new Error(res.statusText);
    }
};

export const getPoints = async () => {
    const res = await requestAuthorized("/points/get-points/", "GET", { }, "");

    if (res.ok) {
        return await res.json();
    } else if (res.status === 401) {
        throw new Error("Unauthorized request");
    } else {
        throw new Error(res.statusText);
    }
};

export const clearPoints = async () => {
    const res = await requestAuthorized("/points/clear-points/", "DELETE", { }, "");

    if (res.ok) {
        await notifyUpdate();

        return;
    } else if (res.status === 401) {
        throw new Error("Unauthorized request");
    } else {
        throw new Error(res.statusText);
    }
};