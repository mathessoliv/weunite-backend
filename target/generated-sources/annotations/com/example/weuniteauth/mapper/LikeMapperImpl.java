package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Like;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.LikeDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-21T23:01:56-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class LikeMapperImpl implements LikeMapper {

    @Override
    public LikeDTO toLikeDTO(Like like, String message) {
        if ( like == null && message == null ) {
            return null;
        }

        String id = null;
        String liker = null;
        String post = null;
        if ( like != null ) {
            if ( like.getId() != null ) {
                id = String.valueOf( like.getId() );
            }
            liker = likeUserUsername( like );
            Long id1 = likePostId( like );
            if ( id1 != null ) {
                post = String.valueOf( id1 );
            }
        }
        String message1 = null;
        message1 = message;

        LikeDTO likeDTO = new LikeDTO( message1, id, liker, post );

        return likeDTO;
    }

    private String likeUserUsername(Like like) {
        if ( like == null ) {
            return null;
        }
        User user = like.getUser();
        if ( user == null ) {
            return null;
        }
        String username = user.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    private Long likePostId(Like like) {
        if ( like == null ) {
            return null;
        }
        Post post = like.getPost();
        if ( post == null ) {
            return null;
        }
        Long id = post.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
