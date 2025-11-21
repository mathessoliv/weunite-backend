package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.post.Comment;
import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.dto.comment.CommentRequestDTO;
import com.example.weuniteauth.repository.CommentRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.RoleRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import com.example.weuniteauth.service.NotificationService;
import com.example.weuniteauth.service.cloudinary.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CloudinaryService cloudinaryService;

    @MockitoBean
    private NotificationService notificationService;

    private Athlete postOwner;
    private Athlete commenter;
    private Post post;

    @BeforeEach
    void setUp() {
        postOwner = persistAthlete("owner_" + System.nanoTime());
        commenter = persistAthlete("commenter_" + System.nanoTime());
        post = postRepository.save(new Post(postOwner, "Highlight reel"));
    }

    @Test
    void createCommentShouldPersistAndNotifyOwner() throws Exception {
        CommentRequestDTO request = new CommentRequestDTO("Incrível jogada!", null);

        mockMvc.perform(
                        post("/api/comment/create")
                                .param("userId", commenter.getId().toString())
                                .param("postId", post.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.text").value("Incrível jogada!"));

        assertThat(commentRepository.count()).isEqualTo(1);
        Mockito.verify(notificationService).createNotification(
                postOwner.getId(),
                "POST_COMMENT",
                commenter.getId(),
                post.getId(),
                null
        );
    }

    @Test
    void getCommentsByPostShouldReturnSavedComments() throws Exception {
        persistComment(commenter, post, "Assistência perfeita");

        mockMvc.perform(get("/api/comment/get/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Assistência perfeita"));
    }

    @Test
    void updateCommentShouldRefreshText() throws Exception {
        Comment comment = persistComment(commenter, post, "Primeira versão");

        CommentRequestDTO update = new CommentRequestDTO("Edição final", null);
        MockMultipartFile commentPart = new MockMultipartFile(
                "comment",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(update)
        );

        mockMvc.perform(
                        multipart("/api/comment/update/{userId}/{commentId}", commenter.getId(), comment.getId())
                                .file(commentPart)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(req -> {
                                    req.setMethod("PUT");
                                    return req;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.text").value("Edição final"));

        Comment refreshed = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(refreshed.getText()).isEqualTo("Edição final");
    }

    @Test
    void deleteCommentShouldRemoveEntity() throws Exception {
        Comment comment = persistComment(commenter, post, "Para ser excluído");

        mockMvc.perform(delete("/api/comment/delete/{userId}/{commentId}", commenter.getId(), comment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comentário excluída com sucesso"));

        assertThat(commentRepository.existsById(comment.getId())).isFalse();
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

    private Comment persistComment(Athlete author, Post targetPost, String text) {
        return commentRepository.save(new Comment(author, targetPost, text, null));
    }
}

