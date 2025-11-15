package com.example.weuniteauth.service.user;

import com.example.weuniteauth.domain.users.Follow;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.FollowDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.FollowMapper;
import com.example.weuniteauth.repository.FollowRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import com.example.weuniteauth.service.FollowService;
import com.example.weuniteauth.service.NotificationService;
import com.example.weuniteauth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowService Tests")
class FollowServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private FollowMapper followMapper;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FollowService followService;

    private User follower;
    private User followed;
    private Follow follow;
    private FollowDTO followDTO;

    @BeforeEach
    void setUp() {
        follower = new User();
        follower.setId(1L);
        follower.setUsername("follower");
        follower.setEmail("follower@example.com");

        followed = new User();
        followed.setId(2L);
        followed.setUsername("followed");
        followed.setEmail("followed@example.com");

        follow = new Follow(follower, followed);
        follow.setId(1L);
        follow.setCreatedAt(Instant.now());
        follow.setStatus(Follow.FollowStatus.ACCEPTED);

        UserDTO followerDTO = new UserDTO("1", "Follower", "follower", "BASIC", null, "follower@example.com", null, null, false, Instant.now(), null);
        UserDTO followedDTO = new UserDTO("2", "Followed", "followed", "BASIC", null, "followed@example.com", null, null, false, Instant.now(), null);

        followDTO = new FollowDTO(
                1L,
                followerDTO,
                followedDTO,
                "ACCEPTED",
                Instant.now().toString(),
                null
        );
    }

    // FOLLOW USER TESTS

    @Test
    @DisplayName("Should follow user successfully and create notification")
    void followUserSuccess() {
        ResponseDTO<FollowDTO> expectedResponse = new ResponseDTO<>("Seguiu com sucesso", followDTO);

        when(followRepository.save(any(Follow.class))).thenReturn(follow);
        when(notificationService.createNotification(anyLong(), anyString(), anyLong(), anyLong(), any()))
                .thenReturn(null);
        when(followMapper.toResponseDTO(eq("Seguiu com sucesso"), any(Follow.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<FollowDTO> result = followService.followUser(follower, followed);

        assertNotNull(result);
        assertEquals("Seguiu com sucesso", result.message());

        verify(followRepository).save(any(Follow.class));
        verify(notificationService).createNotification(eq(2L), eq("NEW_FOLLOWER"), eq(1L), eq(2L), isNull());
    }

    // UNFOLLOW USER TESTS

    @Test
    @DisplayName("Should unfollow user successfully")
    void unfollowUserSuccess() {
        ResponseDTO<FollowDTO> expectedResponse = new ResponseDTO<>("Deixou de seguir com sucesso", followDTO);

        doNothing().when(followRepository).delete(follow);
        when(followMapper.toResponseDTO(eq("Deixou de seguir com sucesso"), any(Follow.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<FollowDTO> result = followService.unfollowUser(follow);

        assertNotNull(result);
        assertEquals("Deixou de seguir com sucesso", result.message());

        verify(followRepository).delete(follow);
    }

    // FOLLOW AND UNFOLLOW TOGGLE TESTS

    @Test
    @DisplayName("Should create follow when not already following")
    void followAndUnfollowCreateFollow() {
        ResponseDTO<FollowDTO> expectedResponse = new ResponseDTO<>("Seguiu com sucesso", followDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followed));
        when(followRepository.findByFollowerIdAndFollowedId(1L, 2L)).thenReturn(Optional.empty());
        when(notificationService.createNotification(anyLong(), anyString(), anyLong(), anyLong(), any()))
                .thenReturn(null);
        when(notificationService.createNotification(anyLong(), anyString(), anyLong(), anyLong(), any())).thenReturn(null);
        when(followMapper.toResponseDTO(eq("Seguiu com sucesso"), any(Follow.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<FollowDTO> result = followService.followAndUnfollow(1L, 2L);

        assertNotNull(result);
        assertEquals("Seguiu com sucesso", result.message());

        verify(followRepository).save(any(Follow.class));
    }

    @Test
    @DisplayName("Should remove follow when already following")
    void followAndUnfollowRemoveFollow() {
        ResponseDTO<FollowDTO> expectedResponse = new ResponseDTO<>("Deixou de seguir com sucesso", followDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followed));
        when(followRepository.findByFollowerIdAndFollowedId(1L, 2L)).thenReturn(Optional.of(follow));
        doNothing().when(followRepository).delete(follow);
        when(followMapper.toResponseDTO(eq("Deixou de seguir com sucesso"), any(Follow.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<FollowDTO> result = followService.followAndUnfollow(1L, 2L);

        assertNotNull(result);
        assertEquals("Deixou de seguir com sucesso", result.message());

        verify(followRepository).delete(follow);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when follower does not exist")
    void followAndUnfollowFollowerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                followService.followAndUnfollow(1L, 2L)
        );

        verify(userRepository).findById(1L);
        verify(followRepository, never()).save(any(Follow.class));
    }

    // GET FOLLOWERS TESTS

    @Test
    @DisplayName("Should get followers successfully")
    void getFollowersSuccess() {
        List<Follow> follows = Arrays.asList(follow);
        List<FollowDTO> followDTOs = Arrays.asList(followDTO);
        ResponseDTO<List<FollowDTO>> expectedResponse = new ResponseDTO<>(
                "Seguidores consultados com sucesso!",
                followDTOs
        );

        when(userRepository.findById(2L)).thenReturn(Optional.of(followed));
        when(followRepository.findAllByFollowedAndStatus(followed, Follow.FollowStatus.ACCEPTED))
                .thenReturn(follows);
        when(followMapper.toResponseDTO(eq("Seguidores consultados com sucesso!"), eq(follows)))
                .thenReturn(expectedResponse);

        ResponseDTO<List<FollowDTO>> result = followService.getFollowers(2L);

        assertNotNull(result);
        assertEquals("Seguidores consultados com sucesso!", result.message());

        verify(userRepository).findById(2L);
        verify(followRepository).findAllByFollowedAndStatus(followed, Follow.FollowStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Should get following successfully")
    void getFollowingSuccess() {
        List<Follow> follows = Arrays.asList(follow);
        List<FollowDTO> followDTOs = Arrays.asList(followDTO);
        ResponseDTO<List<FollowDTO>> expectedResponse = new ResponseDTO<>(
                "Seguindo consultados com sucesso!",
                followDTOs
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(followRepository.findAllByFollowerAndStatus(follower, Follow.FollowStatus.ACCEPTED))
                .thenReturn(follows);
        when(followMapper.toResponseDTO(eq("Seguindo consultados com sucesso!"), eq(follows)))
                .thenReturn(expectedResponse);

        ResponseDTO<List<FollowDTO>> result = followService.getFollowing(1L);

        assertNotNull(result);
        assertEquals("Seguindo consultados com sucesso!", result.message());

        verify(userRepository).findById(1L);
        verify(followRepository).findAllByFollowerAndStatus(follower, Follow.FollowStatus.ACCEPTED);
    }

    // ACCEPT FOLLOW REQUEST TESTS

    @Test
    @DisplayName("Should accept follow request successfully")
    void acceptFollowRequestSuccess() {
        Follow pendingFollow = new Follow(follower, followed);
        pendingFollow.setStatus(Follow.FollowStatus.PENDING);

        ResponseDTO<FollowDTO> expectedResponse = new ResponseDTO<>(
                "Solicitação de seguimento aceita com sucesso!",
                followDTO
        );

        when(followRepository.findByFollowerIdAndFollowedId(1L, 2L)).thenReturn(Optional.of(pendingFollow));
        when(followRepository.save(any(Follow.class))).thenReturn(pendingFollow);
        when(followMapper.toResponseDTO(eq("Solicitação de seguimento aceita com sucesso!"), any(Follow.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<FollowDTO> result = followService.acceptFollowRequest(1L, 2L);

        assertNotNull(result);
        assertEquals("Solicitação de seguimento aceita com sucesso!", result.message());
        assertEquals(Follow.FollowStatus.ACCEPTED, pendingFollow.getStatus());

        verify(followRepository).findByFollowerIdAndFollowedId(1L, 2L);
        verify(followRepository).save(pendingFollow);
    }

    @Test
    @DisplayName("Should decline follow request successfully")
    void declineFollowRequestSuccess() {
        Follow pendingFollow = new Follow(follower, followed);
        pendingFollow.setStatus(Follow.FollowStatus.PENDING);

        ResponseDTO<FollowDTO> expectedResponse = new ResponseDTO<>(
                "Solicitação de seguimento recusada com sucesso!",
                followDTO
        );

        when(followRepository.findByFollowerIdAndFollowedId(1L, 2L)).thenReturn(Optional.of(pendingFollow));
        when(followRepository.save(any(Follow.class))).thenReturn(pendingFollow);
        when(followMapper.toResponseDTO(eq("Solicitação de seguimento recusada com sucesso!"), any(Follow.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<FollowDTO> result = followService.declineFollowRequest(1L, 2L);

        assertNotNull(result);
        assertEquals("Solicitação de seguimento recusada com sucesso!", result.message());
        assertEquals(Follow.FollowStatus.REJECTED, pendingFollow.getStatus());

        verify(followRepository).findByFollowerIdAndFollowedId(1L, 2L);
        verify(followRepository).save(pendingFollow);
    }
}

