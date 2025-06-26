package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.Like;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.exceptions.post.PostNotFoundException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.LikeMapper;
import com.example.weuniteauth.repository.LikeRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

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
    public ResponseDTO<LikeDTO> toggleLike(Long userId, Long postId) {

        User user = userRepository.findById(userId).
                orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Like existingLike = likeRepository.findByUserAndPost(user, post)
                .orElse(null);

        if (existingLike == null) {
            Like newLike = new Like(post, user);
            post.addLike(newLike);
            likeRepository.save(newLike);
            return likeMapper.toResponseDTO( "Curtida criada com sucesso!", newLike);
        } else {
            post.removeLike(existingLike);
            likeRepository.delete(existingLike);
            return likeMapper.toResponseDTO("Curtida deletada com sucesso!", existingLike);
        }

    }

    @Transactional(readOnly = true)
    public ResponseDTO<List<LikeDTO>> getLikes(Long userId) {
        User user = userRepository.findById(userId).
                orElseThrow(UserNotFoundException::new);

        Set<Like> likes = likeRepository.findByUser(user);

        return likeMapper.toResponseDTO("Likes consultados com sucesso!", likes);
    }

    @Transactional(readOnly = true)
    public ResponseDTO<List<LikeDTO>> getLikes(Long userId, int pagina, int items) {
        User user = userRepository.findById(userId).
                orElseThrow(UserNotFoundException::new);

        Pageable pageable = PageRequest.of(pagina, items);

        Page<Like> likes = likeRepository.findByUser(user, pageable);

        return likeMapper.toResponseDTO("Likes consultados com sucesso!", likes.getContent());
    }
}
