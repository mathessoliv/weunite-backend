package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.UserDTO;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-24T17:17:29-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public PostDTO toPostDTO(Post post, String message) {
        if ( post == null && message == null ) {
            return null;
        }

        String id = null;
        String text = null;
        String image = null;
        Set<String> likes = null;
        List<String> comments = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        UserDTO user = null;
        if ( post != null ) {
            if ( post.getId() != null ) {
                id = String.valueOf( post.getId() );
            }
            text = post.getText();
            image = post.getImage();
            likes = mapLikes( post.getLikes() );
            comments = mapComments( post.getComments() );
            createdAt = post.getCreatedAt();
            updatedAt = post.getUpdatedAt();
            user = mapUser( post );
        }
        String message1 = null;
        message1 = message;

        PostDTO postDTO = new PostDTO( message1, id, text, image, likes, comments, createdAt, updatedAt, user );

        return postDTO;
    }
}
