package com.example.weuniteauth.dto.chat;

import com.example.weuniteauth.domain.chat.Message;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ChatDtoTest {

    @Test
    void conversationDtoShouldExposeData() {
        MessageDTO lastMessage = new MessageDTO(2L, 1L, 10L, "Hi", true, Instant.now(), Instant.now(), Message.MessageType.TEXT);
        ConversationDTO dto = new ConversationDTO(1L, Set.of(1L, 2L), lastMessage, Instant.MIN, Instant.MAX, 3);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.participantIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(dto.lastMessage()).isEqualTo(lastMessage);
        assertThat(dto.unreadCount()).isEqualTo(3);
    }

    @Test
    void userStatusDtoShouldSupportMutations() {
        UserStatusDTO statusDTO = new UserStatusDTO();
        statusDTO.setUserId(10L);
        statusDTO.setStatus("ONLINE");
        LocalDateTime now = LocalDateTime.now();
        statusDTO.setTimestamp(now);

        assertThat(statusDTO.getUserId()).isEqualTo(10L);
        assertThat(statusDTO.getStatus()).isEqualTo("ONLINE");
        assertThat(statusDTO.getTimestamp()).isEqualTo(now);
    }

    @Test
    void sendMessageRequestShouldExposeFields() {
        SendMessageRequestDTO request = new SendMessageRequestDTO(1L, 2L, "payload", Message.MessageType.FILE);
        assertThat(request.conversationId()).isEqualTo(1L);
        assertThat(request.senderId()).isEqualTo(2L);
        assertThat(request.content()).isEqualTo("payload");
        assertThat(request.type()).isEqualTo(Message.MessageType.FILE);
    }
}
