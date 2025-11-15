package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.notification.Notification;
import com.example.weuniteauth.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private Notification sampleNotification() {
        return new Notification(
                1L,
                2L,
                "POST_COMMENT",
                3L,
                "Actor",
                "actor_user",
                null,
                5L,
                "commented",
                false,
                LocalDateTime.now()
        );
    }

    @Test
    void getUserNotificationsShouldReturnList() throws Exception {
        when(notificationService.getUserNotifications(2L)).thenReturn(List.of(sampleNotification()));

        mockMvc.perform(get("/api/notifications/user/{userId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("POST_COMMENT"));
    }

    @Test
    void markAsReadShouldReturnOk() throws Exception {
        doNothing().when(notificationService).markAsRead(7L);

        mockMvc.perform(put("/api/notifications/{notificationId}/read", 7L))
                .andExpect(status().isOk());
    }

    @Test
    void deleteNotificationShouldReturnOk() throws Exception {
        doNothing().when(notificationService).deleteNotification(8L);

        mockMvc.perform(delete("/api/notifications/{notificationId}", 8L))
                .andExpect(status().isOk());
    }
}

