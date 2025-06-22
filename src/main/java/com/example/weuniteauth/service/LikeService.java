package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.Like;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.exceptions.post.PostNotFoundException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.LikeMapper;
import com.example.weuniteauth.repository.LikeRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;

    public LikeService(UserRepository userRepository, PostRepository postRepository, LikeRepository likeRepository, LikeMapper likeMapper) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.likeMapper = likeMapper;
    }

    @Transactional
    public LikeDTO like(User user, Post post) {

        Like like = new Like(post, user);

        likeRepository.save(like);

        return likeMapper.toLikeDTO(like, "Curtida criada com sucesso!");

    }

    @Transactional
    public LikeDTO unlike(Like like) {

        likeRepository.delete(like);

        return likeMapper.toLikeDTO(like, "Curtida deletada com sucesso!");

    }

    @Transactional
    public LikeDTO toggleLike(Long userId, Long postId) {
        User liker = userRepository.findById(userId).
                orElseThrow(UserNotFoundException::new);

        Post liked = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Like like = likeRepository.findByUserAndPost(liker, liked)
                .orElse(null);

        if (like == null) {
            return like(liker, liked);
        } else {
            return unlike(like);
        }
    }
}
