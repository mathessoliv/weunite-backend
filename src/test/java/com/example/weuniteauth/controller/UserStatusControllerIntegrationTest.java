package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.chat.UserStatusDTO;
import com.example.weuniteauth.service.UserStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserStatusControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserStatusService userStatusService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void getUserStatusShouldReturnDto() throws Exception {
        UserStatusDTO status = new UserStatusDTO(1L, "ONLINE", LocalDateTime.now());
        when(userStatusService.getUserStatus(1L)).thenReturn(status);

        mockMvc.perform(get("/api/users/{userId}/status", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONLINE"));
    }
}

