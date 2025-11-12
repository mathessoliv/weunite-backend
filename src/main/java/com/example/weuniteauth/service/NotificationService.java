package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.notification.Notification;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.repository.NotificationRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Notification createNotification(Long userId, String type, Long actorId, Long relatedEntityId, String customMessage) {
        if (userId.equals(actorId)) {
            return null;
        }

        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String message = customMessage != null ? customMessage : generateMessage(type);

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setActorId(actorId);
        notification.setActorName(actor.getName());
        notification.setActorUsername(actor.getUsername());
        notification.setActorProfileImg(actor.getProfileImg());
        notification.setRelatedEntityId(relatedEntityId);
        notification.setMessage(message);
        notification.setIsRead(false);

        Notification saved = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", saved);

        return saved;
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    private String generateMessage(String type) {
        return switch (type) {
            case "POST_LIKE" -> "curtiu sua publicação";
            case "POST_COMMENT" -> "comentou na sua publicação";
            case "COMMENT_LIKE" -> "curtiu seu comentário";
            case "COMMENT_REPLY" -> "respondeu seu comentário";
            case "NEW_FOLLOWER" -> "começou a seguir você";
            case "NEW_MESSAGE" -> "enviou uma mensagem";
            default -> "interagiu com você";
        };
    }
}