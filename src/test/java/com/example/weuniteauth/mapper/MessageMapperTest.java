package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.chat.Conversation;
import com.example.weuniteauth.domain.chat.Message;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.chat.MessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("MessageMapper Tests")
class MessageMapperTest {

    @Autowired
    private MessageMapper messageMapper;

    private Message testMessage;
    private Conversation testConversation;
    private User testSender;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");

        testSender = new Athlete();
        testSender.setId(1L);
        testSender.setUsername("sender");
        testSender.setEmail("sender@test.com");
        testSender.setName("Sender User");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        testSender.setRole(roles);
        testSender.setCreatedAt(Instant.now());

        User receiver = new Athlete();
        receiver.setId(2L);
        receiver.setUsername("receiver");
        receiver.setEmail("receiver@test.com");
        receiver.setName("Receiver User");
        receiver.setRole(new HashSet<>(roles));
        receiver.setCreatedAt(Instant.now());

        testConversation = new Conversation();
        testConversation.setId(1L);
        Set<User> participants = new HashSet<>();
        participants.add(testSender);
        participants.add(receiver);
        testConversation.setParticipants(participants);
        testConversation.setCreatedAt(Instant.now());

        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setConversation(testConversation);
        testMessage.setSender(testSender);
        testMessage.setContent("Hello, how are you?");
        testMessage.setRead(false);
        testMessage.setType(Message.MessageType.TEXT);
        testMessage.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("Should convert Message entity to MessageDTO")
    void toDTO() {
        MessageDTO result = messageMapper.toDTO(testMessage);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.conversationId());
        assertEquals(1L, result.senderId());
        assertEquals("Hello, how are you?", result.content());
        assertFalse(result.isRead());
        assertEquals(Message.MessageType.TEXT, result.type());
        assertNotNull(result.createdAt());
        assertNull(result.readAt());
    }

    @Test
    @DisplayName("Should convert message with read status")
    void toDTOReadMessage() {
        testMessage.setRead(true);
        testMessage.setReadAt(Instant.now());

        MessageDTO result = messageMapper.toDTO(testMessage);

        assertNotNull(result);
        assertTrue(result.isRead());
        assertNotNull(result.readAt());
    }

    @Test
    @DisplayName("Should convert message with different type")
    void toDTODifferentType() {
        testMessage.setType(Message.MessageType.IMAGE);

        MessageDTO result = messageMapper.toDTO(testMessage);

        assertNotNull(result);
        assertEquals(Message.MessageType.IMAGE, result.type());
    }

    @Test
    @DisplayName("Should convert list of messages to list of DTOs")
    void toDTOList() {
        Message message2 = new Message();
        message2.setId(2L);
        message2.setConversation(testConversation);
        message2.setSender(testSender);
        message2.setContent("Second message");
        message2.setRead(false);
        message2.setType(Message.MessageType.TEXT);
        message2.setCreatedAt(Instant.now());

        Message message3 = new Message();
        message3.setId(3L);
        message3.setConversation(testConversation);
        message3.setSender(testSender);
        message3.setContent("Third message");
        message3.setRead(true);
        message3.setType(Message.MessageType.TEXT);
        message3.setCreatedAt(Instant.now());
        message3.setReadAt(Instant.now());

        List<Message> messages = new ArrayList<>();
        messages.add(testMessage);
        messages.add(message2);
        messages.add(message3);

        List<MessageDTO> result = messageMapper.toDTOList(messages);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Hello, how are you?", result.get(0).content());
        assertEquals("Second message", result.get(1).content());
        assertEquals("Third message", result.get(2).content());
        assertFalse(result.get(1).isRead());
        assertTrue(result.get(2).isRead());
    }

    @Test
    @DisplayName("Should handle empty message list")
    void toDTOListEmpty() {
        List<Message> messages = new ArrayList<>();

        List<MessageDTO> result = messageMapper.toDTOList(messages);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should map conversationId correctly")
    void toDTOConversationId() {
        testConversation.setId(123L);

        MessageDTO result = messageMapper.toDTO(testMessage);

        assertNotNull(result);
        assertEquals(123L, result.conversationId());
    }

    @Test
    @DisplayName("Should map senderId correctly")
    void toDTOSenderId() {
        testSender.setId(456L);

        MessageDTO result = messageMapper.toDTO(testMessage);

        assertNotNull(result);
        assertEquals(456L, result.senderId());
    }

    @Test
    @DisplayName("Should handle all message types")
    void toDTOAllTypes() {
        Message.MessageType[] types = Message.MessageType.values();

        for (Message.MessageType type : types) {
            testMessage.setType(type);
            MessageDTO result = messageMapper.toDTO(testMessage);
            assertEquals(type, result.type());
        }
    }
}

