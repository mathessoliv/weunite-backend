package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private ResponseDTO<LikeDTO> singleResponse;

    @BeforeEach
    void setUp() {
        singleResponse = new ResponseDTO<>("ok", null);
    }

    @Test
    void shouldToggleLike() {
        when(likeService.toggleLike(anyLong(), anyLong())).thenReturn(singleResponse);
        ResponseEntity<ResponseDTO<LikeDTO>> response = likeController.toggleLike(1L, 2L);
        assertThat(response.getBody()).isEqualTo(singleResponse);
    }

    @Test
    void shouldToggleLikeComment() {
        when(likeService.toggleLikeComment(anyLong(), anyLong())).thenReturn(singleResponse);
        assertThat(likeController.toggleLikeComment(1L, 3L).getBody()).isEqualTo(singleResponse);
    }

    @Test
    void shouldReturnLikes() {
        ResponseDTO<List<LikeDTO>> listResponse = new ResponseDTO<>("ok", List.of());
        when(likeService.getLikes(anyLong())).thenReturn(listResponse);
        when(likeService.getLikes(anyLong(), anyInt(), anyInt())).thenReturn(listResponse);
        when(likeService.getCommentLikes(anyLong())).thenReturn(listResponse);

        assertThat(likeController.getLikes(1L).getBody()).isEqualTo(listResponse);
        assertThat(likeController.getLikes(1L, 0, 10).getBody()).isEqualTo(listResponse);
        assertThat(likeController.getCommentLikes(5L).getBody()).isEqualTo(listResponse);
    }
}

