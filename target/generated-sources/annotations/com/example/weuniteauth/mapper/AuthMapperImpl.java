package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.AuthDTO;
import com.example.weuniteauth.dto.UserDTO;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-21T15:08:10-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250628-1110, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public AuthDTO toAuthDTO(User user, String jwt, Long expiresIn) {
        if ( user == null && jwt == null && expiresIn == null ) {
            return null;
        }

        UserDTO user1 = null;
        user1 = userMapper.toUserDTO( user );
        String jwt1 = null;
        jwt1 = jwt;
        Long expiresIn1 = null;
        expiresIn1 = expiresIn;

        AuthDTO authDTO = new AuthDTO( user1, jwt1, expiresIn1 );

        return authDTO;
    }
}
