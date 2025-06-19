package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.post.CreatePostResponseDTO;
import org.mapstruct.Mapper;

import static org.springframework.http.RequestEntity.post;

@Mapper(componentModel = "spring")
public interface PostMapper {

    CreatePostResponseDTO toCreatePostResponseDTO(Post post);

    default CreatePostResponseDTO toCreatePostResponseDTO(Post post) {
        return new CreatePostResponseDTO(
                post.getAuthor().getId().toString(),
                post.getText(),
                post.getImage(),
                post.getLikes(),
                post.getComments(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
    post.getAuthor().
,
}
