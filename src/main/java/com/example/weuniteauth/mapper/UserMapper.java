package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.domain.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    User toEntity(CreateUserRequestDTO dto);

    @Mapping(target = "id", source = "user.id", resultType = String.class)
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "bio", source = "user.bio")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "profileImg", source = "user.profileImg")
    @Mapping(target = "createdAt", source = "user.createdAt")
    @Mapping(target = "updatedAt", source = "user.updatedAt")
    UserDTO toUserDTO(User user);

    default ResponseDTO<UserDTO> toResponseDTO(String message, User user) {
        UserDTO userDTO = toUserDTO(user);
        return new ResponseDTO<>(message, userDTO);
    }

    default List<ResponseDTO<UserDTO>> toUserDTOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }

        return users.stream()
                .map(user -> toResponseDTO("", user))
                .collect(Collectors.toList());
    }
}
