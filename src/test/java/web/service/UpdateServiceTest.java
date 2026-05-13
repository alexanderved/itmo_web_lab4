package web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.container.AsyncResponse;

@ExtendWith(MockitoExtension.class)
class UpdateServiceTest {
    private UpdateService updateService;

    @Mock
    private AsyncResponse asyncResponse1;
    @Mock
    private AsyncResponse asyncResponse2;
    @Mock
    private AsyncResponse asyncResponse3;

    @BeforeEach
    void setUp() {
        updateService = new UpdateService();
    }

    @Test
    void testWaitUpdateNewUser() {
        assertNull(updateService.waiters.get("user1"));

        updateService.waitUpdate("user1", asyncResponse1);
        
        Queue<AsyncResponse> queue = updateService.waiters.get("user1");
        assertNotNull(queue);
        assertEquals(1, queue.size());
        assertTrue(queue.contains(asyncResponse1));
    }

    @Test
    void testWaitUpdateMultipleUsers() {
        updateService.waitUpdate("user1", asyncResponse1);
        updateService.waitUpdate("user1", asyncResponse2);
        updateService.waitUpdate("user2", asyncResponse3);

        Queue<AsyncResponse> queue = updateService.waiters.get("user1");
        assertEquals(2, queue.size());
        assertEquals(asyncResponse1, queue.poll());
        assertEquals(asyncResponse2, queue.poll());

        queue = updateService.waiters.get("user2");
        assertEquals(1, queue.size());
        assertEquals(asyncResponse3, queue.poll());
    }

    @Test
    void testNotifyUpdateResume() {
        updateService.waitUpdate("user1", asyncResponse1);
        updateService.waitUpdate("user2", asyncResponse2);
        updateService.waitUpdate("user3", asyncResponse3);

        updateService.notifyUpdate("user1");

        verify(asyncResponse1, never()).resume("");
        verify(asyncResponse2).resume("");
        verify(asyncResponse3).resume("");

        assertTrue(updateService.waiters.get("user2").isEmpty());
        assertTrue(updateService.waiters.get("user3").isEmpty());
    }

    @Test
    void testNotifyUpdateCancelledRemoved() {
        when(asyncResponse1.isCancelled()).thenReturn(true);

        updateService.waitUpdate("user1", asyncResponse1);
        updateService.notifyUpdate("user1");

        Queue<AsyncResponse> queue = updateService.waiters.get("user1");
        assertEquals(0, queue.size());
        verify(asyncResponse1, never()).resume("");
    }
}
