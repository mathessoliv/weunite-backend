package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.post.Like;
import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.PostDTO;
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
@DisplayName("PostMapper Tests")
class PostMapperTest {

    @Autowired
    private PostMapper postMapper;

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

        testPost = new Post(testUser, "This is a test post", null);
        testPost.setId(1L);
        testPost.setCreatedAt(Instant.now());
        testPost.setUpdatedAt(Instant.now());
    }

    // TO POST DTO TESTS

    @Test
    @DisplayName("Should convert Post entity to PostDTO")
    void toPostDTO() {
        PostDTO result = postMapper.toPostDTO(testPost);

        assertNotNull(result);
        assertEquals("1", result.id());
        assertEquals("This is a test post", result.text());
        assertNull(result.imageUrl());
        assertNotNull(result.user());
        assertEquals("testuser", result.user().username());
        assertNotNull(result.createdAt());
    }

    @Test
    @DisplayName("Should convert post with image URL")
    void toPostDTOWithImage() {
        testPost.setImageUrl("http://image.url/post.jpg");

        PostDTO result = postMapper.toPostDTO(testPost);

        assertNotNull(result);
        assertEquals("http://image.url/post.jpg", result.imageUrl());
    }

    @Test
    @DisplayName("Should convert post with likes")
    void toPostDTOWithLikes() {
        User liker = new Athlete();
        liker.setId(2L);
        liker.setUsername("liker");

        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        liker.setRole(roles);
        liker.setCreatedAt(Instant.now());

        Like like = new Like(testPost, liker);
        like.setId(1L);

        testPost.addLike(like);

        PostDTO result = postMapper.toPostDTO(testPost);

        assertNotNull(result);
        assertNotNull(result.likes());
        assertEquals(1, result.likes().size());
        assertEquals("1", result.likes().get(0).id());
    }

    @Test
    @DisplayName("Should handle post with empty likes")
    void toPostDTOEmptyLikes() {
        PostDTO result = postMapper.toPostDTO(testPost);

        assertNotNull(result);
        assertNotNull(result.likes());
        assertTrue(result.likes().isEmpty());
    }

    // TO POST DTO LIST TESTS

    @Test
    @DisplayName("Should convert list of posts to list of DTOs")
    void toPostDTOList() {
        Post post2 = new Post(testUser, "Second post", "http://image.url/post2.jpg");
        post2.setId(2L);
        post2.setCreatedAt(Instant.now());

        List<Post> posts = new ArrayList<>();
        posts.add(testPost);
        posts.add(post2);

        List<PostDTO> result = postMapper.toPostDTOList(posts);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("This is a test post", result.get(0).text());
        assertEquals("Second post", result.get(1).text());
    }

    @Test
    @DisplayName("Should handle empty post list")
    void toPostDTOListEmpty() {
        List<Post> posts = new ArrayList<>();

        List<PostDTO> result = postMapper.toPostDTOList(posts);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null post list")
    void toPostDTOListNull() {
        List<PostDTO> result = postMapper.toPostDTOList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TO RESPONSE DTO TESTS

    @Test
    @DisplayName("Should create ResponseDTO with message and post")
    void toResponseDTO() {
        String message = "Post criado com sucesso";

        ResponseDTO<PostDTO> result = postMapper.toResponseDTO(message, testPost);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals("This is a test post", result.data().text());
    }

    @Test
    @DisplayName("Should create ResponseDTO for post with image")
    void toResponseDTOWithImage() {
        testPost.setImageUrl("http://image.url/post.jpg");
        String message = "Post com imagem criado";

        ResponseDTO<PostDTO> result = postMapper.toResponseDTO(message, testPost);

        assertNotNull(result);
        assertEquals("http://image.url/post.jpg", result.data().imageUrl());
    }

    // MAP LIKES WITHOUT POST TESTS

    @Test
    @DisplayName("Should map likes without post reference")
    void mapLikesWithoutPost() {
        User liker1 = new Athlete();
        liker1.setId(2L);
        liker1.setUsername("liker1");

        User liker2 = new Athlete();
        liker2.setId(3L);
        liker2.setUsername("liker2");

        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        liker1.setRole(roles);
        liker2.setRole(new HashSet<>(roles));
        liker1.setCreatedAt(Instant.now());
        liker2.setCreatedAt(Instant.now());

        Like like1 = new Like(testPost, liker1);
        like1.setId(1L);
        Like like2 = new Like(testPost, liker2);
        like2.setId(2L);

        Set<Like> likes = new HashSet<>();
        likes.add(like1);
        likes.add(like2);

        testPost.setLikes(likes);

        PostDTO result = postMapper.toPostDTO(testPost);

        assertNotNull(result);
        assertNotNull(result.likes());
        assertEquals(2, result.likes().size());

        // Verificar que não há referência circular ao post
        result.likes().forEach(likeDTO -> {
            assertNull(likeDTO.post());
        });
    }

    @Test
    @DisplayName("Should handle null likes set")
    void mapLikesNull() {
        testPost.setLikes(null);

        PostDTO result = postMapper.toPostDTO(testPost);

        assertNotNull(result);
        assertNotNull(result.likes());
        assertTrue(result.likes().isEmpty());
    }
}

