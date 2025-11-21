package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.dto.post.PostRequestDTO;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.RoleRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import com.example.weuniteauth.service.cloudinary.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CloudinaryService cloudinaryService;

    private Athlete author;

    @BeforeEach
    void setUp() {
        author = persistAthlete("post_author_" + System.nanoTime());
    }

    @Test
    void createPostShouldPersistEntity() throws Exception {
        PostRequestDTO request = new PostRequestDTO("Opening day announcement");
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(
                        multipart("/api/posts/create/{userId}", author.getId())
                                .file(postPart)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Publicação criada com sucesso!"))
                .andExpect(jsonPath("$.data.text").value("Opening day announcement"));

        assertThat(postRepository.count()).isEqualTo(1);
    }

    @Test
    void updatePostShouldReplaceText() throws Exception {
        Post existing = persistPost("Old caption");

        PostRequestDTO request = new PostRequestDTO("Edited caption");
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(
                        multipart("/api/posts/update/{userId}/{postId}", author.getId(), existing.getId())
                                .file(postPart)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(req -> {
                                    req.setMethod("PUT");
                                    return req;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.text").value("Edited caption"));

        Post refreshed = postRepository.findById(existing.getId()).orElseThrow();
        assertThat(refreshed.getText()).isEqualTo("Edited caption");
    }

    @Test
    void getPostsShouldReturnAllEntries() throws Exception {
        persistPost("First note");
        persistPost("Second note");

        mockMvc.perform(get("/api/posts/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());
    }

    @Test
    void deletePostShouldRemoveFromRepository() throws Exception {
        Post post = persistPost("Temporary");

        mockMvc.perform(delete("/api/posts/delete/{userId}/{postId}", author.getId(), post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Publicação excluída com sucesso"));

        assertThat(postRepository.existsById(post.getId())).isFalse();
    }

    private Athlete persistAthlete(String username) {
        Athlete athlete = new Athlete("Athlete " + username, username, username + "@mail.com", "secret123");
        athlete.setEmailVerified(true);
        Role role = ensureRole("ATHLETE");
        athlete.setRole(new HashSet<>(Collections.singleton(role)));
        return (Athlete) userRepository.save(athlete);
    }

    private Role ensureRole(String value) {
        Role role = roleRepository.findByName(value);
        if (role == null) {
            role = new Role();
            role.setName(value);
            role = roleRepository.save(role);
        }
        return role;
    }

    private Post persistPost(String text) {
        Post post = new Post(author, text);
        return postRepository.save(post);
    }
}
