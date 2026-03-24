package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Company;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
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
@DisplayName("UserMapper Tests")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("ATHLETE");

        testUser = new Athlete();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setBio("Test bio");
        testUser.setProfileImg("http://image.url/profile.jpg");
        testUser.setBannerImg("http://image.url/banner.jpg");
        testUser.setPrivate(false);
        testUser.setCreatedAt(Instant.now());
        testUser.setUpdatedAt(Instant.now());

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);
        testUser.setRole(roles);
    }

    // TO ENTITY TESTS

    @Test
    @DisplayName("Should convert CreateUserRequestDTO to Athlete entity")
    void toEntityAthlete() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO(
                "Test Athlete",
                "athlete123",
                "athlete@test.com",
                "password123",
                "ATHLETE"
        );

        User result = userMapper.toEntity(dto);

        assertNotNull(result);
        assertTrue(result instanceof Athlete);
        assertEquals("athlete123", result.getUsername());
        assertEquals("athlete@test.com", result.getEmail());
        assertEquals("Test Athlete", result.getName());
    }

    @Test
    @DisplayName("Should convert CreateUserRequestDTO to Company entity")
    void toEntityCompany() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO(
                "Test Company",
                "company123",
                "company@test.com",
                "password123",
                "COMPANY"
        );

        User result = userMapper.toEntity(dto);

        assertNotNull(result);
        assertTrue(result instanceof Company);
        assertEquals("company123", result.getUsername());
        assertEquals("company@test.com", result.getEmail());
        assertEquals("Test Company", result.getName());
    }

    @Test
    @DisplayName("Should handle lowercase role name")
    void toEntityLowercaseRole() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO(
                "Test User",
                "testuser",
                "test@test.com",
                "password123",
                "athlete"  // lowercase
        );

        User result = userMapper.toEntity(dto);

        assertNotNull(result);
        assertTrue(result instanceof Athlete);
    }

    @Test
    @DisplayName("Should throw exception for invalid role")
    void toEntityInvalidRole() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO(
                "Test User",
                "testuser",
                "test@test.com",
                "password123",
                "INVALID_ROLE"
        );

        assertThrows(IllegalArgumentException.class, () ->
                userMapper.toEntity(dto)
        );
    }

    // TO USER DTO TESTS

    @Test
    @DisplayName("Should convert User entity to UserDTO")
    void toUserDTO() {
        UserDTO result = userMapper.toUserDTO(testUser);

        assertNotNull(result);
        assertEquals("1", result.id());
        assertEquals("Test User", result.name());
        assertEquals("testuser", result.username());
        assertEquals("ATHLETE", result.role());
        assertEquals("Test bio", result.bio());
        assertEquals("test@example.com", result.email());
        assertEquals("http://image.url/profile.jpg", result.profileImg());
        assertEquals("http://image.url/banner.jpg", result.bannerImg());
        assertFalse(result.isPrivate());
        assertNotNull(result.createdAt());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void toUserDTONullFields() {
        testUser.setBio(null);
        testUser.setProfileImg(null);
        testUser.setBannerImg(null);
        testUser.setUpdatedAt(null);

        UserDTO result = userMapper.toUserDTO(testUser);

        assertNotNull(result);
        assertNull(result.bio());
        assertNull(result.profileImg());
        assertNull(result.bannerImg());
        assertNull(result.updatedAt());
    }

    // TO USER DTO LIST TESTS

    @Test
    @DisplayName("Should convert list of users to list of DTOs")
    void toUserDTOList() {
        User user2 = new Company();
        user2.setId(2L);
        user2.setUsername("company");
        user2.setEmail("company@test.com");
        user2.setName("Company Name");

        Role companyRole = new Role();
        companyRole.setId(2L);
        companyRole.setName("COMPANY");

        Set<Role> roles = new HashSet<>();
        roles.add(companyRole);
        user2.setRole(roles);
        user2.setCreatedAt(Instant.now());

        List<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(user2);

        List<UserDTO> result = userMapper.toUserDTOList(users);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).username());
        assertEquals("company", result.get(1).username());
    }

    @Test
    @DisplayName("Should handle empty list")
    void toUserDTOListEmpty() {
        List<User> users = new ArrayList<>();

        List<UserDTO> result = userMapper.toUserDTOList(users);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TO RESPONSE DTO TESTS

    @Test
    @DisplayName("Should create ResponseDTO with message and user")
    void toResponseDTO() {
        String message = "Usuário criado com sucesso";

        ResponseDTO<UserDTO> result = userMapper.toResponseDTO(message, testUser);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals("testuser", result.data().username());
    }

    // TO SEARCH RESPONSE DTO TESTS

    @Test
    @DisplayName("Should create search ResponseDTO with list of users")
    void toSearchResponseDTO() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        String message = "Busca realizada com sucesso";

        ResponseDTO<List<UserDTO>> result = userMapper.toSearchResponseDTO(message, users);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals(1, result.data().size());
    }

    @Test
    @DisplayName("Should handle empty search results")
    void toSearchResponseDTOEmpty() {
        List<User> users = new ArrayList<>();
        String message = "Nenhum resultado encontrado";

        ResponseDTO<List<UserDTO>> result = userMapper.toSearchResponseDTO(message, users);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertTrue(result.data().isEmpty());
    }
}

