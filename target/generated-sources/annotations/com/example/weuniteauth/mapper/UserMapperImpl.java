package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-30T16:06:07-0300",
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
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        String id = null;
        String name = null;
        String username = null;
        String bio = null;
        String email = null;
        String profileImg = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        if ( user.getId() != null ) {
            id = String.valueOf( user.getId() );
        }
        name = user.getName();
        username = user.getUsername();
        bio = user.getBio();
        email = user.getEmail();
        profileImg = user.getProfileImg();
        createdAt = user.getCreatedAt();
        updatedAt = user.getUpdatedAt();

        UserDTO userDTO = new UserDTO( id, name, username, bio, email, profileImg, createdAt, updatedAt );

        return userDTO;
    }
}
