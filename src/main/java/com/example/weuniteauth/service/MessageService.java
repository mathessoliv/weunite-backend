package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.chat.Conversation;
import com.example.weuniteauth.domain.chat.Message;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.chat.MessageDTO;
import com.example.weuniteauth.dto.chat.SendMessageRequestDTO;
import com.example.weuniteauth.exceptions.NotFoundResourceException;
import com.example.weuniteauth.exceptions.UnauthorizedException;
import com.example.weuniteauth.mapper.MessageMapper;
import com.example.weuniteauth.repository.ConversationRepository;
import com.example.weuniteauth.repository.MessageRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final NotificationService notificationService;

    @Transactional
    public MessageDTO sendMessage(SendMessageRequestDTO request) {
        Conversation conversation = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new NotFoundResourceException("Conversation not found with id: " + request.conversationId()));

        User sender = userRepository.findById(request.senderId())
                .orElseThrow(() -> new NotFoundResourceException("User not found with id: " + request.senderId()));

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(user -> user.getId().equals(sender.getId()));

        if (!isParticipant) {
            throw new UnauthorizedException("User is not a participant of this conversation");
        }

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.content());
        message.setType(request.type());
        message.setRead(false);

        Message savedMessage = messageRepository.save(message);

        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);

        Long senderId = sender.getId();
        conversation.getParticipants().stream()
                .filter(participant -> !participant.getId().equals(senderId))
                .forEach(recipient -> {
                    notificationService.createNotification(
                            recipient.getId(),
                            "NEW_MESSAGE",
                            senderId,
                            conversation.getId(),
                            null
                    );
                });

        return messageMapper.toDTO(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getConversationMessages(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NotFoundResourceException("Conversation not found with id: " + conversationId));

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!isParticipant) {
            throw new UnauthorizedException("User is not a participant of this conversation");
        }

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        return messageMapper.toDTOList(messages);
    }

    @Transactional
    public void markMessagesAsRead(Long conversationId, Long userId) {
        List<Message> unreadMessages = messageRepository.findUnreadMessagesByConversationAndUser(conversationId, userId);

        for (Message message : unreadMessages) {
            if (!message.isRead()) {
                message.setRead(true);
                message.setReadAt(Instant.now());
            }
        }
        messageRepository.saveAll(unreadMessages);
    }

    @Transactional
    public MessageDTO deleteMessage(Long messageId, Long userId, boolean forEveryone) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundResourceException("Mensagem não encontrada"));

        if (!message.getSender().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para apagar esta mensagem");
        }

        MessageDTO messageDTO = messageMapper.toDTO(message);

        messageRepository.delete(message);

        return messageDTO;
    }

    @Transactional
    public MessageDTO editMessage(Long messageId, Long userId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundResourceException("Mensagem não encontrada"));

        if (!message.getSender().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para editar esta mensagem");
        }

        if (!"TEXT".equals(message.getType().toString())) {
            throw new UnauthorizedException("Apenas mensagens de texto podem ser editadas");
        }

        message.setContent(newContent);
        message.setEdited(true);
        message.setEditedAt(Instant.now());

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toDTO(savedMessage);
    }
}