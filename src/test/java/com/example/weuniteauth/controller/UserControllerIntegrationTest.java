package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.repository.RoleRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role athleteRole;

    @BeforeEach
    void setUp() {
        athleteRole = ensureRoleExists("ATHLETE");
    }

    @Test
    void getUserByUsernameShouldReturnPersistedUser() throws Exception {
        Athlete athlete = persistAthlete("playmaker_renan", "Renan Playmaker");

        mockMvc.perform(get("/api/user/username/{username}", athlete.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(athlete.getUsername()))
                .andExpect(jsonPath("$.data.email").value(athlete.getEmail()))
                .andExpect(jsonPath("$.data.role").value("ATHLETE"));
    }

    @Test
    void searchUsersShouldReturnOnlyVerifiedMatches() throws Exception {
        persistAthlete("ana_dev", "Ana Dev");
        persistAthlete("bruno_dev", "Bruno Dev");
        Athlete hidden = persistAthlete("hidden_dev", "Hidden Dev");
        hidden.setEmailVerified(false);
        userRepository.save(hidden);

        mockMvc.perform(get("/api/user/search").param("query", "dev"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].username", containsInAnyOrder("ana_dev", "bruno_dev")));
    }

    @Test
    void deleteUserShouldRemoveUser() throws Exception {
        Athlete athlete = persistAthlete("temporary_user", "Temporary User");

        mockMvc.perform(delete("/api/user/delete/{username}", athlete.getUsername()))
                .andExpect(status().isOk());

        assertThat(userRepository.findByUsername(athlete.getUsername())).isEmpty();
    }

    private Athlete persistAthlete(String username, String name) {
        Athlete athlete = new Athlete(name, username, username + "@test.com", "secret123");
        athlete.setEmailVerified(true);
        athlete.setRole(new HashSet<>(Collections.singleton(athleteRole)));
        return (Athlete) userRepository.save(athlete);
    }

    private Role ensureRoleExists(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            role = roleRepository.save(role);
        }
        return role;
    }
}

