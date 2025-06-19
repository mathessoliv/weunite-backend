package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.post.CreatePostRequestDTO;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.PostMapper;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostService(UserRepository userRepository, PostRepository postRepository, PostMapper postMapper) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Transactional
    public PostDTO createPost(CreatePostRequestDTO post) {
        User user = userRepository.findById(post.authorId())
                .orElseThrow(UserNotFoundException::new);

        Post createdPost = new Post(user, post.text(), post.image());
        postRepository.save(createdPost);

        return postMapper.toPostDTO(createdPost);
    }


}
