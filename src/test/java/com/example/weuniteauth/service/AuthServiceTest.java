package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.auth.*;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.exceptions.auth.InvalidTokenException;
import com.example.weuniteauth.exceptions.auth.NotVerifiedEmailException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.AuthMapper;
import com.example.weuniteauth.service.jwt.JwtService;
import com.example.weuniteauth.service.mail.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;


    @Test
    @DisplayName("Should login successfully when credentials are valid and email is verified")
    void loginSuccess() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("testuser", "password123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("$2a$10$encodedPassword");
        mockUser.setEmailVerified(true);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");

        UserDTO expectedUserDTO = new UserDTO(
                "",
                mockUser.getId().toString(),
                mockUser.getName(),
                mockUser.getUsername(),
                mockUser.getBio(),
                mockUser.getEmail(),
                mockUser.getProfileImg(),
                mockUser.getCreatedAt(),
                mockUser.getUpdatedAt()
        );

        AuthDTO expectedResponse = new AuthDTO(
                "Login realizado com sucesso!",
                expectedUserDTO,
                "jwt-token",
                3600L
        );

        when(userService.findUserEntityByUsername("testuser")).thenReturn(mockUser);
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(mockUser)).thenReturn("jwt-token");
        when(jwtService.getDefaultTokenExpirationTime()).thenReturn(3600L);
        when(authMapper.toLoginResponseDTO(
                "Login realizado com sucesso!",
                mockUser,
                "jwt-token",
                3600L
        )).thenReturn(expectedResponse);

        AuthDTO result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("Login realizado com sucesso!", result.message());
        assertEquals("testuser", result.user().username());
        assertEquals("jwt-token", result.jwt());
        assertEquals(3600L, result.expiresIn());

    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void loginWithNonExistentUser() {

        LoginRequestDTO loginRequest = new LoginRequestDTO("nonexistent", "password123");

        when(userService.findUserEntityByUsername("nonexistent"))
                .thenThrow(new UserNotFoundException());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authService.login(loginRequest);
        });

        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when password is incorrect")
    void loginWithWrongPassword() {

        LoginRequestDTO loginRequest = new LoginRequestDTO("testuser", "wrongpassword");

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("$2a$10$encodedPassword");
        mockUser.setEmailVerified(true);

        when(userService.findUserEntityByUsername("testuser")).thenReturn(mockUser);
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedPassword")).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotVerifiedEmailException when email is not verified")
    void loginWithUnverifiedEmail() {

        LoginRequestDTO loginRequest = new LoginRequestDTO("testuser", "password123");

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("$2a$10$encodedPassword");
        mockUser.setEmailVerified(false); // Email não verificado

        when(userService.findUserEntityByUsername("testuser")).thenReturn(mockUser);
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);

        NotVerifiedEmailException exception = assertThrows(NotVerifiedEmailException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Verifique seu email para fazer login", exception.getMessage());
    }


    @Test
    @DisplayName("Should signup a user when the data doesn't already exist")
    void signUpSuccess() {

        CreateUserRequestDTO userRequestDTO = new CreateUserRequestDTO(
                "Luizao",
                "Luizada",
                "lgtgusmao@hotmail.com",
                "123456Cl@"
        );

        User mockUser = new User();
        mockUser.setUsername("Luizada");
        mockUser.setVerificationToken("123456");

        AuthDTO expectedResponse = new AuthDTO(
                "Cadastro concluído! Verifique seu email",
                null,
                null,
                null
        );


        when(userService.createUser(userRequestDTO)).thenReturn(mockUser);
        when(authMapper.toSignUpResponseDTO(anyString(), anyString())).thenReturn(expectedResponse);

        AuthDTO result = authService.signUp(userRequestDTO);

        assertNotNull(result);
        assertEquals("Cadastro concluído! Verifique seu email", result.message());

    }

    @Test
    @DisplayName("Should throw exception when userService.createUser fails")
    void signUpError() {
        CreateUserRequestDTO userRequestDTO = new CreateUserRequestDTO(
                "Luizao",
                "Luizada",
                "lgtgusmao@hotmail.com",
                "123456Cl@"
        );

        when(userService.createUser(userRequestDTO))
                .thenThrow(new RuntimeException("Erro ao criar usuário"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.signUp(userRequestDTO);
        });

        assertEquals("Erro ao criar usuário", exception.getMessage());
    }


    @Test
    @DisplayName("Should verify email when everything is correct")
    void verifyEmailSuccess() {
        String email = "test@example.com";
        String verificationToken = "123456";

        VerifyEmailRequestDTO verifyEmailRequestDTO = new VerifyEmailRequestDTO(verificationToken);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);
        mockUser.setUsername("testuser");
        mockUser.setName("Test User");
        mockUser.setVerificationToken(verificationToken);
        mockUser.setEmailVerified(false);

        UserDTO expectedUserDTO = new UserDTO(
                "",
                mockUser.getId().toString(),
                mockUser.getName(),
                mockUser.getUsername(),
                mockUser.getBio(),
                mockUser.getEmail(),
                mockUser.getProfileImg(),
                mockUser.getCreatedAt(),
                mockUser.getUpdatedAt()
        );

        AuthDTO expectedResponse = new AuthDTO(
                "Email verificado com sucesso!",
                expectedUserDTO,
                "jwt-token",
                3600L
        );

        when(userService.findUserEntityByEmail(email)).thenReturn(mockUser);
        when(userService.verifyUserEmail(mockUser)).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("jwt-token");
        when(jwtService.getDefaultTokenExpirationTime()).thenReturn(3600L);
        when(authMapper.toVerifyEmailResponseDTO(
                "Email verificado com sucesso!",
                mockUser,
                "jwt-token",
                3600L
        )).thenReturn(expectedResponse);

        AuthDTO result = authService.verifyEmail(verifyEmailRequestDTO, email);

        assertNotNull(result);
        assertEquals("Email verificado com sucesso!", result.message());
        assertEquals("testuser", result.user().username());
        assertEquals("jwt-token", result.jwt());
        assertEquals(3600L, result.expiresIn());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found by email")
    void verifyEmailWithUserNotFound() {

        String email = "nonexistent@example.com";
        String verificationToken = "123456";

        VerifyEmailRequestDTO verifyEmailRequestDTO = new VerifyEmailRequestDTO(verificationToken);

        when(userService.findUserEntityByEmail(email))
                .thenThrow(new UserNotFoundException());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authService.verifyEmail(verifyEmailRequestDTO, email);
        });

        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when user has no verification token")
    void verifyEmailWithNullToken() {

        String email = "test@example.com";
        String requestToken = "123456";

        VerifyEmailRequestDTO verifyEmailRequestDTO = new VerifyEmailRequestDTO(requestToken);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);
        mockUser.setUsername("testuser");
        mockUser.setName("Test User");
        mockUser.setVerificationToken(null);
        mockUser.setEmailVerified(false);

        when(userService.findUserEntityByEmail(email)).thenReturn(mockUser);

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> {
            authService.verifyEmail(verifyEmailRequestDTO, email);
        });

        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should send reset password email successfully when user exists and email is verified")
    void sendResetPasswordSuccess() {

        SendResetPasswordRequestDTO requestDTO = new SendResetPasswordRequestDTO("test@example.com");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setName("Test User");
        mockUser.setEmailVerified(true);
        mockUser.setVerificationToken("123456");

        AuthDTO expectedResponse = new AuthDTO(
                "Código enviado!",
                null,
                null,
                null
        );

        when(userService.findUserEntityByEmail("test@example.com")).thenReturn(mockUser);
        when(userService.generateAndSetToken(mockUser)).thenReturn(mockUser);
        when(authMapper.toSendResetPasswordResponseDTO("Código enviado!")).thenReturn(expectedResponse);

        AuthDTO result = authService.sendResetPassword(requestDTO);

        assertNotNull(result);
        assertEquals("Código enviado!", result.message());

        verify(emailService).sendPasswordResetRequestEmail("test@example.com", "123456");
        verify(userService).generateAndSetToken(mockUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void sendResetPasswordWithNonExistentUser() {

        SendResetPasswordRequestDTO requestDTO = new SendResetPasswordRequestDTO("nonexistent@example.com");

        when(userService.findUserEntityByEmail("nonexistent@example.com"))
                .thenThrow(new UserNotFoundException());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authService.sendResetPassword(requestDTO);
        });

        assertNotNull(exception);

        verify(emailService, never()).sendPasswordResetRequestEmail(anyString(), anyString());
        verify(userService, never()).generateAndSetToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw NotVerifiedEmailException when user email is not verified")
    void sendResetPasswordWithUnverifiedEmail() {

        SendResetPasswordRequestDTO requestDTO = new SendResetPasswordRequestDTO("unverified@example.com");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("unverified@example.com");
        mockUser.setUsername("testuser");
        mockUser.setName("Test User");
        mockUser.setEmailVerified(false); // Email não verificado

        when(userService.findUserEntityByEmail("unverified@example.com")).thenReturn(mockUser);

        NotVerifiedEmailException exception = assertThrows(NotVerifiedEmailException.class, () -> {
            authService.sendResetPassword(requestDTO);
        });

        assertEquals("Verifique seu e-mail para redefinir a senha", exception.getMessage());

        verify(userService, never()).generateAndSetToken(any(User.class));
        verify(emailService, never()).sendPasswordResetRequestEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Should verify reset password token successfully when token matches")
    void verifyResetPasswordTokenSuccess() {

        String email = "test@example.com";
        String verificationToken = "123456";
        VerifyResetTokenRequestDTO requestDTO = new VerifyResetTokenRequestDTO(verificationToken);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);
        mockUser.setUsername("testuser");
        mockUser.setName("Test User");
        mockUser.setVerificationToken(verificationToken);

        AuthDTO expectedResponse = new AuthDTO(
                "Código verificado!",
                null,
                null,
                null
        );

        when(userService.findUserEntityByEmail(email)).thenReturn(mockUser);
        when(authMapper.toVerifyResetTokenResponseDTO("Código verificado!")).thenReturn(expectedResponse);

        AuthDTO result = authService.verifyResetPasswordToken(requestDTO, email);

        assertNotNull(result);
        assertEquals("Código verificado!", result.message());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void verifyResetPasswordTokenWithNonExistentUser() {

        String email = "nonexistent@example.com";
        String verificationToken = "123456";
        VerifyResetTokenRequestDTO requestDTO = new VerifyResetTokenRequestDTO(verificationToken);

        when(userService.findUserEntityByEmail(email))
                .thenThrow(new UserNotFoundException());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authService.verifyResetPasswordToken(requestDTO, email);
        });

        assertNotNull(exception);

        verify(authMapper, never()).toVerifyResetTokenResponseDTO(anyString());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token does not match")
    void verifyResetPasswordTokenWithInvalidToken() {
        String email = "test@example.com";
        String correctToken = "123456";
        String wrongToken = "654321";
        VerifyResetTokenRequestDTO requestDTO = new VerifyResetTokenRequestDTO(wrongToken);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);
        mockUser.setUsername("testuser");
        mockUser.setName("Test User");
        mockUser.setVerificationToken(correctToken);

        when(userService.findUserEntityByEmail(email)).thenReturn(mockUser);

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> {
            authService.verifyResetPasswordToken(requestDTO, email);
        });

        assertEquals("Token inválido", exception.getMessage());

        verify(authMapper, never()).toVerifyResetTokenResponseDTO(anyString());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when user has null verification token")
    void verifyResetPasswordTokenWithNullUserToken() {

        String email = "test@example.com";
        String requestToken = "123456";
        VerifyResetTokenRequestDTO requestDTO = new VerifyResetTokenRequestDTO(requestToken);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);
        mockUser.setUsername("testuser");
        mockUser.setName("Test User");
        mockUser.setVerificationToken(null);

        when(userService.findUserEntityByEmail(email)).thenReturn(mockUser);

        assertThrows(NullPointerException.class, () -> {
            authService.verifyResetPasswordToken(requestDTO, email);
        });

        verify(authMapper, never()).toVerifyResetTokenResponseDTO(anyString());
    }

    @Test
    @DisplayName("Should reset password successfully when verification token is valid")
    void resetPasswordSuccess() {

        String verificationToken = "123456";
        String newPassword = "newPassword123@";
        String encodedPassword = "$2a$10$encodedNewPassword";
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(newPassword);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setName("Test User");
        mockUser.setPassword("oldEncodedPassword");
        mockUser.setVerificationToken(verificationToken);

        AuthDTO expectedResponse = new AuthDTO(
                "Senha redefinida!",
                null,
                null,
                null
        );

        when(userService.findUserByVerificationToken(verificationToken)).thenReturn(mockUser);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(authMapper.toResetPasswordResponseDTO("Senha redefinida!")).thenReturn(expectedResponse);

        AuthDTO result = authService.resetPassword(requestDTO, verificationToken);

        assertNotNull(result);
        assertEquals("Senha redefinida!", result.message());

        assertEquals(encodedPassword, mockUser.getPassword());

        assertNull(mockUser.getVerificationToken());
        assertNull(mockUser.getVerificationTokenExpires());

        verify(emailService).sendPasswordResetSuccessEmail("test@example.com");
        verify(passwordEncoder).encode(newPassword);
        verify(authMapper).toResetPasswordResponseDTO("Senha redefinida!");
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when verification token is not found")
    void resetPasswordWithInvalidToken() {

        String invalidToken = "invalid123";
        String newPassword = "newPassword123@";
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(newPassword);

        when(userService.findUserByVerificationToken(invalidToken))
                .thenThrow(new InvalidTokenException());

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> {
            authService.resetPassword(requestDTO, invalidToken);
        });

        assertEquals("Token inválido", exception.getMessage());

        verify(passwordEncoder, never()).encode(anyString());
        verify(emailService, never()).sendPasswordResetSuccessEmail(anyString());
        verify(authMapper, never()).toResetPasswordResponseDTO(anyString());
    }

}