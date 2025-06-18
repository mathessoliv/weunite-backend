package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.auth.LoginRequestDTO;
import com.example.weuniteauth.dto.auth.ResetPasswordRequestDTO;
import com.example.weuniteauth.dto.auth.SendResetPasswordRequestDTO;
import com.example.weuniteauth.dto.auth.VerifyEmailRequestDTO;
import com.example.weuniteauth.dto.auth.VerifyResetTokenRequestDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-18T16:21:53-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public User toEntity(LoginRequestDTO loginRequestDTO) {
        if ( loginRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( loginRequestDTO.username() );
        user.setPassword( loginRequestDTO.password() );

        return user;
    }

    @Override
    public User toEntity(VerifyEmailRequestDTO verifyEmailRequestDTO) {
        if ( verifyEmailRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setVerificationToken( verifyEmailRequestDTO.verificationToken() );

        return user;
    }

    @Override
    public User toEntity(SendResetPasswordRequestDTO sendResetPasswordRequestDTO) {
        if ( sendResetPasswordRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( sendResetPasswordRequestDTO.email() );

        return user;
    }

    @Override
    public User toEntity(VerifyResetTokenRequestDTO verifyResetTokenRequestDTO) {
        if ( verifyResetTokenRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setVerificationToken( verifyResetTokenRequestDTO.verificationToken() );

        return user;
    }

    @Override
    public User toEntity(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        if ( resetPasswordRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setPassword( resetPasswordRequestDTO.newPassword() );

        return user;
    }

    @Override
    public AuthDTO toSignUpResponseDTO(String message, String username) {
        if ( message == null && username == null ) {
            return null;
        }

        String message1 = null;
        message1 = message;

        UserDTO user = null;
        String jwt = null;
        Long expiresIn = null;

        AuthDTO authDTO = new AuthDTO( message1, user, jwt, expiresIn );

        return authDTO;
    }

    @Override
    public AuthDTO toSendResetPasswordResponseDTO(String message) {
        if ( message == null ) {
            return null;
        }

        String message1 = null;

        message1 = message;

        UserDTO user = null;
        String jwt = null;
        Long expiresIn = null;

        AuthDTO authDTO = new AuthDTO( message1, user, jwt, expiresIn );

        return authDTO;
    }

    @Override
    public AuthDTO toVerifyResetTokenResponseDTO(String message) {
        if ( message == null ) {
            return null;
        }

        String message1 = null;

        message1 = message;

        UserDTO user = null;
        String jwt = null;
        Long expiresIn = null;

        AuthDTO authDTO = new AuthDTO( message1, user, jwt, expiresIn );

        return authDTO;
    }

    @Override
    public AuthDTO toResetPasswordResponseDTO(String message) {
        if ( message == null ) {
            return null;
        }

        String message1 = null;

        message1 = message;

        UserDTO user = null;
        String jwt = null;
        Long expiresIn = null;

        AuthDTO authDTO = new AuthDTO( message1, user, jwt, expiresIn );

        return authDTO;
    }
}
