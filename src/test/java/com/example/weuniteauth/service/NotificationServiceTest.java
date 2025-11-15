package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.notification.Notification;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.repository.NotificationRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    void setUp() {
        actor = new User();
        actor.setId(3L);
        actor.setName("Actor");
        actor.setUsername("actor_user");
        actor.setProfileImg("img");
    }

    @Test
    void createNotificationShouldPersistAndBroadcast() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(actor));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(10L);
            notification.setCreatedAt(LocalDateTime.now());
            return notification;
        });

        Notification notification = notificationService.createNotification(5L, "POST_LIKE", 3L, 9L, null);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        assertThat(notificationCaptor.getValue().getUserId()).isEqualTo(5L);
        assertThat(notificationCaptor.getValue().getActorName()).isEqualTo("Actor");

        verify(messagingTemplate).convertAndSend(eq("/topic/user/5/notifications"), any(Notification.class));
        assertThat(notification.getId()).isEqualTo(10L);
    }

    @Test
    void createNotificationShouldReturnNullWhenActorIsTarget() {
        Notification notification = notificationService.createNotification(3L, "POST_LIKE", 3L, 9L, null);

        assertThat(notification).isNull();
        verifyNoInteractions(notificationRepository, messagingTemplate);
    }

    @Test
    void getMethodsShouldDelegateToRepository() {
        Notification notification = buildNotification();
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(notification));
        when(notificationRepository.countByUserIdAndIsReadFalse(5L)).thenReturn(2L);

        assertThat(notificationService.getUserNotifications(5L)).containsExactly(notification);
        assertThat(notificationService.getUnreadCount(5L)).isEqualTo(2L);
    }

    @Test
    void markAsReadShouldUpdateEntity() {
        Notification notification = buildNotification();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L);

        assertThat(notification.getIsRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAllAsReadShouldInvokeRepository() {
        notificationService.markAllAsRead(5L);
        verify(notificationRepository).markAllAsReadByUserId(5L);
    }

    @Test
    void deleteNotificationShouldCallRepository() {
        notificationService.deleteNotification(9L);
        verify(notificationRepository).deleteById(9L);
    }

    private Notification buildNotification() {
        return new Notification(1L, 5L, "POST", 3L, "Actor",
                "actor_user", null, 9L, "message", false, LocalDateTime.now());
    }
}

