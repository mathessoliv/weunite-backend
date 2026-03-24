package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.chat.UserStatusDTO;
import com.example.weuniteauth.service.UserStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserStatusControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserStatusService userStatusService;

    @InjectMocks
    private UserStatusController userStatusController;

    @Test
    void shouldUpdateStatusViaWebsocket() {
        UserStatusDTO statusDTO = new UserStatusDTO(1L, "ONLINE", null);

        userStatusController.updateUserStatus(statusDTO);

        verify(userStatusService).updateUserStatus(any(UserStatusDTO.class));
        verify(messagingTemplate).convertAndSend("/topic/user/1/status", statusDTO);
    }

    @Test
    void shouldGetUserStatus() {
        UserStatusDTO statusDTO = new UserStatusDTO(1L, "ONLINE", LocalDateTime.now());
        when(userStatusService.getUserStatus(1L)).thenReturn(statusDTO);

        assertThat(userStatusController.getUserStatus(1L).getBody()).isEqualTo(statusDTO);
    }
}
