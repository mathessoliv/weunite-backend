package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Follow;
import com.example.weuniteauth.dto.FollowDTO;
import com.example.weuniteauth.dto.UserDTO;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-19T20:09:13-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23-valhalla (Oracle Corporation)"
)
@Component
public class FollowMapperImpl implements FollowMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public FollowDTO toFollowDTO(Follow follow) {
        if ( follow == null ) {
            return null;
        }

        Long id = null;
        UserDTO follower = null;
        UserDTO followed = null;
        String status = null;
        String createdAt = null;
        String updatedAt = null;

        id = follow.getId();
        follower = userMapper.toUserDTO( follow.getFollower() );
        followed = userMapper.toUserDTO( follow.getFollowed() );
        if ( follow.getStatus() != null ) {
            status = follow.getStatus().name();
        }
        if ( follow.getCreatedAt() != null ) {
            createdAt = follow.getCreatedAt().toString();
        }
        if ( follow.getUpdatedAt() != null ) {
            updatedAt = follow.getUpdatedAt().toString();
        }

        FollowDTO followDTO = new FollowDTO( id, follower, followed, status, createdAt, updatedAt );

        return followDTO;
    }
}
