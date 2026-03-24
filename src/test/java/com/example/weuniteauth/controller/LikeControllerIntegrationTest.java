package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.repository.LikeRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.RoleRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import com.example.weuniteauth.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class LikeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @MockitoBean
    private NotificationService notificationService;

    private Athlete persistAthlete(String username) {
        Athlete athlete = new Athlete("Athlete " + username, username, username + "@mail.com", "Secret#123");
        athlete.setEmailVerified(true);
        Role role = roleRepository.findByName("ATHLETE");
        if (role == null) {
            role = new Role();
            role.setName("ATHLETE");
            roleRepository.save(role);
        }
        athlete.setRole(new HashSet<>(Collections.singleton(role)));
        return (Athlete) userRepository.save(athlete);
    }

    private Post persistPost(Athlete owner, String text) {
        Post post = new Post(owner, text);
        return postRepository.save(post);
    }

    @Test
    void toggleLikeShouldAddAndRemove() throws Exception {
        Athlete liker = persistAthlete("liker_" + System.nanoTime());
        Post post = persistPost(liker, "Initial text");

        mockMvc.perform(post("/api/likes/toggleLike/{userId}/{postId}", liker.getId(), post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Curtida criada com sucesso!"));

        assertThat(likeRepository.count()).isEqualTo(1);

        mockMvc.perform(post("/api/likes/toggleLike/{userId}/{postId}", liker.getId(), post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Curtida deletada com sucesso!"));

        assertThat(likeRepository.count()).isEqualTo(0);
    }

    @Test
    void getLikesPageShouldReturnPersistedLikes() throws Exception {
        Athlete liker = persistAthlete("likes_user_" + System.nanoTime());
        Post post = persistPost(liker, "Text");

        mockMvc.perform(post("/api/likes/toggleLike/{userId}/{postId}", liker.getId(), post.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/likes/get/{userId}/page", liker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].post.id").value(post.getId().toString()));
    }
}
