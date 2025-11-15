package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.post.Comment;
import com.example.weuniteauth.domain.post.Like;
import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.exceptions.comment.CommentNotFoundException;
import com.example.weuniteauth.exceptions.post.PostNotFoundException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.LikeMapper;
import com.example.weuniteauth.repository.CommentRepository;
import com.example.weuniteauth.repository.LikeRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeService Tests")
class LikeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LikeService likeService;

    private User user;
    private User postOwner;
    private Post post;
    private Comment comment;
    private Like like;
    private LikeDTO likeDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        postOwner = new User();
        postOwner.setId(2L);
        postOwner.setUsername("postowner");

        post = new Post();
        post.setId(1L);
        post.setText("Test post");
        post.setUser(postOwner);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setUser(postOwner);

        like = new Like(post, user);
        like.setId(1L);

        likeDTO = new LikeDTO("1", null, null);
    }

    // TOGGLE LIKE POST TESTS

    @Test
    @DisplayName("Should create like when post is not liked yet")
    void toggleLikeCreateLike() {
        ResponseDTO<LikeDTO> expectedResponse = new ResponseDTO<>("Curtida criada com sucesso!", likeDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(notificationService.createNotification(anyLong(), anyString(), anyLong(), anyLong(), any())).thenReturn(null);
        when(likeMapper.toResponseDTO(eq("Curtida criada com sucesso!"), any(Like.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<LikeDTO> result = likeService.toggleLike(1L, 1L);

        assertNotNull(result);
        assertEquals("Curtida criada com sucesso!", result.message());

        verify(userRepository).findById(1L);
        verify(postRepository).findById(1L);
        verify(likeRepository).findByUserAndPost(user, post);
        verify(likeRepository).save(any(Like.class));
        verify(notificationService).createNotification(eq(2L), eq("POST_LIKE"), eq(1L), eq(1L), isNull());
        verify(likeMapper).toResponseDTO(eq("Curtida criada com sucesso!"), any(Like.class));
    }

    @Test
    @DisplayName("Should delete like when post is already liked")
    void toggleLikeDeleteLike() {
        ResponseDTO<LikeDTO> expectedResponse = new ResponseDTO<>("Curtida deletada com sucesso!", likeDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(like));
        doNothing().when(likeRepository).delete(like);
        when(likeMapper.toResponseDTO(eq("Curtida deletada com sucesso!"), any(Like.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<LikeDTO> result = likeService.toggleLike(1L, 1L);

        assertNotNull(result);
        assertEquals("Curtida deletada com sucesso!", result.message());

        verify(userRepository).findById(1L);
        verify(postRepository).findById(1L);
        verify(likeRepository).findByUserAndPost(user, post);
        verify(likeRepository).delete(like);
        verify(notificationService, never()).createNotification(anyLong(), anyString(), anyLong(), anyLong(), any());
        verify(likeMapper).toResponseDTO(eq("Curtida deletada com sucesso!"), any(Like.class));
    }

    @Test
    @DisplayName("Should not create notification when user likes own post")
    void toggleLikeOwnPost() {
        post.setUser(user); // Same user
        ResponseDTO<LikeDTO> expectedResponse = new ResponseDTO<>("Curtida criada com sucesso!", likeDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(likeMapper.toResponseDTO(eq("Curtida criada com sucesso!"), any(Like.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<LikeDTO> result = likeService.toggleLike(1L, 1L);

        assertNotNull(result);
        verify(notificationService, never()).createNotification(anyLong(), anyString(), anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void toggleLikeUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                likeService.toggleLike(999L, 1L)
        );

        verify(userRepository).findById(999L);
        verify(postRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when post does not exist")
    void toggleLikePostNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () ->
                likeService.toggleLike(1L, 999L)
        );

        verify(userRepository).findById(1L);
        verify(postRepository).findById(999L);
        verify(likeRepository, never()).save(any(Like.class));
    }

    // TOGGLE LIKE COMMENT TESTS

    @Test
    @DisplayName("Should create like when comment is not liked yet")
    void toggleLikeCommentCreateLike() {
        Like commentLike = new Like(comment, user);
        commentLike.setId(1L);

        ResponseDTO<LikeDTO> expectedResponse = new ResponseDTO<>("Curtida criada com sucesso!", likeDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeRepository.findByUserAndComment(user, comment)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(commentLike);
        when(notificationService.createNotification(anyLong(), anyString(), anyLong(), anyLong(), any()))
                .thenReturn(null);
        when(likeMapper.toResponseDTO(eq("Curtida criada com sucesso!"), any(Like.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<LikeDTO> result = likeService.toggleLikeComment(1L, 1L);

        assertNotNull(result);
        assertEquals("Curtida criada com sucesso!", result.message());

        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
        verify(likeRepository).findByUserAndComment(user, comment);
        verify(likeRepository).save(any(Like.class));
        verify(notificationService).createNotification(eq(2L), eq("COMMENT_LIKE"), eq(1L), eq(1L), isNull());
    }

    @Test
    @DisplayName("Should delete like when comment is already liked")
    void toggleLikeCommentDeleteLike() {
        Like commentLike = new Like(comment, user);
        commentLike.setId(1L);

        ResponseDTO<LikeDTO> expectedResponse = new ResponseDTO<>("Curtida deletada com sucesso!", likeDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeRepository.findByUserAndComment(user, comment)).thenReturn(Optional.of(commentLike));
        doNothing().when(likeRepository).delete(commentLike);
        when(likeMapper.toResponseDTO(eq("Curtida deletada com sucesso!"), any(Like.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<LikeDTO> result = likeService.toggleLikeComment(1L, 1L);

        assertNotNull(result);
        assertEquals("Curtida deletada com sucesso!", result.message());

        verify(likeRepository).delete(commentLike);
        verify(notificationService, never()).createNotification(anyLong(), anyString(), anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("Should not create notification when user likes own comment")
    void toggleLikeCommentOwnComment() {
        comment.setUser(user); // Same user
        ResponseDTO<LikeDTO> expectedResponse = new ResponseDTO<>("Curtida criada com sucesso!", likeDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeRepository.findByUserAndComment(user, comment)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(likeMapper.toResponseDTO(eq("Curtida criada com sucesso!"), any(Like.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<LikeDTO> result = likeService.toggleLikeComment(1L, 1L);

        assertNotNull(result);
        verify(notificationService, never()).createNotification(anyLong(), anyString(), anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("Should throw CommentNotFoundException when comment does not exist")
    void toggleLikeCommentNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                likeService.toggleLikeComment(1L, 999L)
        );

        verify(commentRepository).findById(999L);
        verify(likeRepository, never()).save(any(Like.class));
    }
}
