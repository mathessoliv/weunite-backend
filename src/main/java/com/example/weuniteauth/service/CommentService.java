package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.Comment;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.CommentDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.comment.CommentRequestDTO;
import com.example.weuniteauth.exceptions.post.PostNotFoundException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.CommentMapper;
import com.example.weuniteauth.repository.CommentRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    public CommentService(UserRepository userRepository, CommentRepository commentRepository, PostRepository postRepository, CommentMapper commentMapper) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional
    public ResponseDTO<CommentDTO> createComment(Long userId, Long postId, CommentRequestDTO comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Comment newComment = new Comment(
                user,
                post,
                comment.text(),
                comment.image()
        );

        commentRepository.save(newComment);

        return commentMapper.toResponseDTO("Comentário criado com sucesso!", newComment);
    }
}
