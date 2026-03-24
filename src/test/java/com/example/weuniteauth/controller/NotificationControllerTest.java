package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.notification.Notification;
import com.example.weuniteauth.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void shouldReturnNotifications() {
        Notification notification = new Notification(1L, 2L, "POST", 3L,
                "Actor", "actor", null, 4L, "msg", false, LocalDateTime.now());
        when(notificationService.getUserNotifications(2L)).thenReturn(List.of(notification));

        ResponseEntity<List<Notification>> response = notificationController.getUserNotifications(2L);
        assertThat(response.getBody()).containsExactly(notification);
    }

    @Test
    void shouldReturnUnreadCount() {
        when(notificationService.getUnreadCount(2L)).thenReturn(5L);

        ResponseEntity<Map<String, Long>> response = notificationController.getUnreadCount(2L);
        assertThat(response.getBody()).containsEntry("unreadCount", 5L);
    }

    @Test
    void shouldMarkAndDelete() {
        doNothing().when(notificationService).markAsRead(anyLong());
        doNothing().when(notificationService).markAllAsRead(anyLong());
        doNothing().when(notificationService).deleteNotification(anyLong());

        assertThat(notificationController.markAsRead(1L).getStatusCodeValue()).isEqualTo(200);
        assertThat(notificationController.markAllAsRead(2L).getStatusCodeValue()).isEqualTo(200);
        assertThat(notificationController.deleteNotification(3L).getStatusCodeValue()).isEqualTo(200);
    }
}

