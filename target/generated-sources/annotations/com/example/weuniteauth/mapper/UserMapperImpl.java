package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-21T23:28:34-0300",
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

        String message1 = null;
        message1 = message;
        String username1 = null;
        username1 = username;

        String id = null;
        String name = null;
        String bio = null;
        String email = null;
        String profileImg = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        UserDTO userDTO = new UserDTO( message1, id, name, username1, bio, email, profileImg, createdAt, updatedAt );

        return userDTO;
    }

    @Override
    public UserDTO toGetUser(String message, String id, String name, String username, String email, Instant createdAt, Instant updatedAt) {
        if ( message == null && id == null && name == null && username == null && email == null && createdAt == null && updatedAt == null ) {
            return null;
        }

        String message1 = null;
        message1 = message;
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

        String bio = null;
        String profileImg = null;

        UserDTO userDTO = new UserDTO( message1, id1, name1, username1, bio, email1, profileImg, createdAt1, updatedAt1 );

        return userDTO;
    }
}
