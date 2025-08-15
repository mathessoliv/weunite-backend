package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.UpdateUserRequestDTO;
import com.example.weuniteauth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ResponseDTO<UserDTO>> getUser(@PathVariable String username) {
        ResponseDTO<UserDTO> userDTO = userService.getUser(username);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> getUserById(@PathVariable Long id) {
        ResponseDTO<UserDTO> userDTO = userService.getUser(id);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<ResponseDTO<UserDTO>> deleteUser(@PathVariable String username) {
        ResponseDTO<UserDTO> userDTO = userService.deleteUser(username);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<ResponseDTO<UserDTO>> updateUser(@PathVariable String username,
                                                           @RequestPart(value = "user") UpdateUserRequestDTO requestDTO,
                                                           @RequestPart(value = "image", required = false) MultipartFile image) {
        ResponseDTO<UserDTO> userDTO = userService.updateUser(requestDTO, username, image);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResponseDTO<UserDTO>>> searchUser(@RequestParam String username,
                                                                 @RequestParam(defaultValue = "10") Integer limit) {
        List<ResponseDTO<UserDTO>> userDTOs = userService.searchUsersByName(username, limit);
        return ResponseEntity.ok(userDTOs);
    }

}
