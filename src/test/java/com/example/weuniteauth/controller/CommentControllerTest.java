package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.CommentDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.comment.CommentRequestDTO;
import com.example.weuniteauth.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @Test
    void shouldCreateComment() {
        ResponseDTO<CommentDTO> response = new ResponseDTO<>("ok", null);
        when(commentService.createComment(anyLong(), anyLong(), any(CommentRequestDTO.class))).thenReturn(response);

        ResponseEntity<ResponseDTO<CommentDTO>> entity = commentController.createComment(1L, 2L, new CommentRequestDTO("text", null));
        assertThat(entity.getBody()).isEqualTo(response);
    }

    @Test
    void shouldGetComments() {
        when(commentService.getCommentsByPost(2L)).thenReturn(List.of());
        when(commentService.getCommentsByUser(1L)).thenReturn(List.of());

        assertThat(commentController.getCommentsByPost(2L).getBody()).isEmpty();
        assertThat(commentController.getCommentsByUser(1L).getBody()).isEmpty();
    }
}

