package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.auth.*;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthDTO> signup(@RequestBody @Valid CreateUserRequestDTO createUserRequestDTO) {
        AuthDTO signUpResponseDTO = authService.signUp(createUserRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        AuthDTO loginRequest = authService.login(loginRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(loginRequest);
    }

    @PostMapping("/verify-email/{email}")
    public ResponseEntity<AuthDTO> verifyEmail(@RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO, @PathVariable String email) {
        AuthDTO verifyEmailResponseDTO = authService.verifyEmail(verifyEmailRequestDTO, email);
        return ResponseEntity.status(HttpStatus.OK).body(verifyEmailResponseDTO);
    }

    @PostMapping("/send-reset-password")
    public ResponseEntity<AuthDTO> sendResetPassword(@RequestBody SendResetPasswordRequestDTO sendResetPasswordRequestDTO) {
        AuthDTO sendResetPasswordResponseDTO = authService.sendResetPassword(sendResetPasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(sendResetPasswordResponseDTO);
    }

    @PostMapping("/verify-reset-token/{email}")
    public ResponseEntity<AuthDTO> verifyResetToken(@RequestBody VerifyResetTokenRequestDTO verifyResetTokenRequestDTO, @PathVariable String email) {
        AuthDTO verifyResetTokenResponseDTO = authService.verifyResetPasswordToken(verifyResetTokenRequestDTO, email);
        return ResponseEntity.status(HttpStatus.OK).body(verifyResetTokenResponseDTO);
    }

    @PostMapping("/reset-password/{verificationToken}")
    public ResponseEntity<AuthDTO> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO, @PathVariable String verificationToken) {
        AuthDTO resetPasswordResponseDTO = authService.resetPassword(resetPasswordRequestDTO, verificationToken);
        return ResponseEntity.status(HttpStatus.OK).body(resetPasswordResponseDTO);
    }
}
