package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.common.UserBaseDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-10T10:09:08-0300",
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
    public UserBaseDTO toUserBaseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        String name = null;
        String username = null;
        String email = null;

        name = user.getName();
        username = user.getUsername();
        email = user.getEmail();

        String id = user.getId().toString();
        LocalDateTime createdAt = null;

        UserBaseDTO userBaseDTO = new UserBaseDTO( id, name, username, email, createdAt );

        return userBaseDTO;
    }
}
