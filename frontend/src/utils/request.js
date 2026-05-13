"use strict"


import { loadTokens, refresh } from "./auth";


const SERVER_API_URI = import.meta.env.VITE_API_URI


export const request = async (uri, method, headers, body) => {
    if (method === "GET") {
        return await fetch(SERVER_API_URI + uri, {
            method,
            headers
        });
    }

    return await fetch(SERVER_API_URI + uri, {
        method,
        headers,
        body
    });
}


export const requestAuthorized = async (uri, method, headers, body) => {
    let tokenInfo = loadTokens();
    let authHeaders = { ...headers, "Authorization": `Bearer ${tokenInfo.access}` };

    let res = await request(uri, method, authHeaders, body);
    if (res.status === 401) {
        await refresh();

        tokenInfo = loadTokens();
        authHeaders = { ...headers, "Authorization": `Bearer ${tokenInfo.access}` };
    } else {
        return res;
    }

    return await request(uri, method, authHeaders, body);
}