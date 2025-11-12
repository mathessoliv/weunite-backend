package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.chat.Message;
import com.example.weuniteauth.dto.chat.MessageDTO;
import com.example.weuniteauth.dto.chat.SendMessageRequestDTO;
import com.example.weuniteauth.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatController Tests")
class ChatControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatController chatController;

    private SendMessageRequestDTO sendMessageRequest;
    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        sendMessageRequest = new SendMessageRequestDTO(
                1L,
                1L,
                "Hello, World!",
                Message.MessageType.TEXT
        );

        messageDTO = new MessageDTO(
                1L,
                1L,
                1L,
                "Hello, World!",
                false,
                Instant.now(),
                null,
                Message.MessageType.TEXT
        );
    }

    @Test
    @DisplayName("Should send message via WebSocket successfully")
    void shouldSendMessageViaWebSocketSuccessfully() {
        // Given
        when(messageService.sendMessage(any(SendMessageRequestDTO.class))).thenReturn(messageDTO);

        // When
        chatController.sendMessage(sendMessageRequest);

        // Then
        verify(messageService).sendMessage(sendMessageRequest);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MessageDTO> messageCaptor = ArgumentCaptor.forClass(MessageDTO.class);

        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), messageCaptor.capture());

        assertThat(destinationCaptor.getValue()).isEqualTo("/topic/conversation/1");
        assertThat(messageCaptor.getValue()).isEqualTo(messageDTO);
        assertThat(messageCaptor.getValue().content()).isEqualTo("Hello, World!");
    }

    @Test
    @DisplayName("Should send message to correct conversation topic")
    void shouldSendMessageToCorrectConversationTopic() {
        // Given
        SendMessageRequestDTO requestWithDifferentConversation = new SendMessageRequestDTO(
                5L,
                1L,
                "Test message",
                Message.MessageType.TEXT
        );

        MessageDTO differentMessageDTO = new MessageDTO(
                2L,
                5L,
                1L,
                "Test message",
                false,
                Instant.now(),
                null,
                Message.MessageType.TEXT
        );

        when(messageService.sendMessage(any(SendMessageRequestDTO.class))).thenReturn(differentMessageDTO);

        // When
        chatController.sendMessage(requestWithDifferentConversation);

        // Then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);

        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), any(MessageDTO.class));

        assertThat(destinationCaptor.getValue()).isEqualTo("/topic/conversation/5");
    }

    @Test
    @DisplayName("Should mark messages as read via WebSocket successfully")
    void shouldMarkMessagesAsReadViaWebSocketSuccessfully() {
        // Given
        Long conversationId = 1L;
        Long userId = 2L;

        doNothing().when(messageService).markMessagesAsRead(conversationId, userId);

        // When
        chatController.markAsRead(conversationId, userId);

        // Then
        verify(messageService).markMessagesAsRead(conversationId, userId);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), userIdCaptor.capture());

        assertThat(destinationCaptor.getValue()).isEqualTo("/topic/conversation/1/read");
        assertThat(userIdCaptor.getValue()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should send mark as read notification to correct topic")
    void shouldSendMarkAsReadNotificationToCorrectTopic() {
        // Given
        Long conversationId = 10L;
        Long userId = 5L;

        doNothing().when(messageService).markMessagesAsRead(conversationId, userId);

        // When
        chatController.markAsRead(conversationId, userId);

        // Then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);

        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), eq(userId));

        assertThat(destinationCaptor.getValue()).isEqualTo("/topic/conversation/10/read");
    }

    @Test
    @DisplayName("Should handle sending IMAGE type message")
    void shouldHandleSendingImageTypeMessage() {
        // Given
        SendMessageRequestDTO imageRequest = new SendMessageRequestDTO(
                1L,
                1L,
                "https://example.com/image.jpg",
                Message.MessageType.IMAGE
        );

        MessageDTO imageMessageDTO = new MessageDTO(
                2L,
                1L,
                1L,
                "https://example.com/image.jpg",
                false,
                Instant.now(),
                null,
                Message.MessageType.IMAGE
        );

        when(messageService.sendMessage(any(SendMessageRequestDTO.class))).thenReturn(imageMessageDTO);

        // When
        chatController.sendMessage(imageRequest);

        // Then
        verify(messageService).sendMessage(imageRequest);

        ArgumentCaptor<MessageDTO> messageCaptor = ArgumentCaptor.forClass(MessageDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/conversation/1"), messageCaptor.capture());

        assertThat(messageCaptor.getValue().type()).isEqualTo(Message.MessageType.IMAGE);
        assertThat(messageCaptor.getValue().content()).contains("image.jpg");
    }

    @Test
    @DisplayName("Should handle sending FILE type message")
    void shouldHandleSendingFileTypeMessage() {
        // Given
        SendMessageRequestDTO fileRequest = new SendMessageRequestDTO(
                1L,
                1L,
                "https://example.com/document.pdf",
                Message.MessageType.FILE
        );

        MessageDTO fileMessageDTO = new MessageDTO(
                3L,
                1L,
                1L,
                "https://example.com/document.pdf",
                false,
                Instant.now(),
                null,
                Message.MessageType.FILE
        );

        when(messageService.sendMessage(any(SendMessageRequestDTO.class))).thenReturn(fileMessageDTO);

        // When
        chatController.sendMessage(fileRequest);

        // Then
        verify(messageService).sendMessage(fileRequest);

        ArgumentCaptor<MessageDTO> messageCaptor = ArgumentCaptor.forClass(MessageDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/conversation/1"), messageCaptor.capture());

        assertThat(messageCaptor.getValue().type()).isEqualTo(Message.MessageType.FILE);
        assertThat(messageCaptor.getValue().content()).contains("document.pdf");
    }

    @Test
    @DisplayName("Should verify WebSocket message mapping for sendMessage")
    void shouldVerifyWebSocketMessageMappingForSendMessage() {
        // Given
        when(messageService.sendMessage(any(SendMessageRequestDTO.class))).thenReturn(messageDTO);

        // When
        chatController.sendMessage(sendMessageRequest);

        // Then
        verify(messageService, times(1)).sendMessage(sendMessageRequest);
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), any(MessageDTO.class));
    }

    @Test
    @DisplayName("Should verify WebSocket message mapping for markAsRead")
    void shouldVerifyWebSocketMessageMappingForMarkAsRead() {
        // Given
        Long conversationId = 1L;
        Long userId = 2L;

        doNothing().when(messageService).markMessagesAsRead(conversationId, userId);

        // When
        chatController.markAsRead(conversationId, userId);

        // Then
        verify(messageService, times(1)).markMessagesAsRead(conversationId, userId);
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), eq(userId));
    }

    @Test
    @DisplayName("Should propagate exceptions from messageService.sendMessage")
    void shouldPropagateExceptionsFromMessageServiceSendMessage() {
        // Given
        when(messageService.sendMessage(any(SendMessageRequestDTO.class)))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then
        try {
            chatController.sendMessage(sendMessageRequest);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Service error");
        }

        verify(messagingTemplate, never()).convertAndSend(eq("/topic/conversation/1"), any(MessageDTO.class));
    }

    @Test
    @DisplayName("Should propagate exceptions from messageService.markMessagesAsRead")
    void shouldPropagateExceptionsFromMessageServiceMarkMessagesAsRead() {
        // Given
        Long conversationId = 1L;
        Long userId = 2L;

        doThrow(new RuntimeException("Service error"))
                .when(messageService).markMessagesAsRead(conversationId, userId);

        // When & Then
        try {
            chatController.markAsRead(conversationId, userId);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Service error");
        }

        verify(messagingTemplate, never()).convertAndSend(eq("/topic/conversation/1/read"), eq(userId));
    }
}
