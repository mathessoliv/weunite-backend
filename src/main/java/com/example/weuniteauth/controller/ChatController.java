package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.chat.MessageDTO;
import com.example.weuniteauth.dto.chat.SendMessageRequestDTO;
import com.example.weuniteauth.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload @Valid SendMessageRequestDTO request) {
        MessageDTO messageDTO = messageService.sendMessage(request);

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + request.conversationId(),
                messageDTO
        );
    }

    @MessageMapping("/chat.markAsRead")
    public void markAsRead(@Payload Long conversationId, @Payload Long userId) {
        messageService.markMessagesAsRead(conversationId, userId);

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId + "/read",
                userId
        );
    }
}
