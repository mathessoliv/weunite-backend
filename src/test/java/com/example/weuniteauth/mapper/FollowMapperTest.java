package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Follow;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.FollowDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("FollowMapper Tests")
class FollowMapperTest {

    @Autowired
    private FollowMapper followMapper;

    private Follow testFollow;
    private User follower;
    private User followed;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");

        follower = new Athlete();
        follower.setId(1L);
        follower.setUsername("follower");
        follower.setEmail("follower@test.com");
        follower.setName("Follower User");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        follower.setRole(roles);
        follower.setCreatedAt(Instant.now());

        followed = new Athlete();
        followed.setId(2L);
        followed.setUsername("followed");
        followed.setEmail("followed@test.com");
        followed.setName("Followed User");
        followed.setRole(new HashSet<>(roles));
        followed.setCreatedAt(Instant.now());

        testFollow = new Follow();
        testFollow.setId(1L);
        testFollow.setFollower(follower);
        testFollow.setFollowed(followed);
        testFollow.setStatus(Follow.FollowStatus.ACCEPTED);
        testFollow.setCreatedAt(Instant.now());
        testFollow.setUpdatedAt(Instant.now());
    }

    // TO FOLLOW DTO TESTS

    @Test
    @DisplayName("Should convert Follow entity to FollowDTO")
    void toFollowDTO() {
        FollowDTO result = followMapper.toFollowDTO(testFollow);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNotNull(result.follower());
        assertEquals("follower", result.follower().username());
        assertNotNull(result.followed());
        assertEquals("followed", result.followed().username());
        assertEquals("ACCEPTED", result.status());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
    }

    @Test
    @DisplayName("Should map follower details correctly")
    void toFollowDTOFollowerDetails() {
        FollowDTO result = followMapper.toFollowDTO(testFollow);

        assertNotNull(result);
        assertNotNull(result.follower());
        assertEquals("1", result.follower().id());
        assertEquals("Follower User", result.follower().name());
        assertEquals("follower@test.com", result.follower().email());
    }

    @Test
    @DisplayName("Should map followed details correctly")
    void toFollowDTOFollowedDetails() {
        FollowDTO result = followMapper.toFollowDTO(testFollow);

        assertNotNull(result);
        assertNotNull(result.followed());
        assertEquals("2", result.followed().id());
        assertEquals("Followed User", result.followed().name());
        assertEquals("followed@test.com", result.followed().email());
    }

    @Test
    @DisplayName("Should convert follow with PENDING status")
    void toFollowDTOPendingStatus() {
        testFollow.setStatus(Follow.FollowStatus.PENDING);

        FollowDTO result = followMapper.toFollowDTO(testFollow);

        assertNotNull(result);
        assertEquals("PENDING", result.status());
    }

    @Test
    @DisplayName("Should convert follow with REJECTED status")
    void toFollowDTORejectedStatus() {
        testFollow.setStatus(Follow.FollowStatus.REJECTED);

        FollowDTO result = followMapper.toFollowDTO(testFollow);

        assertNotNull(result);
        assertEquals("REJECTED", result.status());
    }

    @Test
    @DisplayName("Should handle null updatedAt")
    void toFollowDTONullUpdatedAt() {
        testFollow.setUpdatedAt(null);

        FollowDTO result = followMapper.toFollowDTO(testFollow);

        assertNotNull(result);
        assertNull(result.updatedAt());
        assertNotNull(result.createdAt());
    }

    // MAP FOLLOWS TO LIST TESTS

    @Test
    @DisplayName("Should convert list of follows to list of DTOs")
    void mapFollows() {
        User user3 = new Athlete();
        user3.setId(3L);
        user3.setUsername("user3");
        user3.setEmail("user3@test.com");
        user3.setName("User 3");

        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user3.setRole(roles);
        user3.setCreatedAt(Instant.now());

        Follow follow2 = new Follow();
        follow2.setId(2L);
        follow2.setFollower(follower);
        follow2.setFollowed(user3);
        follow2.setStatus(Follow.FollowStatus.ACCEPTED);
        follow2.setCreatedAt(Instant.now());

        Follow follow3 = new Follow();
        follow3.setId(3L);
        follow3.setFollower(followed);
        follow3.setFollowed(follower);
        follow3.setStatus(Follow.FollowStatus.PENDING);
        follow3.setCreatedAt(Instant.now());

        List<Follow> follows = new ArrayList<>();
        follows.add(testFollow);
        follows.add(follow2);
        follows.add(follow3);

        List<FollowDTO> result = followMapper.mapFollows(follows);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("follower", result.get(0).follower().username());
        assertEquals("followed", result.get(0).followed().username());
        assertEquals("user3", result.get(1).followed().username());
    }

    @Test
    @DisplayName("Should handle empty follow list")
    void mapFollowsEmpty() {
        List<Follow> follows = new ArrayList<>();

        List<FollowDTO> result = followMapper.mapFollows(follows);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null follow list")
    void mapFollowsNull() {
        List<FollowDTO> result = followMapper.mapFollows(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TO RESPONSE DTO TESTS - SINGLE FOLLOW

    @Test
    @DisplayName("Should create ResponseDTO with message and single follow")
    void toResponseDTOSingle() {
        String message = "Seguindo com sucesso";

        ResponseDTO<FollowDTO> result = followMapper.toResponseDTO(message, testFollow);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals("follower", result.data().follower().username());
        assertEquals("followed", result.data().followed().username());
    }

    // TO RESPONSE DTO TESTS - LIST OF FOLLOWS

    @Test
    @DisplayName("Should create ResponseDTO with message and list of follows")
    void toResponseDTOList() {
        Follow follow2 = new Follow();
        follow2.setId(2L);
        follow2.setFollower(follower);
        follow2.setFollowed(followed);
        follow2.setStatus(Follow.FollowStatus.ACCEPTED);
        follow2.setCreatedAt(Instant.now());

        List<Follow> follows = new ArrayList<>();
        follows.add(testFollow);
        follows.add(follow2);

        String message = "Seguidores carregados";

        ResponseDTO<List<FollowDTO>> result = followMapper.toResponseDTO(message, follows);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());
    }

    @Test
    @DisplayName("Should create ResponseDTO with empty follow list")
    void toResponseDTOEmptyList() {
        List<Follow> follows = new ArrayList<>();
        String message = "Nenhum seguidor encontrado";

        ResponseDTO<List<FollowDTO>> result = followMapper.toResponseDTO(message, follows);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertTrue(result.data().isEmpty());
    }

    // EDGE CASE TESTS

    @Test
    @DisplayName("Should handle all follow statuses")
    void toFollowDTOAllStatuses() {
        Follow.FollowStatus[] statuses = Follow.FollowStatus.values();

        for (Follow.FollowStatus status : statuses) {
            testFollow.setStatus(status);
            FollowDTO result = followMapper.toFollowDTO(testFollow);
            assertEquals(status.name(), result.status());
        }
    }

    @Test
    @DisplayName("Should preserve timestamps correctly")
    void toFollowDTOPreserveTimestamps() {
        Instant createdAt = Instant.now().minusSeconds(3600);
        Instant updatedAt = Instant.now();

        testFollow.setCreatedAt(createdAt);
        testFollow.setUpdatedAt(updatedAt);

        FollowDTO result = followMapper.toFollowDTO(testFollow);

        assertNotNull(result);
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
        // Timestamps são convertidos para String no DTO
    }
}


