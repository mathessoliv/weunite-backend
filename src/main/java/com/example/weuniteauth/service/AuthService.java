package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.auth.*;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.exceptions.auth.InvalidTokenException;
import com.example.weuniteauth.exceptions.auth.NotVerifiedEmailException;
import com.example.weuniteauth.service.mail.EmailService;
import com.example.weuniteauth.mapper.AuthMapper;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.service.jwt.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            AuthMapper authMapper,
            JwtService jwtService,
            EmailService emailService
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public ResponseDTO<AuthDTO> login(LoginRequestDTO requestDTO) {

        User user = userService.findUserEntityByUsername(requestDTO.username());

        if (!passwordEncoder.matches(requestDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }

        if (!user.isEmailVerified()) {
            throw new NotVerifiedEmailException("Verifique seu email para fazer login");
        }

        // Verificar se o usuário está banido
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new BadCredentialsException("Sua conta foi banida permanentemente da plataforma");
        }

        // Verificar se o usuário está suspenso
        if (Boolean.TRUE.equals(user.getIsSuspended())) {
            if (user.getSuspendedUntil() != null && Instant.now().isBefore(user.getSuspendedUntil())) {
                // Formatar data em padrão brasileiro
                java.time.ZonedDateTime zdt = user.getSuspendedUntil()
                    .atZone(java.time.ZoneId.of("America/Sao_Paulo"));
                java.time.format.DateTimeFormatter formatter = 
                    java.time.format.DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'às' HH:mm")
                        .withLocale(new java.util.Locale("pt", "BR"));
                String dataFormatada = zdt.format(formatter);
                
                throw new BadCredentialsException(
                    String.format("Sua conta está suspensa até %s", dataFormatada)
                );
            } else {
                // Suspensão expirou, remover flag
                user.setIsSuspended(false);
                user.setSuspendedUntil(null);
                user.setSuspensionReason(null);
            }
        }

        String jwtValue = jwtService.generateToken(user);
        Long expiresIn = jwtService.getDefaultTokenExpirationTime();

        return authMapper.toResponseDTO(
                "Login realizado com sucesso!",
                user,
                jwtValue,
                expiresIn
        );
    }

    @Transactional
    public ResponseDTO<AuthDTO> signUp(CreateUserRequestDTO requestDTO) {

        User newUser = userService.createUser(requestDTO);

        emailService.sendVerificationEmailAsync(requestDTO.email(), newUser.getVerificationToken());

        return authMapper.toResponseDTO("Cadastro concluído! Verifique seu email", newUser);
    }

    @Transactional
    public ResponseDTO<AuthDTO> verifyEmail(VerifyEmailRequestDTO requestDTO, String email) {

        User user = userService.findUserEntityByEmail(email);

        if (user.getVerificationToken() == null) {
            throw new InvalidTokenException();
        }

        if (!user.getVerificationToken().equals(requestDTO.verificationToken())) {
            throw new InvalidTokenException();
        }

        userService.verifyUserEmail(user);

        String jwtValue = jwtService.generateToken(user);
        Long expiresIn = jwtService.getDefaultTokenExpirationTime();

        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        return authMapper.toResponseDTO("Email verificado com sucesso!", user, jwtValue, expiresIn);
    }

    @Transactional
    public ResponseDTO<AuthDTO> sendResetPassword(SendResetPasswordRequestDTO requestDTO) {
        User user = userService.findUserEntityByEmail(requestDTO.email());

        if (!user.isEmailVerified()) {
            throw new NotVerifiedEmailException("Verifique seu e-mail para redefinir a senha");
        }

        userService.generateAndSetToken(user);
        emailService.sendPasswordResetRequestEmail(requestDTO.email(), user.getVerificationToken());

        return authMapper.toResponseDTO("Código enviado!");
    }

    @Transactional
    public ResponseDTO<AuthDTO> verifyResetPasswordToken(VerifyResetTokenRequestDTO requestDTO, String email) {
        User user = userService.findUserEntityByEmail(email);

        if (!user.getVerificationToken().equals(requestDTO.verificationToken())) {
            throw new InvalidTokenException();
        }

        return authMapper.toResponseDTO("Código verificado!");
    }

    @Transactional
    public ResponseDTO<AuthDTO> resetPassword(ResetPasswordRequestDTO requestDTO, String verificationToken) {
        User user = userService.findUserByVerificationToken(verificationToken);

        user.setPassword(passwordEncoder.encode(requestDTO.newPassword()));
        user.setVerificationToken(null);
        user.setVerificationTokenExpires(null);

        emailService.sendPasswordResetSuccessEmail(user.getEmail());
        return authMapper.toResponseDTO("Senha redefinida!");
    }

}
