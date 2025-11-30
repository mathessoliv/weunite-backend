package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.post.Comment;
import com.example.weuniteauth.domain.post.Like;
import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.post.Repost;
import com.example.weuniteauth.dto.CommentDTO;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.RepostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PostMapper {

    @Mapping(target = "id", source = "post.id", resultType = String.class)
    @Mapping(target = "text", source = "post.text")
    @Mapping(target = "imageUrl", source = "post.imageUrl")
    @Mapping(target = "likes", source = "post.likes", qualifiedByName = "mapLikesWithoutPost")
    @Mapping(target = "comments", source = "post.comments", qualifiedByName = "mapCommentsWithoutPost")
    @Mapping(target = "reposts", source = "post.reposts", qualifiedByName = "mapRepostsWithoutPost")
    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "updatedAt", source = "post.updatedAt")
    @Mapping(target = "user", source = "post.user")
    @Mapping(target = "repostedBy", ignore = true)
    @Mapping(target = "repostedAt", ignore = true)
    PostDTO toPostDTO(Post post);

    @Mapping(target = "id", source = "repost.post.id", resultType = String.class)
    @Mapping(target = "text", source = "repost.post.text")
    @Mapping(target = "imageUrl", source = "repost.post.imageUrl")
    @Mapping(target = "videoUrl", source = "repost.post.videoUrl")
    @Mapping(target = "likes", source = "repost.post.likes", qualifiedByName = "mapLikesWithoutPost")
    @Mapping(target = "comments", source = "repost.post.comments", qualifiedByName = "mapCommentsWithoutPost")
    @Mapping(target = "reposts", source = "repost.post.reposts", qualifiedByName = "mapRepostsWithoutPost")
    @Mapping(target = "createdAt", source = "repost.post.createdAt")
    @Mapping(target = "updatedAt", source = "repost.post.updatedAt")
    @Mapping(target = "user", source = "repost.post.user")
    @Mapping(target = "repostedBy", source = "repost.user")
    @Mapping(target = "repostedAt", source = "repost.createdAt")
    PostDTO toPostDTOFromRepost(Repost repost);

    default List<PostDTO> toPostDTOListFromReposts(List<Repost> reposts) {
        if (reposts == null || reposts.isEmpty()) {
            return List.of();
        }

        return reposts.stream()
                .map(this::toPostDTOFromRepost)
                .collect(Collectors.toList());
    }

    default ResponseDTO<PostDTO> toResponseDTO(String message, Post post) {
        PostDTO postDTO = toPostDTO(post);
        return new ResponseDTO<>(message, postDTO);
    }

    default List<PostDTO> toPostDTOList(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return List.of();
        }

        return posts.stream()
                .map(this::toPostDTO)
                .collect(Collectors.toList());
    }

    @Named("mapLikeWithoutPost")
    @Mapping(target = "id", source = "like.id", resultType = String.class)
    @Mapping(target = "user", source = "like.user")
    @Mapping(target = "post", ignore = true)
    LikeDTO mapLikeWithoutPost(Like like);

    @Named("mapLikesWithoutPost")
    default List<LikeDTO> mapLikesWithoutPost(Set<Like> likes) {
        if (likes == null || likes.isEmpty()) {
            return List.of();
        }

        return likes.stream()
                .map(this::mapLikeWithoutPost)
                .collect(Collectors.toList());
    }

    @Named("mapCommentWithoutPost")
    @Mapping(target = "id", source = "comment.id", resultType = String.class)
    @Mapping(target = "user", source = "comment.user")
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "imageUrl", source = "comment.imageUrl")
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", source = "comment.createdAt")
    @Mapping(target = "updatedAt", source = "comment.updatedAt")
    CommentDTO mapCommentWithoutPost(Comment comment);

    @Named("mapCommentsWithoutPost")
    default List<CommentDTO> mapCommentsWithoutPost(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }

        return comments.stream()
                .filter(comment -> !comment.isDeleted())
                .map(this::mapCommentWithoutPost)
                .collect(Collectors.toList());
    }

    @Named("mapRepostWithoutPost")
    @Mapping(target = "id", source = "repost.id", resultType = String.class)
    @Mapping(target = "user", source = "repost.user")
    @Mapping(target = "post", ignore = true)
    RepostDTO mapRepostWithoutPost(Repost repost);

    @Named("mapRepostsWithoutPost")
    default List<RepostDTO> mapRepostsWithoutPost(Set<Repost> reposts) {
        if (reposts == null || reposts.isEmpty()) {
            return List.of();
        }

        return reposts.stream()
                .map(this::mapRepostWithoutPost)
                .collect(Collectors.toList());
    }
}