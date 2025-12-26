package web.service;

import jakarta.ejb.Singleton;
import jakarta.ws.rs.container.AsyncResponse;

import java.util.*;

@Singleton
public class UpdateService {
    final Map<String, Queue<AsyncResponse>> waiters = new HashMap<>();

    public void waitUpdate(String username, AsyncResponse res) {
        waiters.computeIfAbsent(username, k -> new ArrayDeque<>()).offer(res);
    }

    public void notifyUpdate(String username) {
        Set<String> usernames = waiters.keySet();
        for (String un : usernames) {
            var queue = waiters.get(un);
            if (!un.equals(username)) {
                while (!queue.isEmpty()) {
                    queue.poll().resume("");
                }
            } else {
                queue.removeIf(AsyncResponse::isCancelled);
            }
        }
    }
}
