package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Like;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.RequestEntity.post;

@Mapper(componentModel = "spring", uses = {LikeMapper.class})
public interface PostMapper {

    @Mapping(target = "message", source = "message")
    @Mapping(target = "id", source = "post.id", resultType = String.class)
    @Mapping(target = "text", source = "post.text")
    @Mapping(target = "image", source = "post.image")
    @Mapping(target = "likes", source = "post.likes", qualifiedByName = "mapLikes")
    @Mapping(target = "comments", source = "post.comments")
    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "updatedAt", source = "post.updatedAt")
    @Mapping(target = "user", source = "post", qualifiedByName = "mapUser")
    PostDTO toPostDTO(Post post, String message);

    @Named("mapLikes")
    default Set<LikeDTO> mapLikes(Set<Like> likes) {
        if (likes == null) {
            return null;
        }

        return likes.stream()
                .map(like -> new LikeDTO(
                        null,
                        like.getId().toString(),
                        like.getUser().getUsername(),
                        like.getPost().getId().toString()
                ))
                .collect(Collectors.toSet());
    }

    @Named("mapUser")
    default UserDTO mapUser(Post post) {

        User author = post.getAuthor();
        return new UserDTO(
                null,
                author.getId().toString(),
                author.getName(),
                author.getUsername(),
                null,
                null,
                author.getProfileImg(),
                author.getCreatedAt(),
                author.getUpdatedAt()
        );
    }
}