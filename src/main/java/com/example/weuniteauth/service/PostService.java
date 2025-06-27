package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
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
    public ResponseDTO<PostDTO> createPost(Long userId, PostRequestDTO post) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Post createdPost = new Post(
                user,
                post.text(),
                post.image()
        );

        postRepository.save(createdPost);

        return postMapper.toResponseDTO("Publicação criada com sucesso!", createdPost);
    }

    @Transactional
    public ResponseDTO<PostDTO> updatePost(Long userId, Long postId, PostRequestDTO updatedPost) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!userId.equals(existingPost.getUser().getId())) {
            throw new UnauthorizedException("Você precisa estar logado para atualizar esta publicação");
        }

        existingPost.setText(updatedPost.text());
        existingPost.setImage(updatedPost.image());

        postRepository.save(existingPost);

        return postMapper.toResponseDTO("Publicação atualizada com sucesso!", existingPost);
    }

    @Transactional(readOnly = true)
    public ResponseDTO<PostDTO> getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return postMapper.toResponseDTO("Publicação consultada com sucesso!", post);
    }

    @Transactional
    public ResponseDTO<PostDTO> deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!userId.equals(post.getUser().getId())) {
            throw new UnauthorizedException("Você precisa estar logado para deletar essa publicação!");
        }

        postRepository.delete(post);

        return postMapper.toResponseDTO("Publicação excluída com sucesso", post);
    }
}
