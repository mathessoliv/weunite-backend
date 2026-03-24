package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.chat.Message;
import com.example.weuniteauth.dto.chat.MessageDTO;
import com.example.weuniteauth.dto.chat.SendMessageRequestDTO;
import com.example.weuniteauth.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatController chatController;

    @Test
    void shouldSendMessage() {
        SendMessageRequestDTO request = new SendMessageRequestDTO(1L, 2L, "hi", Message.MessageType.TEXT);
        MessageDTO messageDTO = new MessageDTO(5L, 1L, 2L, "hi", true, Instant.now(), Instant.now(), Message.MessageType.TEXT, false, false, null);

        when(messageService.sendMessage(any(SendMessageRequestDTO.class))).thenReturn(messageDTO);

        chatController.sendMessage(request);

        verify(messageService).sendMessage(request);
        verify(messagingTemplate).convertAndSend("/topic/conversation/1", messageDTO);
    }

    @Test
    void shouldMarkAsRead() {
        Map<String, Long> payload = Map.of("conversationId", 1L, "userId", 2L);
        chatController.markAsRead(payload);
        verify(messageService).markMessagesAsRead(1L, 2L);
        verify(messagingTemplate).convertAndSend("/topic/conversation/1/read", 2L);
    }

    @Test
    void uploadFileShouldReturnBadRequestWhenEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        ResponseEntity<?> response = chatController.uploadFile(emptyFile, 1L, 2L);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void uploadFileShouldRejectLargeFiles() {
        byte[] large = new byte[(10 * 1024 * 1024) + 1];
        MockMultipartFile largeFile = new MockMultipartFile("file", "large.txt", "text/plain", large);
        ResponseEntity<?> response = chatController.uploadFile(largeFile, 1L, 2L);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void uploadFileShouldHandleIOException() throws IOException {
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(100L);
        lenient().when(file.getOriginalFilename()).thenReturn("error.txt");
        lenient().when(file.getContentType()).thenReturn("text/plain");
        doThrow(new IOException("fail")).when(file).getInputStream();

        ResponseEntity<?> response = chatController.uploadFile(file, 1L, 2L);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }
}
