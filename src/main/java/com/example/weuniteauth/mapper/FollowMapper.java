package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.users.Follow;
import com.example.weuniteauth.dto.FollowDTO;
import com.example.weuniteauth.dto.ResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface FollowMapper {

    FollowDTO toFollowDTO(Follow follow);

    default ResponseDTO<FollowDTO> toResponseDTO(String message, Follow follow) {
        FollowDTO followDTO = toFollowDTO(follow);
        return new ResponseDTO<>(message, followDTO);
    }

    default ResponseDTO<List<FollowDTO>> toResponseDTO(String message, List<Follow> follows) {
        List<FollowDTO> followDTOs = mapFollows(follows);
        return new ResponseDTO<>(message, followDTOs);
    }

    default List<FollowDTO> mapFollows(List<Follow> follows) {
        if (follows == null || follows.isEmpty()) {
            return List.of();
        }

        return follows.stream()
                .map(this::toFollowDTO)
                .collect(Collectors.toList());
    }
}
