package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Comment;
import com.example.weuniteauth.domain.Like;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(target = "id", source = "like.id", resultType = String.class)
    @Mapping(target = "user", source = "like.user.username")
    @Mapping(target = "post", source = "like.post", qualifiedByName = "mapPost")
    @Mapping(target = "message", source = "message")
    LikeDTO toLikeDTO(Like like, String message);

    @Named("mapPost")
    default PostDTO mapPost(Post post) {
        User author = post.getAuthor();
        return new PostDTO(
                null,
                post.getId().toString(),
                post.getText(),
                post.getImage(),
                mapLikes(post.getLikes()),
                mapComments(post.getComments()),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                new UserDTO(
                        null,
                        author.getId().toString(),
                        author.getName(),
                        author.getUsername(),
                        null,
                        null,
                        author.getProfileImg(),
                        null,
                        null
                )
        );
    }

    @Named("mapLikes")
    default Set<String> mapLikes(Set<Like> likes) {
        if (likes == null) {
            return null;
        }

        return likes.stream()
                .map(like -> like.getId().toString())
                .collect(Collectors.toSet());
    }

    @Named("mapComments")
    default List<String> mapComments(List<Comment> comments) {
        if (comments == null) {
            return null;
        }

        return comments.stream()
                .map(comment -> comment.getId().toString())
                .collect(Collectors.toList());
    }

    default Set<LikeDTO> toLikeDTOSet(Set<Like> likes) {
        if (likes == null) {
            return null;
        }

        return likes.stream()
                .map(like -> toLikeDTO(like, null))
                .collect(Collectors.toSet());
    }
}
