package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.auth.LoginRequestDTO;
import com.example.weuniteauth.dto.auth.ResetPasswordRequestDTO;
import com.example.weuniteauth.dto.auth.SendResetPasswordRequestDTO;
import com.example.weuniteauth.dto.auth.VerifyEmailRequestDTO;
import com.example.weuniteauth.dto.auth.VerifyResetTokenRequestDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ResponseDTO<AuthDTO> response;

    @BeforeEach
    void setUp() {
        UserDTO user = new UserDTO("1", "Test", "tester", "BASIC", null, "tester@example.com",
                null, null, false, Instant.now(), Instant.now());
        response = new ResponseDTO<>("ok", new AuthDTO(user, "token", 3600L));
    }

    @Test
    void shouldSignupUser() {
        when(authService.signUp(any(CreateUserRequestDTO.class))).thenReturn(response);
        ResponseEntity<ResponseDTO<AuthDTO>> entity = authController.signup(createUser());
        assertThat(entity.getStatusCodeValue()).isEqualTo(201);
        assertThat(entity.getBody()).isEqualTo(response);
    }

    @Test
    void shouldSignupCompany() {
        when(authService.signUp(any(CreateUserRequestDTO.class))).thenReturn(response);
        assertThat(authController.signupCompany(createUser()).getBody()).isEqualTo(response);
    }

    @Test
    void shouldLogin() {
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);
        assertThat(authController.login(new LoginRequestDTO("tester", "Secret#123")).getBody()).isEqualTo(response);
    }

    @Test
    void shouldVerifyEmail() {
        when(authService.verifyEmail(any(VerifyEmailRequestDTO.class), any(String.class))).thenReturn(response);
        assertThat(authController.verifyEmail(new VerifyEmailRequestDTO("123456"), "email@test.com").getBody()).isEqualTo(response);
    }

    @Test
    void shouldSendResetPassword() {
        when(authService.sendResetPassword(any(SendResetPasswordRequestDTO.class))).thenReturn(response);
        assertThat(authController.sendResetPassword(new SendResetPasswordRequestDTO("email@test.com")).getBody()).isEqualTo(response);
    }

    @Test
    void shouldVerifyResetToken() {
        when(authService.verifyResetPasswordToken(any(VerifyResetTokenRequestDTO.class), any(String.class))).thenReturn(response);
        assertThat(authController.verifyResetToken(new VerifyResetTokenRequestDTO("123456"), "email@test.com").getBody()).isEqualTo(response);
    }

    @Test
    void shouldResetPassword() {
        when(authService.resetPassword(any(ResetPasswordRequestDTO.class), any(String.class))).thenReturn(response);
        assertThat(authController.resetPassword(new ResetPasswordRequestDTO("Secret#123"), "token").getBody()).isEqualTo(response);
    }

    private CreateUserRequestDTO createUser() {
        return new CreateUserRequestDTO("Tester", "tester", "tester@example.com", "Secret#123", "ATHLETE");
    }
}
