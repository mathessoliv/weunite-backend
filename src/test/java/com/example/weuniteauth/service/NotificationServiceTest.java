package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.notification.Notification;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.repository.NotificationRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    private User actor;
    private Notification notification;

    @BeforeEach
    void setUp() {
        actor = new User();
        actor.setId(1L);
        actor.setUsername("actor");
        actor.setName("Actor Name");
        actor.setProfileImg("http://image.url/actor.jpg");

        notification = new Notification();
        notification.setId(1L);
        notification.setUserId(2L);
        notification.setType("POST_LIKE");
        notification.setActorId(1L);
        notification.setActorName("Actor Name");
        notification.setActorUsername("actor");
        notification.setActorProfileImg("http://image.url/actor.jpg");
        notification.setRelatedEntityId(10L);
        notification.setMessage("curtiu sua publicação");
        notification.setIsRead(false);
    }

    // CREATE NOTIFICATION TESTS

    @Test
    @DisplayName("Should create notification successfully")
    void createNotificationSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(actor));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(Notification.class));

        Notification result = notificationService.createNotification(2L, "POST_LIKE", 1L, 10L, null);

        assertNotNull(result);
        assertEquals(2L, result.getUserId());
        assertEquals("POST_LIKE", result.getType());
        assertEquals(1L, result.getActorId());
        assertEquals("curtiu sua publicação", result.getMessage());
        assertFalse(result.getIsRead());

        verify(userRepository).findById(1L);
        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/user/2/notifications"), any(Notification.class));
    }

    @Test
    @DisplayName("Should create notification with custom message")
    void createNotificationWithCustomMessage() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(actor));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(Notification.class));

        Notification result = notificationService.createNotification(2L, "CUSTOM", 1L, 10L, "Mensagem customizada");

        assertNotNull(result);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should not create notification when userId equals actorId")
    void createNotificationSameUser() {
        Notification result = notificationService.createNotification(1L, "POST_LIKE", 1L, 10L, null);

        assertNull(result);
        verify(userRepository, never()).findById(anyLong());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should throw exception when actor not found")
    void createNotificationActorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                notificationService.createNotification(2L, "POST_LIKE", 1L, 10L, null)
        );

        verify(userRepository).findById(1L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should generate correct message for POST_LIKE type")
    void generateMessagePostLike() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(actor));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notif = invocation.getArgument(0);
            assertEquals("curtiu sua publicação", notif.getMessage());
            return notif;
        });

        notificationService.createNotification(2L, "POST_LIKE", 1L, 10L, null);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should generate correct message for POST_COMMENT type")
    void generateMessagePostComment() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(actor));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notif = invocation.getArgument(0);
            assertEquals("comentou na sua publicação", notif.getMessage());
            return notif;
        });

        notificationService.createNotification(2L, "POST_COMMENT", 1L, 10L, null);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should generate correct message for NEW_FOLLOWER type")
    void generateMessageNewFollower() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(actor));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notif = invocation.getArgument(0);
            assertEquals("começou a seguir você", notif.getMessage());
            return notif;
        });

        notificationService.createNotification(2L, "NEW_FOLLOWER", 1L, 10L, null);

        verify(notificationRepository).save(any(Notification.class));
    }

    // GET USER NOTIFICATIONS TESTS

    @Test
    @DisplayName("Should get user notifications successfully")
    void getUserNotificationsSuccess() {
        List<Notification> notifications = Arrays.asList(notification);

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(2L)).thenReturn(notifications);

        List<Notification> result = notificationService.getUserNotifications(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getUserId());

        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(2L);
    }

    // GET UNREAD COUNT TESTS

    @Test
    @DisplayName("Should get unread count successfully")
    void getUnreadCountSuccess() {
        when(notificationRepository.countByUserIdAndIsReadFalse(2L)).thenReturn(5L);

        Long count = notificationService.getUnreadCount(2L);

        assertEquals(5L, count);
        verify(notificationRepository).countByUserIdAndIsReadFalse(2L);
    }

    // MARK AS READ TESTS

    @Test
    @DisplayName("Should mark notification as read successfully")
    void markAsReadSuccess() {
        notification.setIsRead(false);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsRead(1L);

        assertTrue(notification.getIsRead());
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should throw exception when notification not found")
    void markAsReadNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                notificationService.markAsRead(999L)
        );

        verify(notificationRepository).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    // MARK ALL AS READ TESTS

    @Test
    @DisplayName("Should mark all notifications as read successfully")
    void markAllAsReadSuccess() {
        doNothing().when(notificationRepository).markAllAsReadByUserId(2L);

        notificationService.markAllAsRead(2L);

        verify(notificationRepository).markAllAsReadByUserId(2L);
    }

    // DELETE NOTIFICATION TESTS

    @Test
    @DisplayName("Should delete notification successfully")
    void deleteNotificationSuccess() {
        doNothing().when(notificationRepository).deleteById(1L);

        notificationService.deleteNotification(1L);

        verify(notificationRepository).deleteById(1L);
    }
}

