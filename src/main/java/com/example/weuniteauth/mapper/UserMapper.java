package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Company;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.SkillDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default User toEntity(CreateUserRequestDTO dto) {
        String role = dto.role().toUpperCase();

        User user = switch (role) {
            case "ATHLETE" -> new Athlete();
            case "COMPANY" -> new Company();
            default -> throw new IllegalArgumentException("Tipo de usuário inválido: " + role);
        };

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setName(dto.name());

        return user;
    }

    @Mapping(target = "id", source = "user.id", resultType = String.class)
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "role", expression = "java(user.getRole().iterator().next().getName())")
    @Mapping(target = "bio", source = "user.bio")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "profileImg", source = "user.profileImg")
    @Mapping(target = "bannerImg", source = "user.bannerImg")
    @Mapping(target = "isPrivate", source = "user.private")
    @Mapping(target = "createdAt", source = "user.createdAt")
    @Mapping(target = "updatedAt", source = "user.updatedAt")
    @Mapping(target = "height", expression = "java(user instanceof com.example.weuniteauth.domain.users.Athlete ? ((com.example.weuniteauth.domain.users.Athlete) user).getHeight() : null)")
    @Mapping(target = "weight", expression = "java(user instanceof com.example.weuniteauth.domain.users.Athlete ? ((com.example.weuniteauth.domain.users.Athlete) user).getWeight() : null)")
    @Mapping(target = "footDomain", expression = "java(user instanceof com.example.weuniteauth.domain.users.Athlete ? ((com.example.weuniteauth.domain.users.Athlete) user).getFootDomain() : null)")
    @Mapping(target = "position", expression = "java(user instanceof com.example.weuniteauth.domain.users.Athlete ? ((com.example.weuniteauth.domain.users.Athlete) user).getPosition() : null)")
    @Mapping(target = "birthDate", expression = "java(user instanceof com.example.weuniteauth.domain.users.Athlete ? ((com.example.weuniteauth.domain.users.Athlete) user).getBirthDate() : null)")
    @Mapping(target = "skills", expression = "java(mapSkills(user))")
    UserDTO toUserDTO(User user);

    default List<SkillDTO> mapSkills(User user) {
        if (user instanceof Athlete athlete) {
            return athlete.getSkills().stream()
                    .map(skill -> new SkillDTO(skill.getId(), skill.getName()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    List<UserDTO> toUserDTOList(List<User> users);

    default ResponseDTO<UserDTO> toResponseDTO(String message, User user) {
        UserDTO userDTO = toUserDTO(user);
        return new ResponseDTO<>(message, userDTO);
    }

    default ResponseDTO<List<UserDTO>> toSearchResponseDTO(String message, List<User> users) {
        List<UserDTO> userDTOs = toUserDTOList(users);
        return new ResponseDTO<>(message, userDTOs);
    }
}
