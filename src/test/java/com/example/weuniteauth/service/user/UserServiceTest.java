package com.example.weuniteauth.service.user;

import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.user.UpdateUserRequestDTO;
import com.example.weuniteauth.exceptions.NotFoundResourceException;
import com.example.weuniteauth.exceptions.user.UserAlreadyExistsException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.UserMapper;
import com.example.weuniteauth.repository.RoleRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import com.example.weuniteauth.service.UserService;
import com.example.weuniteauth.service.cloudinary.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("BASIC");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPassword("encodedPassword");
        testUser.setRole(new HashSet<>(Set.of(testRole)));
        testUser.setEmailVerified(true);
        testUser.setCreatedAt(Instant.now());

        userDTO = new UserDTO(
                "1",
                "Test User",
                "testuser",
                "BASIC",
                null,
                "test@example.com",
                null,
                null,
                false,
                Instant.now(),
                null,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
    }

    // CREATE USER TESTS

    @Test
    @DisplayName("Should create user successfully when data is valid")
    void createUserSuccess() {
        CreateUserRequestDTO createUserRequest = new CreateUserRequestDTO(
                "Test User",      // name
                "testuser",       // username
                "test@example.com",
                "password123",
                "BASIC"
        );

        when(userRepository.existsByUsernameOrEmail("testuser", "test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("BASIC")).thenReturn(testRole);
        when(userMapper.toEntity(any(CreateUserRequestDTO.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(createUserRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNotNull(result.getVerificationToken());

        verify(userRepository).existsByUsernameOrEmail("testuser", "test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(roleRepository).findByName("BASIC");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when username or email already exists")
    void createUserWithExistingUsernameOrEmail() {
        CreateUserRequestDTO createUserRequest = new CreateUserRequestDTO(
                "Test User",
                "testuser",
                "test@example.com",
                "password123",
                "BASIC"
        );

        when(userRepository.existsByUsernameOrEmail("testuser", "test@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.createUser(createUserRequest)
        );

        verify(userRepository).existsByUsernameOrEmail("testuser", "test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw NotFoundResourceException when role does not exist")
    void createUserWithInvalidRole() {
        CreateUserRequestDTO createUserRequest = new CreateUserRequestDTO(
                "Test User",
                "testuser",
                "test@example.com",
                "password123",
                "INVALID"
        );

        when(userRepository.existsByUsernameOrEmail("testuser", "test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("INVALID")).thenReturn(null);
        when(userMapper.toEntity(any(CreateUserRequestDTO.class))).thenReturn(testUser);

        assertThrows(NotFoundResourceException.class, () ->
                userService.createUser(createUserRequest)
        );

        verify(roleRepository).findByName("INVALID");
        verify(userRepository, never()).save(any(User.class));
    }

    // GET USER TESTS

    @Test
    @DisplayName("Should get user by id successfully")
    void getUserByIdSuccess() {
        ResponseDTO<UserDTO> expectedResponse = new ResponseDTO<>("Usuário encontrado com sucesso", userDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponseDTO(eq("Usuário encontrado com sucesso"), any(User.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<UserDTO> result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals("Usuário encontrado com sucesso", result.message());

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user id does not exist")
    void getUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.getUser(999L)
        );

        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void getUserByUsernameSuccess() {
        ResponseDTO<UserDTO> expectedResponse = new ResponseDTO<>("Usuário encontrado com sucesso", userDTO);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userMapper.toResponseDTO(eq("Usuário encontrado com sucesso"), any(User.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<UserDTO> result = userService.getUser("testuser");

        assertNotNull(result);
        assertEquals("Usuário encontrado com sucesso", result.message());

        verify(userRepository).findByUsername("testuser");
    }

    // UPDATE USER TESTS

    @Test
    @DisplayName("Should update user successfully")
    void updateUserSuccess() {
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO(
                "Updated Name",
                "updateduser",
                "Updated bio",
                true,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );

        ResponseDTO<UserDTO> expectedResponse = new ResponseDTO<>("Usuário atualizado com sucesso!", userDTO);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponseDTO(eq("Usuário atualizado com sucesso!"), any(User.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<UserDTO> result = userService.updateUser(updateRequest, "testuser", null, null);

        assertNotNull(result);
        assertEquals("Usuário atualizado com sucesso!", result.message());

        verify(userRepository).findByUsername("testuser");
        verify(userRepository).save(testUser);
    }

    // DELETE USER TESTS

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUserSuccess() {
        ResponseDTO<UserDTO> expectedResponse = new ResponseDTO<>("Usuário deletado com sucesso", userDTO);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);
        when(userMapper.toResponseDTO(eq("Usuário deletado com sucesso"), any(User.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<UserDTO> result = userService.deleteUser("testuser");

        assertNotNull(result);
        assertEquals("Usuário deletado com sucesso", result.message());

        verify(userRepository).findByUsername("testuser");
        verify(userRepository).delete(testUser);
    }

    // DELETE BANNER TESTS

    @Test
    @DisplayName("Should delete banner successfully")
    void deleteBannerSuccess() {
        testUser.setBannerImg("http://image.url/banner.jpg");

        ResponseDTO<UserDTO> expectedResponse = new ResponseDTO<>("Banner deletado com sucesso", userDTO);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponseDTO(eq("Banner deletado com sucesso"), any(User.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<UserDTO> result = userService.deleteBanner("testuser");

        assertNotNull(result);
        assertEquals("Banner deletado com sucesso", result.message());
        assertNull(testUser.getBannerImg());

        verify(userRepository).findByUsername("testuser");
        verify(userRepository).save(testUser);
    }
}

