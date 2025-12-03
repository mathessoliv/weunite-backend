package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.post.Repost;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.RepostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.exceptions.post.PostNotFoundException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.RepostMapper;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.RepostRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final RepostRepository repostRepository;
    private final RepostMapper repostMapper;
    private final NotificationService notificationService;

    @Transactional
    public ResponseDTO<RepostDTO> toggleRepost(Long userId, Long postId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Repost existingRepost = repostRepository.findByUserAndPost(user, post)
                .orElse(null);

        if (existingRepost == null) {
            Repost newRepost = new Repost(post, user);
            post.addRepost(newRepost);
            repostRepository.save(newRepost);

            notificationService.createNotification(
                    post.getUser().getId(),
                    "POST_REPOST",
                    userId,
                    postId,
                    null
            );

            return repostMapper.toResponseDTO("Post republicado com sucesso!", newRepost);
        } else {
            post.removeRepost(existingRepost);
            repostRepository.delete(existingRepost);
            return repostMapper.toResponseDTO("Repost removido com sucesso!", existingRepost);
        }
    }
}