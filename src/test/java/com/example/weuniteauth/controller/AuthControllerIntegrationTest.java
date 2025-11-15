package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.auth.LoginRequestDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private ResponseDTO<AuthDTO> sampleResponse() {
        UserDTO user = new UserDTO("1", "Test", "tester", "BASIC", null, "tester@example.com", null, null, false, Instant.now(), Instant.now());
        return new ResponseDTO<>("ok", new AuthDTO(user, "token", 3600L));
    }

    @Test
    void loginShouldReturnToken() throws Exception {
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"tester","password":"Secret#123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jwt").value("token"));
    }

    @Test
    void signupShouldReturnCreatedUser() throws Exception {
        when(authService.signUp(any(CreateUserRequestDTO.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Tester","username":"tester","email":"tester@example.com","password":"Secret#123","role":"athlete"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user.username").value("tester"));
    }
}
