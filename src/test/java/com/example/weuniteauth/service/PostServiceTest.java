package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.post.PostRequestDTO;
import com.example.weuniteauth.mapper.PostMapper;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.UserRepository;
import com.example.weuniteauth.service.cloudinary.CloudinaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("Should create a post with image successfully")
    void createPostWithImageSuccessfully() {

        PostRequestDTO postRequest = new PostRequestDTO("This is a test post");
        MultipartFile image = mock(MultipartFile.class);

        User mockUser = new User();
        mockUser.setId(1L);

        Post createdPost = new Post(mockUser, postRequest.text(), "http://image.url/test.jpg");
        PostDTO postDTO = mock(PostDTO.class);

        ResponseDTO<PostDTO> expectedResponse = new ResponseDTO<>("Publicação criada com sucesso!", postDTO);

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(image.isEmpty()).thenReturn(false);
        when(cloudinaryService.uploadPost(image, mockUser.getId())).thenReturn("http://image.url/test.jpg");
        when(postRepository.save(any(Post.class))).thenReturn(createdPost);
        when(postMapper.toResponseDTO(anyString(), any(Post.class))).thenReturn(expectedResponse);

        ResponseDTO<PostDTO> result = postService.createPost(mockUser.getId(), postRequest, image);

        assertEquals("Publicação criada com sucesso!", result.message());
        assertEquals(postDTO, result.data());
    }

    @Test
    @DisplayName("Should create a post without image successfully")
    void  createPostWithoutImageSuccessfully() {

        PostRequestDTO postRequest = new PostRequestDTO("This is a test post");

        User mockUser = new User();
        mockUser.setId(1L);

        Post createdPost = new Post(mockUser, postRequest.text(), null);
        PostDTO postDTO = mock(PostDTO.class);

        ResponseDTO<PostDTO> expectedResponse = new ResponseDTO<>("Publicação criada com sucesso!", postDTO);

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

    }
}
