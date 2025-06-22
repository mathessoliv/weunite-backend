package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Like;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.PostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(target = "id", source = "like.id", resultType = String.class)
    @Mapping(target = "liker", source = "like.user.username")
    @Mapping(target = "post", source = "like.post.id", resultType = String.class)
    @Mapping(target = "message", source = "message")
    LikeDTO toLikeDTO(Like like, String message);

}
