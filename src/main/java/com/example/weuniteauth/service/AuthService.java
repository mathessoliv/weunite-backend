package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.auth.resetpassword.*;
import com.example.weuniteauth.dto.auth.verifyemail.VerifyEmailRequestDTO;
import com.example.weuniteauth.dto.auth.verifyemail.VerifyEmailResponseDTO;
import com.example.weuniteauth.dto.auth.login.LoginRequestDTO;
import com.example.weuniteauth.dto.auth.login.LoginResponseDTO;
import com.example.weuniteauth.dto.auth.signup.SignUpResponseDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.exceptions.auth.NotVerifiedEmailException;
import com.example.weuniteauth.service.mail.EmailService;
import com.example.weuniteauth.mapper.AuthMapper;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.service.jwt.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(UserService userService,
                       PasswordEncoder passwordEncoder,
                       AuthMapper authMapper,
                       JwtService jwtService,
                       EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {// username e senha


        User user = userService.findUserEntityByUsername(requestDTO.username());

        if (!passwordEncoder.matches(requestDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }

        if (!user.isEmailVerified()) {
            throw new NotVerifiedEmailException("Verifique seu email para fazer login");
        }

        String jwtValue = jwtService.generateToken(user);
        Long expiresIn = jwtService.getDefaultExpirationTime();

        return authMapper.toLoginResponseDTO(jwtValue, expiresIn);
    }

    @Transactional
    public SignUpResponseDTO signup(CreateUserRequestDTO requestDTO) {

        User newUser = userService.createUser(requestDTO);

        emailService.sendVerificationEmailAsync(requestDTO.email(), newUser.getVerificationToken());

        return authMapper.toSignUpResponseDTO(newUser);
    }

    @Transactional
    public VerifyEmailResponseDTO verifyEmail(VerifyEmailRequestDTO requestDTO) {
        String verificationCode = requestDTO.verificationCode();
        User user = userService.findUserByVerificationToken(verificationCode);
        userService.verifyUserEmail(user);

        String jwtValue = jwtService.generateToken(user);
        Long expiresIn = jwtService.getDefaultExpirationTime();

        return authMapper.toVerifyEmailResponseDTO(user.getUsername(), true, "E-mail verificado com sucesso", jwtValue, expiresIn);
    }

    @Transactional
    public SendResetPasswordResponseDTO sendResetPassword(SendResetPasswordRequestDTO requestDTO) {
        User user = userService.findUserEntityByEmail(requestDTO.email());

        if (!user.isEmailVerified()) {
            throw new NotVerifiedEmailException("Verifique seu e-mail para redefinir a senha");
        }

        userService.generateAndSetToken(user);
        emailService.sendPasswordResetRequestEmail(requestDTO.email(), user.getVerificationToken());

        return authMapper.toSendResetPasswordResponseDTO("Código enviado!");
    }

    @Transactional
    public VerifyResetTokenResponseDTO verifyResetPasswordToken(VerifyResetTokenRequestDTO requestDTO) {
        User user = userService.findUserByVerificationToken(requestDTO.verificationToken());
        return authMapper.toVerifyResetTokenResponseDTO("Código verificado!");
    };

    @Transactional
    public ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO requestDTO) {
        User user = userService.findUserByVerificationToken(requestDTO.verificationToken());

        user.setPassword(passwordEncoder.encode(requestDTO.newPassword()));
        user.setVerificationToken(null);
        user.setVerificationTokenExpires(null);

        emailService.sendPasswordResetSuccessEmail(user.getEmail());
        return authMapper.toResetPasswordResponseDTO("Senha redefinida");
    }
}
