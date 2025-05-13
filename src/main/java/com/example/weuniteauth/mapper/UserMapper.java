package com.example.weuniteauth.mapper;

import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.domain.User;
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

    UserDTO toDeleteUserDTO(String message, String username);
    UserDTO toGetUser(String message, String id, String name, String username, String email, Instant createdAt, Instant updatedAt);
}
