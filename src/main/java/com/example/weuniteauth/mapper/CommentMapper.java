package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Comment;
import com.example.weuniteauth.domain.Like;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.dto.CommentDTO;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", source = "comment.id", resultType = String.class)
    @Mapping(target = "user", source = "comment.user")
    @Mapping(target = "post", source = "comment.post", qualifiedByName = "mapPostWithoutLikes")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "image", source = "comment.image")
    @Mapping(target = "parentComment", source = "comment.parentComment")
    @Mapping(target = "comments", source = "comment.comments", qualifiedByName = "mapCommentsToList")
    @Mapping(target = "createdAt", source = "comment.createdAt")
    @Mapping(target = "updatedAt", source = "comment.updatedAt")
    CommentDTO toCommentDTO(Comment comment);

    @Named("mapPostWithoutLikes")
    @Mapping(target = "id", source = "post.id", resultType = String.class)
    @Mapping(target = "text", source = "post.text")
    @Mapping(target = "image", source = "post.image")
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "updatedAt", source = "post.updatedAt")
    @Mapping(target = "user", source = "post.user")
    PostDTO mapPostWithoutLikes(Post post);

    @Named("mapCommentsToList")
    default List<CommentDTO> mapCommentsToList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }

        return comments.stream()
                .map(this::toCommentDTO)
                .collect(Collectors.toList());
    }

    default ResponseDTO<CommentDTO> toResponseDTO(String message, Comment comment) {
        CommentDTO commentDTO = toCommentDTO(comment);
        return new ResponseDTO<>(message, commentDTO);
    }

}
