package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.post.PostRequestDTO;
import com.example.weuniteauth.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Test
    void shouldCreatePost() {
        ResponseDTO<PostDTO> dto = new ResponseDTO<>("ok", null);
        when(postService.createPost(anyLong(), any(PostRequestDTO.class), any())).thenReturn(dto);

        ResponseEntity<ResponseDTO<PostDTO>> response = postController.createPost(1L,
                new PostRequestDTO("text"), new MockMultipartFile("image", new byte[0]));
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void shouldGetPost() {
        ResponseDTO<PostDTO> dto = new ResponseDTO<>("ok", null);
        when(postService.getPost(1L)).thenReturn(dto);
        when(postService.getPosts()).thenReturn(List.of());

        assertThat(postController.getPost(1L).getBody()).isEqualTo(dto);
        assertThat(postController.getPosts().getBody()).isEmpty();
    }
}

