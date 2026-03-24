package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.repository.FollowRepository;
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
class FollowControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FollowRepository followRepository;

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

    @Test
    void followAndUnfollowShouldPersistChanges() throws Exception {
        Athlete follower = persistAthlete("follower_" + System.nanoTime());
        Athlete followed = persistAthlete("followed_" + System.nanoTime());

        mockMvc.perform(post("/api/follow/followAndUnfollow/{followerId}/{followedId}", follower.getId(), followed.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.follower.username").value(follower.getUsername()));

        assertThat(followRepository.count()).isEqualTo(1);

        mockMvc.perform(post("/api/follow/followAndUnfollow/{followerId}/{followedId}", follower.getId(), followed.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deixou de seguir com sucesso"));

        assertThat(followRepository.count()).isEqualTo(0);
    }

    @Test
    void getFollowersShouldReturnStoredFollow() throws Exception {
        Athlete follower = persistAthlete("follower_list_" + System.nanoTime());
        Athlete followed = persistAthlete("followed_list_" + System.nanoTime());

        // create follow via endpoint
        mockMvc.perform(post("/api/follow/followAndUnfollow/{followerId}/{followedId}", follower.getId(), followed.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/follow/followers/{userId}", followed.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].follower.username").value(follower.getUsername()));
    }
}
