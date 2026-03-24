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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService Tests")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;
    private Conversation conversation;
    private Message message;
    private MessageDTO messageDTO;
    private SendMessageRequestDTO sendMessageRequest;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setUsername("sender");
        sender.setEmail("sender@example.com");

        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("receiver");
        receiver.setEmail("receiver@example.com");

        conversation = new Conversation();
        conversation.setId(1L);
        Set<User> participants = new HashSet<>();
        participants.add(sender);
        participants.add(receiver);
        conversation.setParticipants(participants);
        conversation.setCreatedAt(Instant.now());
        conversation.setUpdatedAt(Instant.now());

        message = new Message();
        message.setId(1L);
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent("Hello, World!");
        message.setType(Message.MessageType.TEXT);
        message.setRead(false);
        message.setCreatedAt(Instant.now());

        messageDTO = new MessageDTO(
                1L,
                1L,
                1L,
                "Hello, World!",
                false,
                Instant.now(),
                null,
                Message.MessageType.TEXT,
                false,
                false,
                null
        );

        sendMessageRequest = new SendMessageRequestDTO(
                1L,
                1L,
                "Hello, World!",
                Message.MessageType.TEXT
        );
    }

    @Test
    @DisplayName("Should send message successfully")
    void shouldSendMessageSuccessfully() {
        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toDTO(any(Message.class))).thenReturn(messageDTO);

        // When
        MessageDTO result = messageService.sendMessage(sendMessageRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Hello, World!");
        assertThat(result.senderId()).isEqualTo(1L);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());
        Message capturedMessage = messageCaptor.getValue();

        assertThat(capturedMessage.getContent()).isEqualTo("Hello, World!");
        assertThat(capturedMessage.getSender()).isEqualTo(sender);
        assertThat(capturedMessage.getConversation()).isEqualTo(conversation);
        assertThat(capturedMessage.isRead()).isFalse();

        verify(conversationRepository).save(conversation);
    }

    @Test
    @DisplayName("Should throw exception when conversation not found")
    void shouldThrowExceptionWhenConversationNotFound() {
        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> messageService.sendMessage(sendMessageRequest))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessageContaining("Conversation not found with id: 1");

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should throw exception when sender not found")
    void shouldThrowExceptionWhenSenderNotFound() {
        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> messageService.sendMessage(sendMessageRequest))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessageContaining("User not found with id: 1");

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should throw exception when sender is not a participant")
    void shouldThrowExceptionWhenSenderIsNotParticipant() {
        // Given
        User nonParticipant = new User();
        nonParticipant.setId(3L);
        nonParticipant.setUsername("nonparticipant");

        SendMessageRequestDTO request = new SendMessageRequestDTO(
                1L,
                3L,
                "Hello, World!",
                Message.MessageType.TEXT
        );

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(3L)).thenReturn(Optional.of(nonParticipant));

        // When & Then
        assertThatThrownBy(() -> messageService.sendMessage(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User is not a participant of this conversation");

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should get conversation messages successfully")
    void shouldGetConversationMessagesSuccessfully() {
        // Given
        List<Message> messages = Arrays.asList(message);
        List<MessageDTO> messageDTOs = Arrays.asList(messageDTO);

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(1L)).thenReturn(messages);
        when(messageMapper.toDTOList(messages)).thenReturn(messageDTOs);

        // When
        List<MessageDTO> result = messageService.getConversationMessages(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).content()).isEqualTo("Hello, World!");

        verify(conversationRepository).findById(1L);
        verify(messageRepository).findByConversationIdOrderByCreatedAtAsc(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting messages for non-existent conversation")
    void shouldThrowExceptionWhenGettingMessagesForNonExistentConversation() {
        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> messageService.getConversationMessages(1L, 1L))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessageContaining("Conversation not found with id: 1");

        verify(messageRepository, never()).findByConversationIdOrderByCreatedAtAsc(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when user is not participant while getting messages")
    void shouldThrowExceptionWhenUserIsNotParticipantWhileGettingMessages() {
        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        // When & Then
        assertThatThrownBy(() -> messageService.getConversationMessages(1L, 3L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User is not a participant of this conversation");

        verify(messageRepository, never()).findByConversationIdOrderByCreatedAtAsc(anyLong());
    }

    @Test
    @DisplayName("Should mark messages as read successfully")
    void shouldMarkMessagesAsReadSuccessfully() {
        // Given
        Message unreadMessage1 = new Message();
        unreadMessage1.setId(1L);
        unreadMessage1.setSender(receiver);
        unreadMessage1.setRead(false);

        Message unreadMessage2 = new Message();
        unreadMessage2.setId(2L);
        unreadMessage2.setSender(receiver);
        unreadMessage2.setRead(false);

        List<Message> unreadMessages = Arrays.asList(unreadMessage1, unreadMessage2);

        when(messageRepository.findUnreadMessagesByConversationAndUser(1L, 1L))
                .thenReturn(unreadMessages);

        // When
        messageService.markMessagesAsRead(1L, 1L);

        // Then
        assertThat(unreadMessage1.isRead()).isTrue();
        assertThat(unreadMessage1.getReadAt()).isNotNull();
        assertThat(unreadMessage2.isRead()).isTrue();
        assertThat(unreadMessage2.getReadAt()).isNotNull();

        verify(messageRepository).saveAll(unreadMessages);
    }

    @Test
    @DisplayName("Should handle marking messages as read when no unread messages exist")
    void shouldHandleMarkingMessagesAsReadWhenNoUnreadMessagesExist() {
        // Given
        when(messageRepository.findUnreadMessagesByConversationAndUser(1L, 1L))
                .thenReturn(Collections.emptyList());

        // When
        messageService.markMessagesAsRead(1L, 1L);

        // Then
        verify(messageRepository).saveAll(Collections.emptyList());
    }

    @Test
    @DisplayName("Should send message with IMAGE type")
    void shouldSendMessageWithImageType() {
        // Given
        SendMessageRequestDTO imageRequest = new SendMessageRequestDTO(
                1L,
                1L,
                "https://example.com/image.jpg",
                Message.MessageType.IMAGE
        );

        Message imageMessage = new Message();
        imageMessage.setId(2L);
        imageMessage.setConversation(conversation);
        imageMessage.setSender(sender);
        imageMessage.setContent("https://example.com/image.jpg");
        imageMessage.setType(Message.MessageType.IMAGE);
        imageMessage.setRead(false);

        MessageDTO imageMessageDTO = new MessageDTO(
                2L,
                1L,
                1L,
                "https://example.com/image.jpg",
                false,
                Instant.now(),
                null,
                Message.MessageType.IMAGE,
                false,
                false,
                null
        );

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenReturn(imageMessage);
        when(messageMapper.toDTO(any(Message.class))).thenReturn(imageMessageDTO);

        // When
        MessageDTO result = messageService.sendMessage(imageRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(Message.MessageType.IMAGE);
        assertThat(result.content()).contains("image.jpg");

        verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("Should update conversation updatedAt timestamp when sending message")
    void shouldUpdateConversationUpdatedAtWhenSendingMessage() {
        // Given
        Instant originalUpdatedAt = conversation.getUpdatedAt();

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toDTO(any(Message.class))).thenReturn(messageDTO);

        // When
        messageService.sendMessage(sendMessageRequest);

        // Then
        ArgumentCaptor<Conversation> conversationCaptor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(conversationCaptor.capture());
        Conversation capturedConversation = conversationCaptor.getValue();

        assertThat(capturedConversation.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }
}
