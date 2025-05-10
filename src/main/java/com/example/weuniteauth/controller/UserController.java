package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.user.UserResponseDTO;
import com.example.weuniteauth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String username) {
        UserResponseDTO user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }


}
