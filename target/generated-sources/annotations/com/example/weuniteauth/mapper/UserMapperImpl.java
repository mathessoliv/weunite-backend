package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-25T16:22:46-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Amazon.com Inc.)"
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
        String bannerImg = null;
        boolean isPrivate = false;
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
        bannerImg = user.getBannerImg();
        isPrivate = user.isPrivate();
        createdAt = user.getCreatedAt();
        updatedAt = user.getUpdatedAt();

        UserDTO userDTO = new UserDTO( id, name, username, bio, email, profileImg, bannerImg, isPrivate, createdAt, updatedAt );

        return userDTO;
    }

    @Override
    public List<UserDTO> toUserDTOList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>( users.size() );
        for ( User user : users ) {
            list.add( toUserDTO( user ) );
        }

        return list;
    }
}
