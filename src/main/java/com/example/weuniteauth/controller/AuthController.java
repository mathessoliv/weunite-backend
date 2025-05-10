package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.auth.login.LoginRequestDTO;
import com.example.weuniteauth.dto.auth.login.LoginResponseDTO;
import com.example.weuniteauth.dto.auth.resetpassword.*;
import com.example.weuniteauth.dto.auth.signup.SignUpResponseDTO;
import com.example.weuniteauth.dto.auth.verifyemail.VerifyEmailRequestDTO;
import com.example.weuniteauth.dto.auth.verifyemail.VerifyEmailResponseDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.service.AuthService;
import com.example.weuniteauth.validations.ValidPassword;
import jakarta.validation.Valid;
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
    public ResponseEntity<SignUpResponseDTO> signup(@RequestBody @Valid CreateUserRequestDTO createUserRequestDTO) {
        SignUpResponseDTO signUpResponseDTO = authService.signup(createUserRequestDTO);
        return ResponseEntity.ok(signUpResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginRequest = authService.login(loginRequestDTO);
        return ResponseEntity.ok(loginRequest);
    }

    @PutMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponseDTO> verifyEmail(@RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO) {
        VerifyEmailResponseDTO verifyEmailResponseDTO = authService.verifyEmail(verifyEmailRequestDTO);
        return ResponseEntity.ok(verifyEmailResponseDTO);
    }

    @PostMapping("/send-reset-password")
    public ResponseEntity<SendResetPasswordResponseDTO> sendResetPassword(@RequestBody SendResetPasswordRequestDTO sendResetPasswordRequestDTO) {
        SendResetPasswordResponseDTO sendResetPasswordResponseDTO = authService.sendResetPassword(sendResetPasswordRequestDTO);
        return ResponseEntity.ok(sendResetPasswordResponseDTO);
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<VerifyResetTokenResponseDTO> verifyResetToken(@RequestBody VerifyResetTokenRequestDTO verifyResetTokenRequestDTO) {
        VerifyResetTokenResponseDTO verifyResetTokenResponseDTO = authService.verifyResetPasswordToken(verifyResetTokenRequestDTO);
        return ResponseEntity.ok(verifyResetTokenResponseDTO);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO) {
        ResetPasswordResponseDTO resetPasswordResponseDTO = authService.resetPassword(resetPasswordRequestDTO);
        return ResponseEntity.ok(resetPasswordResponseDTO);
    }
}
