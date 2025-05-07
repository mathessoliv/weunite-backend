package com.example.weuniteauth.mapper;

import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.user.UserResponseDTO;
import com.example.weuniteauth.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserRequestDTO dto);

    @Mapping(target = "id", expression = "java(user.getId().toString())")
    UserResponseDTO toUserResponseDto(User user);
}
