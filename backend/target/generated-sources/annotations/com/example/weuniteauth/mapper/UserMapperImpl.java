package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-13T15:55:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(CreateUserRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setName( dto.name() );
        user.setUsername( dto.username() );
        user.setEmail( dto.email() );
        user.setPassword( dto.password() );

        return user;
    }

    @Override
    public UserDTO toDeleteUserDTO(String message, String username) {
        if ( message == null && username == null ) {
            return null;
        }

        String username1 = null;
        username1 = username;

        String id = null;
        String name = null;
        String email = null;
        String profileImg = null;
        String jwt = null;
        Long expiresIn = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        UserDTO userDTO = new UserDTO( id, name, username1, email, profileImg, jwt, expiresIn, createdAt, updatedAt );

        return userDTO;
    }

    @Override
    public UserDTO toGetUser(String message, String id, String name, String username, String email, Instant createdAt, Instant updatedAt) {
        if ( message == null && id == null && name == null && username == null && email == null && createdAt == null && updatedAt == null ) {
            return null;
        }

        String id1 = null;
        id1 = id;
        String name1 = null;
        name1 = name;
        String username1 = null;
        username1 = username;
        String email1 = null;
        email1 = email;
        Instant createdAt1 = null;
        createdAt1 = createdAt;
        Instant updatedAt1 = null;
        updatedAt1 = updatedAt;

        String profileImg = null;
        String jwt = null;
        Long expiresIn = null;

        UserDTO userDTO = new UserDTO( id1, name1, username1, email1, profileImg, jwt, expiresIn, createdAt1, updatedAt1 );

        return userDTO;
    }
}
