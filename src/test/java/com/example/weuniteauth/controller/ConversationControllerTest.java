package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.chat.Message;
import com.example.weuniteauth.dto.chat.ConversationDTO;
import com.example.weuniteauth.dto.chat.CreateConversationRequestDTO;
import com.example.weuniteauth.dto.chat.MessageDTO;
import com.example.weuniteauth.service.ConversationService;
import com.example.weuniteauth.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConversationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ConversationController REST Tests")
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private MessageService messageService;

    private ConversationDTO conversationDTO;
    private MessageDTO messageDTO;
    private CreateConversationRequestDTO createRequest;

    @BeforeEach
    void setUp() {
        conversationDTO = new ConversationDTO(
                1L,
                Set.of(1L, 2L),
                null,
                Instant.now(),
                Instant.now(),
                0
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

        createRequest = new CreateConversationRequestDTO(
                1L,
                Set.of(2L)
        );
    }

    @Test
    @DisplayName("Should create conversation successfully")
    void shouldCreateConversationSuccessfully() throws Exception {
        // Given
        when(conversationService.createConversation(any(CreateConversationRequestDTO.class)))
                .thenReturn(conversationDTO);

        // When & Then
        mockMvc.perform(post("/api/conversations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.participantIds").isArray());

        verify(conversationService).createConversation(any(CreateConversationRequestDTO.class));
    }

    @Test
    @DisplayName("Should get user conversations successfully")
    void shouldGetUserConversationsSuccessfully() throws Exception {
        // Given
        List<ConversationDTO> conversations = Arrays.asList(conversationDTO);
        when(conversationService.getUserConversations(1L)).thenReturn(conversations);

        // When & Then
        mockMvc.perform(get("/api/conversations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(conversationService).getUserConversations(1L);
    }

    @Test
    @DisplayName("Should get conversation by id successfully")
    void shouldGetConversationByIdSuccessfully() throws Exception {
        // Given
        when(conversationService.getConversationById(1L, 1L)).thenReturn(conversationDTO);

        // When & Then
        mockMvc.perform(get("/api/conversations/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.participantIds").isArray());

        verify(conversationService).getConversationById(1L, 1L);
    }

    @Test
    @DisplayName("Should get conversation messages successfully")
    void shouldGetConversationMessagesSuccessfully() throws Exception {
        // Given
        List<MessageDTO> messages = Arrays.asList(messageDTO);
        when(messageService.getConversationMessages(1L, 1L)).thenReturn(messages);

        // When & Then
        mockMvc.perform(get("/api/conversations/1/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Hello, World!"));

        verify(messageService).getConversationMessages(1L, 1L);
    }

    @Test
    @DisplayName("Should mark messages as read successfully")
    void shouldMarkMessagesAsReadSuccessfully() throws Exception {
        // Given
        doNothing().when(messageService).markMessagesAsRead(1L, 1L);

        // When & Then
        mockMvc.perform(put("/api/conversations/1/read/1"))
                .andExpect(status().isNoContent());

        verify(messageService).markMessagesAsRead(1L, 1L);
    }

    @Test
    @DisplayName("Should return empty list when user has no conversations")
    void shouldReturnEmptyListWhenUserHasNoConversations() throws Exception {
        // Given
        when(conversationService.getUserConversations(1L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/conversations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(conversationService).getUserConversations(1L);
    }

    @Test
    @DisplayName("Should return empty list when conversation has no messages")
    void shouldReturnEmptyListWhenConversationHasNoMessages() throws Exception {
        // Given
        when(messageService.getConversationMessages(1L, 1L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/conversations/1/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(messageService).getConversationMessages(1L, 1L);
    }

    @Test
    @DisplayName("Should validate create conversation request - null initiator")
    void shouldValidateCreateConversationRequestNullInitiator() throws Exception {
        // Given
        CreateConversationRequestDTO invalidRequest = new CreateConversationRequestDTO(
                null,
                Set.of(2L)
        );

        // When & Then
        mockMvc.perform(post("/api/conversations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(conversationService, never()).createConversation(any(CreateConversationRequestDTO.class));
    }

    @Test
    @DisplayName("Should validate create conversation request - empty participants")
    void shouldValidateCreateConversationRequestEmptyParticipants() throws Exception {
        // Given
        CreateConversationRequestDTO invalidRequest = new CreateConversationRequestDTO(
                1L,
                Collections.emptySet()
        );

        // When & Then
        mockMvc.perform(post("/api/conversations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(conversationService, never()).createConversation(any(CreateConversationRequestDTO.class));
    }

    @Test
    @DisplayName("Should handle multiple participants in create conversation")
    void shouldHandleMultipleParticipantsInCreateConversation() throws Exception {
        // Given
        CreateConversationRequestDTO multiParticipantRequest = new CreateConversationRequestDTO(
                1L,
                Set.of(2L, 3L, 4L)
        );

        ConversationDTO multiParticipantConversation = new ConversationDTO(
                1L,
                Set.of(1L, 2L, 3L, 4L),
                null,
                Instant.now(),
                Instant.now(),
                0
        );

        when(conversationService.createConversation(any(CreateConversationRequestDTO.class)))
                .thenReturn(multiParticipantConversation);

        // When & Then
        mockMvc.perform(post("/api/conversations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(multiParticipantRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.participantIds").isArray());

        verify(conversationService).createConversation(any(CreateConversationRequestDTO.class));
    }

    @Test
    @DisplayName("Should get conversation with unread count")
    void shouldGetConversationWithUnreadCount() throws Exception {
        // Given
        ConversationDTO conversationWithUnread = new ConversationDTO(
                1L,
                Set.of(1L, 2L),
                null,
                Instant.now(),
                Instant.now(),
                5
        );

        when(conversationService.getConversationById(1L, 1L)).thenReturn(conversationWithUnread);

        // When & Then
        mockMvc.perform(get("/api/conversations/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.unreadCount").value(5));

        verify(conversationService).getConversationById(1L, 1L);
    }

    @Test
    @DisplayName("Should get conversation with last message")
    void shouldGetConversationWithLastMessage() throws Exception {
        // Given
        MessageDTO lastMessage = new MessageDTO(
                10L,
                1L,
                2L,
                "Last message content",
                true,
                Instant.now(),
                Instant.now(),
                Message.MessageType.TEXT
        );

        ConversationDTO conversationWithLastMessage = new ConversationDTO(
                1L,
                Set.of(1L, 2L),
                lastMessage,
                Instant.now(),
                Instant.now(),
                0
        );

        when(conversationService.getConversationById(1L, 1L)).thenReturn(conversationWithLastMessage);

        // When & Then
        mockMvc.perform(get("/api/conversations/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastMessage.id").value(10L))
                .andExpect(jsonPath("$.lastMessage.content").value("Last message content"));

        verify(conversationService).getConversationById(1L, 1L);
    }

    @Test
    @DisplayName("Should handle different message types in conversation messages")
    void shouldHandleDifferentMessageTypesInConversationMessages() throws Exception {
        // Given
        MessageDTO textMessage = new MessageDTO(
                1L, 1L, 1L, "Text", false, Instant.now(), null, Message.MessageType.TEXT
        );
        MessageDTO imageMessage = new MessageDTO(
                2L, 1L, 2L, "https://example.com/image.jpg", false, Instant.now(), null, Message.MessageType.IMAGE
        );
        MessageDTO fileMessage = new MessageDTO(
                3L, 1L, 1L, "https://example.com/file.pdf", false, Instant.now(), null, Message.MessageType.FILE
        );

        List<MessageDTO> messages = Arrays.asList(textMessage, imageMessage, fileMessage);
        when(messageService.getConversationMessages(1L, 1L)).thenReturn(messages);

        // When & Then
        mockMvc.perform(get("/api/conversations/1/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].type").value("TEXT"))
                .andExpect(jsonPath("$[1].type").value("IMAGE"))
                .andExpect(jsonPath("$[2].type").value("FILE"));

        verify(messageService).getConversationMessages(1L, 1L);
    }
}
