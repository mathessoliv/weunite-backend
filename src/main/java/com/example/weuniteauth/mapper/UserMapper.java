package com.example.weuniteauth.mapper;

import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.user.UpdateUserRequestDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    User toEntity(CreateUserRequestDTO dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "bio", source = "bio")
    User toEntity(UpdateUserRequestDTO dto);

    UserDTO toDeleteUserDTO(String message, String username);

    UserDTO toGetUser(String message, String id, String name, String username, String email, Instant createdAt, Instant updatedAt);

    UserDTO toReturnPost(String id, String name, String username, String profileImg, Instant createdAt, Instant updatedAt);

    default UserDTO toUpdateUserDTO(String message, User user) {
        return new UserDTO(message, user.getId().toString(), user.getName(), user.getUsername(), user.getBio(), user.getEmail(), user.getProfileImg(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
