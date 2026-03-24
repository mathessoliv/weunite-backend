package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.chat.Message;
import com.example.weuniteauth.dto.chat.ConversationDTO;
import com.example.weuniteauth.dto.chat.CreateConversationRequestDTO;
import com.example.weuniteauth.dto.chat.MessageDTO;
import com.example.weuniteauth.service.ConversationService;
import com.example.weuniteauth.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ConversationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationService conversationService;

    @MockitoBean
    private MessageService messageService;

    @Test
    void createConversationShouldReturnDto() throws Exception {
        ConversationDTO dto = new ConversationDTO(1L, Set.of(1L, 2L), null, Instant.now(), Instant.now(), 0);
        when(conversationService.createConversation(any(CreateConversationRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/conversations/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"initiatorUserId":1,"participantIds":[2]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getConversationMessagesShouldReturnList() throws Exception {
        MessageDTO messageDTO = new MessageDTO(1L, 1L, 1L, "hello", true, Instant.now(), Instant.now(), Message.MessageType.TEXT, false, false, null);
        when(messageService.getConversationMessages(1L, 2L)).thenReturn(List.of(messageDTO));

        mockMvc.perform(get("/api/conversations/{conversationId}/messages/{userId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("hello"));
    }

    @Test
    void markMessagesAsReadShouldReturnNoContent() throws Exception {
        doNothing().when(messageService).markMessagesAsRead(3L, 4L);

        mockMvc.perform(put("/api/conversations/{conversationId}/read/{userId}", 3L, 4L))
                .andExpect(status().isNoContent());
    }
}

