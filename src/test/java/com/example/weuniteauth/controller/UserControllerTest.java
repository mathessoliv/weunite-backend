package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.UpdateUserRequestDTO;
import com.example.weuniteauth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ResponseDTO<UserDTO> responseDTO;

    @BeforeEach
    void setUp() {
        UserDTO dto = new UserDTO("1", "user", "username", "BASIC", null, "user@test.com",
                null, null, false, Instant.now(), Instant.now());
        responseDTO = new ResponseDTO<>("ok", dto);
    }

    @Test
    void shouldGetUserByUsername() {
        when(userService.getUser("username")).thenReturn(responseDTO);
        ResponseEntity<ResponseDTO<UserDTO>> response = userController.getUser("username");
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }

    @Test
    void shouldGetUserById() {
        when(userService.getUser(1L)).thenReturn(responseDTO);
        assertThat(userController.getUserById(1L).getBody()).isEqualTo(responseDTO);
    }

    @Test
    void shouldDeleteUserAndBanner() {
        when(userService.deleteUser("username")).thenReturn(responseDTO);
        when(userService.deleteBanner("username")).thenReturn(responseDTO);

        assertThat(userController.deleteUser("username").getBody()).isEqualTo(responseDTO);
        assertThat(userController.deleteBanner("username").getBody()).isEqualTo(responseDTO);
    }

    @Test
    void shouldUpdateUser() {
        when(userService.updateUser(any(UpdateUserRequestDTO.class), any(String.class), any(), any())).thenReturn(responseDTO);
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO("name", "username", "bio", true);
        MockMultipartFile profile = new MockMultipartFile("profileImage", new byte[0]);
        MockMultipartFile banner = new MockMultipartFile("bannerImage", new byte[0]);

        assertThat(userController.updateUser("username", requestDTO, profile, banner).getBody()).isEqualTo(responseDTO);
    }

    @Test
    void shouldSearchUsers() {
        ResponseDTO<List<UserDTO>> listResponse = new ResponseDTO<>("ok", List.of());
        when(userService.searchUsers("query")).thenReturn(listResponse);

        assertThat(userController.searchUsers("query").getBody()).isEqualTo(listResponse);
    }
}

