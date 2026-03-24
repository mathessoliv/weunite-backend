package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("AuthMapper Tests")
class AuthMapperTest {

    @Autowired
    private AuthMapper authMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");

        testUser = new Athlete();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setBio("Test bio");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        testUser.setRole(roles);
        testUser.setCreatedAt(Instant.now());
    }

    // TO AUTH DTO TESTS

    @Test
    @DisplayName("Should convert User to AuthDTO with JWT and expiresIn")
    void toAuthDTOComplete() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        Long expiresIn = 3600000L;

        AuthDTO result = authMapper.toAuthDTO(testUser, jwt, expiresIn);

        assertNotNull(result);
        assertNotNull(result.user());
        assertEquals("testuser", result.user().username());
        assertEquals(jwt, result.jwt());
        assertEquals(3600000L, result.expiresIn());
    }

    @Test
    @DisplayName("Should convert User to AuthDTO with null JWT")
    void toAuthDTONullJwt() {
        AuthDTO result = authMapper.toAuthDTO(testUser, null, null);

        assertNotNull(result);
        assertNotNull(result.user());
        assertEquals("testuser", result.user().username());
        assertNull(result.jwt());
        assertNull(result.expiresIn());
    }

    @Test
    @DisplayName("Should include all user fields in AuthDTO")
    void toAuthDTOUserFields() {
        String jwt = "test.jwt.token";
        Long expiresIn = 7200000L;

        AuthDTO result = authMapper.toAuthDTO(testUser, jwt, expiresIn);

        assertNotNull(result);
        assertNotNull(result.user());
        assertEquals("1", result.user().id());
        assertEquals("Test User", result.user().name());
        assertEquals("testuser", result.user().username());
        assertEquals("test@example.com", result.user().email());
        assertEquals("Test bio", result.user().bio());
        assertEquals("ATHLETE", result.user().role());
    }

    // TO RESPONSE DTO TESTS - COMPLETE

    @Test
    @DisplayName("Should create ResponseDTO with message, user, JWT and expiresIn")
    void toResponseDTOComplete() {
        String message = "Login realizado com sucesso";
        String jwt = "test.jwt.token";
        Long expiresIn = 3600000L;

        ResponseDTO<AuthDTO> result = authMapper.toResponseDTO(message, testUser, jwt, expiresIn);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals("testuser", result.data().user().username());
        assertEquals(jwt, result.data().jwt());
        assertEquals(3600000L, result.data().expiresIn());
    }

    // TO RESPONSE DTO TESTS - MESSAGE ONLY

    @Test
    @DisplayName("Should create ResponseDTO with only message")
    void toResponseDTOMessageOnly() {
        String message = "Token de verificação enviado";

        ResponseDTO<AuthDTO> result = authMapper.toResponseDTO(message);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNull(result.data());
    }

    // TO RESPONSE DTO TESTS - USER ONLY

    @Test
    @DisplayName("Should create ResponseDTO with message and user only")
    void toResponseDTOUserOnly() {
        String message = "Usuário criado com sucesso";

        ResponseDTO<AuthDTO> result = authMapper.toResponseDTO(message, testUser);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertNotNull(result.data().user());
        assertEquals("testuser", result.data().user().username());
        assertNull(result.data().jwt());
        assertNull(result.data().expiresIn());
    }

    // EDGE CASE TESTS

    @Test
    @DisplayName("Should handle user with minimal fields")
    void toAuthDTOMinimalUser() {
        User minimalUser = new Athlete();
        minimalUser.setId(2L);
        minimalUser.setUsername("minimal");
        minimalUser.setEmail("minimal@test.com");
        minimalUser.setName("Minimal");

        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        minimalUser.setRole(roles);
        minimalUser.setCreatedAt(Instant.now());

        AuthDTO result = authMapper.toAuthDTO(minimalUser, "jwt", 3600000L);

        assertNotNull(result);
        assertNotNull(result.user());
        assertEquals("minimal", result.user().username());
        assertNull(result.user().bio());
        assertNull(result.user().profileImg());
    }

    @Test
    @DisplayName("Should handle different expiresIn values")
    void toAuthDTODifferentExpiresIn() {
        Long shortExpiry = 1800000L; // 30 minutes
        Long longExpiry = 86400000L; // 24 hours

        AuthDTO result1 = authMapper.toAuthDTO(testUser, "jwt1", shortExpiry);
        AuthDTO result2 = authMapper.toAuthDTO(testUser, "jwt2", longExpiry);

        assertEquals(1800000L, result1.expiresIn());
        assertEquals(86400000L, result2.expiresIn());
    }
}

