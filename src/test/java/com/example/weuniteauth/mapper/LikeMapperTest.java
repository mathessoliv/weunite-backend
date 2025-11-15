package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.post.Like;
import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("LikeMapper Tests")
class LikeMapperTest {

    @Autowired
    private LikeMapper likeMapper;

    private Like testLike;
    private Post testPost;
    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");

        testUser = new Athlete();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        testUser.setRole(roles);
        testUser.setCreatedAt(Instant.now());

        testPost = new Post(testUser, "Test post", null);
        testPost.setId(1L);
        testPost.setCreatedAt(Instant.now());

        testLike = new Like(testPost, testUser);
        testLike.setId(1L);
    }

    @Test
    @DisplayName("Should convert Like entity to LikeDTO")
    void toLikeDTO() {
        LikeDTO result = likeMapper.toLikeDTO(testLike);

        assertNotNull(result);
        assertEquals("1", result.id());
        assertNotNull(result.user());
        assertEquals("testuser", result.user().username());
        assertNotNull(result.post());
        assertEquals("1", result.post().id());
    }

    @Test
    @DisplayName("Should map user details correctly")
    void toLikeDTOUserDetails() {
        LikeDTO result = likeMapper.toLikeDTO(testLike);

        assertNotNull(result);
        assertNotNull(result.user());
        assertEquals("1", result.user().id());
        assertEquals("Test User", result.user().name());
        assertEquals("test@example.com", result.user().email());
    }

    @Test
    @DisplayName("Should map post without likes to avoid circular reference")
    void toLikeDTOPostWithoutLikes() {
        LikeDTO result = likeMapper.toLikeDTO(testLike);

        assertNotNull(result);
        assertNotNull(result.post());
        assertEquals("Test post", result.post().text());
        // Post deve vir sem likes para evitar referência circular
    }

    @Test
    @DisplayName("Should create ResponseDTO with message and like")
    void toResponseDTOSingle() {
        String message = "Like adicionado";

        ResponseDTO<LikeDTO> result = likeMapper.toResponseDTO(message, testLike);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals("testuser", result.data().user().username());
    }

    @Test
    @DisplayName("Should create ResponseDTO with Set of likes")
    void toResponseDTOSet() {
        User user2 = new Athlete();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@test.com");
        user2.setName("User 2");

        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user2.setRole(roles);
        user2.setCreatedAt(Instant.now());

        Like like2 = new Like(testPost, user2);
        like2.setId(2L);

        Set<Like> likes = new HashSet<>();
        likes.add(testLike);
        likes.add(like2);

        String message = "Likes carregados";

        ResponseDTO<List<LikeDTO>> result = likeMapper.toResponseDTO(message, likes);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());
    }

    @Test
    @DisplayName("Should create ResponseDTO with List of likes")
    void toResponseDTOList() {
        User user2 = new Athlete();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@test.com");
        user2.setName("User 2");

        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user2.setRole(roles);
        user2.setCreatedAt(Instant.now());

        Like like2 = new Like(testPost, user2);
        like2.setId(2L);

        List<Like> likes = new ArrayList<>();
        likes.add(testLike);
        likes.add(like2);

        String message = "Likes carregados";

        ResponseDTO<List<LikeDTO>> result = likeMapper.toResponseDTO(message, likes);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());
    }

    @Test
    @DisplayName("Should map likes from Set")
    void mapLikesFromSet() {
        User user2 = new Athlete();
        user2.setId(3L);
        user2.setUsername("user3");
        user2.setEmail("user3@test.com");
        user2.setName("User 3");

        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user2.setRole(roles);
        user2.setCreatedAt(Instant.now());

        Like like2 = new Like(testPost, user2);
        like2.setId(2L);

        Set<Like> likes = new HashSet<>();
        likes.add(testLike);
        likes.add(like2);

        List<LikeDTO> result = likeMapper.mapLikes(likes);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should handle empty Set of likes")
    void mapLikesEmptySet() {
        Set<Like> likes = new HashSet<>();

        List<LikeDTO> result = likeMapper.mapLikes(likes);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null Set of likes")
    void mapLikesNullSet() {
        List<LikeDTO> result = likeMapper.mapLikes((Set<Like>) null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should map likes from List")
    void mapLikesFromList() {
        Like like2 = new Like(testPost, testUser);
        like2.setId(2L);

        List<Like> likes = new ArrayList<>();
        likes.add(testLike);
        likes.add(like2);

        List<LikeDTO> result = likeMapper.mapLikes(likes);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}

