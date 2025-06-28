package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Like;
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
    date = "2025-06-28T14:06:45-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class LikeMapperImpl implements LikeMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public LikeDTO toLikeDTO(Like like) {
        if ( like == null ) {
            return null;
        }

        String id = null;
        UserDTO user = null;
        PostDTO post = null;

        if ( like.getId() != null ) {
            id = String.valueOf( like.getId() );
        }
        user = userMapper.toUserDTO( like.getUser() );
        post = mapPostWithoutLikes( like.getPost() );

        LikeDTO likeDTO = new LikeDTO( id, user, post );

        return likeDTO;
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
