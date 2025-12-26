import { requestAuthorized } from "./request";

export async function waitUpdate(action) {
    let response = await requestAuthorized("/update/wait", "GET", { }, null);

    if (response.status == 502) {
        await waitUpdate(action);
    } else if (response.status != 200) {
        await new Promise(resolve => setTimeout(resolve, 1000));
        console.log("repeat");
        await waitUpdate(action);
    } else {
        await action();

        await waitUpdate(action);
    }
}

export async function notifyUpdate() {
    await requestAuthorized("/update/notify", "POST", { }, "");
}