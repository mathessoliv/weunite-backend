package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.auth.*;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    User toEntity(LoginRequestDTO loginRequestDTO);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "verificationToken", source = "verificationCode")
    User toEntity(VerifyEmailRequestDTO verifyEmailRequestDTO);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "email", source = "email")
    User toEntity(SendResetPasswordRequestDTO sendResetPasswordRequestDTO);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "verificationToken", source = "verificationToken")
    User toEntity(VerifyResetTokenRequestDTO verifyResetTokenRequestDTO);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "password", source = "newPassword")
    User toEntity(ResetPasswordRequestDTO resetPasswordRequestDTO);

    AuthDTO toSignUpResponseDTO(String message, String username);

    default AuthDTO toVerifyEmailResponseDTO(String message, String userId, String username, String email, String jwt, Long expiresIn, String name, String profileImg, Instant createdAt, Instant updatedAt) {
        return new AuthDTO(message, new UserDTO(userId, name, username, email, profileImg, jwt, expiresIn, createdAt, updatedAt));
    }

    default AuthDTO toLoginResponseDTO(String message, String userId, String username, String email, String jwt, Long expiresIn, String name, String profileImg, Instant createdAt, Instant updatedAt) {
        return new AuthDTO(message, new UserDTO(userId, name, username, email, profileImg, jwt, expiresIn, createdAt, updatedAt));
    }

    AuthDTO toSendResetPasswordResponseDTO(String message);

    AuthDTO toVerifyResetTokenResponseDTO(String message);

    AuthDTO toResetPasswordResponseDTO(String message);
}
