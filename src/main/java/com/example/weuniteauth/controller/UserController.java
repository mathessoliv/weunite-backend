package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.UpdateUserRequestDTO;
import com.example.weuniteauth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        UserDTO userDTO = userService.getUser(username);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUser(id);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable String username) {
        UserDTO userDTO = userService.deleteUser(username);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username, @RequestBody UpdateUserRequestDTO requestDTO) {
        UserDTO userDTO = userService.updateUser(requestDTO, username);
        return ResponseEntity.ok(userDTO);
    }


}
