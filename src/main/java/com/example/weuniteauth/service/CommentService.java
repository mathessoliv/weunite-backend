package com.example.weuniteauth.service;

import com.example.weuniteauth.repository.CommentRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(UserRepository userRepository, CommentRepository commentRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }


}
