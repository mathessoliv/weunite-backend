package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.users.Follow;
import com.example.weuniteauth.dto.FollowDTO;
import com.example.weuniteauth.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FollowMapperImpl implements FollowMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public FollowDTO toFollowDTO(Follow follow) {
        if (follow == null) {
            return null;
        }

        Long id = follow.getId();

        UserDTO followerDto = null;
        if (follow.getFollower() != null) {
            followerDto = userMapper.toUserDTO(follow.getFollower());
        }

        UserDTO followedDto = null;
        if (follow.getFollowed() != null) {
            followedDto = userMapper.toUserDTO(follow.getFollowed());
        }

        String status = null;
        if (follow.getStatus() != null) {
            status = follow.getStatus().toString();
        }

        String createdAt = null;
        if (follow.getCreatedAt() != null) {
            createdAt = follow.getCreatedAt().toString();
        }

        String updatedAt = null;
        if (follow.getUpdatedAt() != null) {
            updatedAt = follow.getUpdatedAt().toString();
        }

        return new FollowDTO(id, followerDto, followedDto, status, createdAt, updatedAt);
    }
}

