package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.FollowDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {

    @Mock
    private FollowService followService;

    @InjectMocks
    private FollowController followController;

    private ResponseDTO<FollowDTO> followResponse;

    @BeforeEach
    void setUp() {
        followResponse = new ResponseDTO<>("ok", null);
    }

    @Test
    void shouldFollowAndUnfollow() {
        when(followService.followAndUnfollow(anyLong(), anyLong())).thenReturn(followResponse);
        ResponseEntity<ResponseDTO<FollowDTO>> response = followController.followAndUnfollow(1L, 2L);
        assertThat(response.getBody()).isEqualTo(followResponse);
    }

    @Test
    void shouldGetFollow() {
        FollowDTO dto = new FollowDTO(1L, null, null, "ACCEPTED", null, null);
        when(followService.getFollow(anyLong(), anyLong())).thenReturn(dto);
        assertThat(followController.getFollow(1L, 2L).getBody()).isEqualTo(dto);
    }

    @Test
    void shouldGetFollowersAndFollowing() {
        ResponseDTO<List<FollowDTO>> listResponse = new ResponseDTO<>("ok", List.of());
        when(followService.getFollowers(anyLong())).thenReturn(listResponse);
        when(followService.getFollowing(anyLong())).thenReturn(listResponse);

        assertThat(followController.getFollowers(5L).getBody()).isEqualTo(listResponse);
        assertThat(followController.getFollowing(5L).getBody()).isEqualTo(listResponse);
    }

    @Test
    void shouldAcceptAndDecline() {
        when(followService.acceptFollowRequest(anyLong(), anyLong())).thenReturn(followResponse);
        when(followService.declineFollowRequest(anyLong(), anyLong())).thenReturn(followResponse);

        assertThat(followController.acceptFollowRequest(1L, 2L).getBody()).isEqualTo(followResponse);
        assertThat(followController.declineFollowRequest(1L, 2L).getBody()).isEqualTo(followResponse);
    }
}

