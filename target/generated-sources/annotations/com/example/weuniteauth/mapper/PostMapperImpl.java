package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Comment;
import com.example.weuniteauth.domain.Like;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.UserDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-26T19:08:14-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PostDTO toPostDTO(Post post) {
        if ( post == null ) {
            return null;
        }

        String id = null;
        String text = null;
        String image = null;
        List<LikeDTO> likes = null;
        List<Comment> comments = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        UserDTO user = null;

        if ( post.getId() != null ) {
            id = String.valueOf( post.getId() );
        }
        text = post.getText();
        image = post.getImage();
        likes = mapLikesWithoutPost( post.getLikes() );
        List<Comment> list1 = post.getComments();
        if ( list1 != null ) {
            comments = new ArrayList<Comment>( list1 );
        }
        createdAt = post.getCreatedAt();
        updatedAt = post.getUpdatedAt();
        user = userMapper.toUserDTO( post.getAuthor() );

        PostDTO postDTO = new PostDTO( id, text, image, likes, comments, createdAt, updatedAt, user );

        return postDTO;
    }

    @Override
    public LikeDTO mapLikeWithoutPost(Like like) {
        if ( like == null ) {
            return null;
        }

        String id = null;
        UserDTO user = null;

        if ( like.getId() != null ) {
            id = String.valueOf( like.getId() );
        }
        user = userMapper.toUserDTO( like.getUser() );

        PostDTO post = null;

        LikeDTO likeDTO = new LikeDTO( id, user, post );

        return likeDTO;
    }
}
