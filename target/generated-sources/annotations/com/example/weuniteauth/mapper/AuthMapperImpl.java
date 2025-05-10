package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.common.ExtendedTokenResponseDTO;
import com.example.weuniteauth.dto.common.MessageResponseDTO;
import com.example.weuniteauth.dto.common.TokenResponseDTO;
import com.example.weuniteauth.dto.common.UserBaseDTO;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-10T10:09:08-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public TokenResponseDTO toTokenResponseDTO(String accessToken, Long expiresIn) {
        if ( accessToken == null && expiresIn == null ) {
            return null;
        }

        String accessToken1 = null;
        accessToken1 = accessToken;
        Long expiresIn1 = null;
        expiresIn1 = expiresIn;

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO( accessToken1, expiresIn1 );

        return tokenResponseDTO;
    }

    @Override
    public UserBaseDTO toUserBaseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        String id = null;
        String name = null;
        String username = null;
        String email = null;

        if ( user.getId() != null ) {
            id = String.valueOf( user.getId() );
        }
        name = user.getName();
        username = user.getUsername();
        email = user.getEmail();

        LocalDateTime createdAt = null;

        UserBaseDTO userBaseDTO = new UserBaseDTO( id, name, username, email, createdAt );

        return userBaseDTO;
    }

    @Override
    public ExtendedTokenResponseDTO toExtendedTokenResponseDTO(String username, boolean verified, String message, String accessToken, Long expiresIn) {
        if ( username == null && message == null && accessToken == null && expiresIn == null ) {
            return null;
        }

        String username1 = null;
        username1 = username;
        boolean verified1 = false;
        verified1 = verified;
        String message1 = null;
        message1 = message;
        String accessToken1 = null;
        accessToken1 = accessToken;
        Long expiresIn1 = null;
        expiresIn1 = expiresIn;

        ExtendedTokenResponseDTO extendedTokenResponseDTO = new ExtendedTokenResponseDTO( username1, verified1, message1, accessToken1, expiresIn1 );

        return extendedTokenResponseDTO;
    }

    @Override
    public MessageResponseDTO toMessageResponseDTO(String message) {
        if ( message == null ) {
            return null;
        }

        String message1 = null;

        message1 = message;

        MessageResponseDTO messageResponseDTO = new MessageResponseDTO( message1 );

        return messageResponseDTO;
    }
}
