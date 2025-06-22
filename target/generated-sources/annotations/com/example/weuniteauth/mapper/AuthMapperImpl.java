package com.example.weuniteauth.mapper;

import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.UserDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-21T23:14:18-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

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
