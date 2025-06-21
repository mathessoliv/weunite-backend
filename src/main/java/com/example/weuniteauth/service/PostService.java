package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.post.PostRequestDTO;
import com.example.weuniteauth.exceptions.UnauthorizedException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.exceptions.post.PostNotFoundException;
import com.example.weuniteauth.mapper.PostMapper;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
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
    public PostDTO createPost(PostRequestDTO post) {
        User user = userRepository.findById(post.authorId())
                .orElseThrow(UserNotFoundException::new);

        Post createdPost = new Post(
                user,
                post.text(),
                post.image()
        );

        postRepository.save(createdPost);

        return postMapper.toPostDTO(createdPost, "Publicação criada com sucesso!");
    }

    @Transactional
    public PostDTO updatePost(Long postId, PostRequestDTO updatedPost) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!updatedPost.authorId().equals(existingPost.getAuthor().getId())) {
            throw new UnauthorizedException("Você precisar estar logado para atualizar esta publicação");
        }

        existingPost.setText(updatedPost.text());
        existingPost.setImage(updatedPost.image());

        postRepository.save(existingPost);

        return postMapper.toPostDTO(existingPost, "Publicação atualizada com sucesso!");
    }

    @Transactional(readOnly = true)
    public PostDTO getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return postMapper.toPostDTO(post, "Publicação consultada com sucesso!");
    }

    @Transactional
    public PostDTO deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        postRepository.delete(post);
        return postMapper.toPostDTO(post, "Publicação excluída com sucesso");
    }
}
