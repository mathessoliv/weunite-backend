package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.UserDTO;
import org.mapstruct.Mapper;

import static org.springframework.http.RequestEntity.post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    default PostDTO toPostDTO(Post post) {
        User author = post.getAuthor();
        return new PostDTO(
                "Publicação criada com sucesso!",
                post.getId().toString(),
                post.getText(),
                post.getImage(),
                post.getLikes(),
                post.getComments(),
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
                        author.getCreatedAt(),
                        author.getUpdatedAt()
                )
        );
    }
}
