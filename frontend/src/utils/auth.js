"use strict"

import router from "@/router";
import { request } from "./request";


const cookieName = "tokenInfo";


export class UserInfo {
    constructor(username, password) {
        this.username = username;
        this.password = password;
    }
}


export class TokenInfo {
    constructor(access, refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    static fromData(data = {}) {
        return Object.assign(new TokenInfo("", ""), data);
    }

    static fromJson(json){
        return TokenInfo.fromData(JSON.parse(json));
    }
}


export class RefreshState {
    constructor() {
        this.isRefreshing = false;
        this.promises = [];
    }

    addPromise(resolve, reject) {
        this.promises.push([resolve, reject]);
    }

    resolveAll() {
        for (const [resolve, _] of this.promises) {
            resolve();
        }
        this.promises = [];
    }

    rejectAll(err) {
        for (const [_, reject] of this.promises) {
            reject(err);
        }
        this.promises = [];
    }
}

export const refreshState = new RefreshState();


export const storeTokens = (tokenInfo) => {
    document.cookie = `${cookieName}=${encodeURIComponent(JSON.stringify(tokenInfo))}`;
};

export const loadTokens = () => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${cookieName}=`);

    if (parts.length === 2) {
        const encodedJson = parts.pop().split(';').shift();

        return TokenInfo.fromJson(decodeURIComponent(encodedJson));
    } else {
        return null;
    }
}

export const removeTokens = () => {
    document.cookie = cookieName + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
};

export const isAuthenticated = () => {
    return loadTokens() !== null;
}

export const logOut = () => {
    removeTokens();
    router.push("/login");
}

export const registration = async (userInfo) => {
    const res = await request("/auth/registration/", "POST",
        { "Content-Type": "application/json" }, JSON.stringify(userInfo));

    if (res.status === 500) {
        throw new Error(await res.text());
    }

    if (!res.ok) {
        throw new Error(res.statusText);
    }
};

export const login = async (userInfo) => {
    const res = await request("/auth/login/", "POST",
        { "Content-Type": "application/json" }, JSON.stringify(userInfo));

    if (res.status === 500) {
        throw new Error(await res.text());
    }

    if (!res.ok) {
        throw new Error(res.statusText);
    }

    const tokenInfo = TokenInfo.fromData(await res.json());
    storeTokens(tokenInfo);
};

export const refresh = async () => {
    if (refreshState.isRefreshing) {
        await new Promise((resolve, reject) => {
            refreshState.addPromise(resolve, reject);
        });

        return;
    }

    refreshState.isRefreshing = true;
    const res = await request("/auth/refresh/", "POST",
        { "Content-Type": "application/json" }, JSON.stringify(loadTokens()));

    if (!res.ok) {
        refreshState.isRefreshing = false;
        refreshState.rejectAll(new Error(res.statusText));

        throw new Error(res.statusText);
    }

    const tokenInfo = TokenInfo.fromData(await res.json());
    storeTokens(tokenInfo);

    refreshState.isRefreshing = false;
    refreshState.resolveAll();
};