package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Comment;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.dto.CommentDTO;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.UserDTO;
import java.time.Instant;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-21T15:08:35-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250628-1110, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public CommentDTO toCommentDTO(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        String id = null;
        UserDTO user = null;
        PostDTO post = null;
        String text = null;
        String imageUrl = null;
        CommentDTO parentComment = null;
        List<CommentDTO> comments = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        if ( comment.getId() != null ) {
            id = String.valueOf( comment.getId() );
        }
        user = userMapper.toUserDTO( comment.getUser() );
        post = mapPostWithoutLikes( comment.getPost() );
        text = comment.getText();
        imageUrl = comment.getImageUrl();
        parentComment = toCommentDTO( comment.getParentComment() );
        comments = mapCommentsToList( comment.getComments() );
        createdAt = comment.getCreatedAt();
        updatedAt = comment.getUpdatedAt();

        CommentDTO commentDTO = new CommentDTO( id, user, post, text, imageUrl, parentComment, comments, createdAt, updatedAt );

        return commentDTO;
    }

    @Override
    public PostDTO mapPostWithoutLikes(Post post) {
        if ( post == null ) {
            return null;
        }

        String id = null;
        String text = null;
        String imageUrl = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        UserDTO user = null;

        if ( post.getId() != null ) {
            id = String.valueOf( post.getId() );
        }
        text = post.getText();
        imageUrl = post.getImageUrl();
        createdAt = post.getCreatedAt();
        updatedAt = post.getUpdatedAt();
        user = userMapper.toUserDTO( post.getUser() );

        List<LikeDTO> likes = null;
        List<CommentDTO> comments = null;

        PostDTO postDTO = new PostDTO( id, text, imageUrl, likes, comments, createdAt, updatedAt, user );

        return postDTO;
    }
}
